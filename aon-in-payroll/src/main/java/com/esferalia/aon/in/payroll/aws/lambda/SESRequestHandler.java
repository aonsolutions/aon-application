package com.esferalia.aon.in.payroll.aws.lambda;

import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.esferalia.aon.in.payroll.pdf.SalaryPDFParser;
import com.esferalia.aon.in.payroll.pdf.jooq.DSLPDFSalaryBuilder;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public class SESRequestHandler implements RequestHandler<Object, String> {
	
	private static final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";

	
	private static interface Callback {
		void startFile(String filename);
		void endFile(String fileName);
		void exception( String filename , Exception e );
	}
	
	
	private static class MimeCallback implements Callback {
		
		List<String> paragraphs = new LinkedList<String>();
		
		@Override
		public void startFile(String filename) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endFile(String fileName) {
			paragraphs.add(String.format("<p style=\"text-align:justify\">%s...OK</p>\n", fileName));
		}

		@Override
		public void exception(String fileName, Exception e) {
			paragraphs.add(String.format("<p style=\"text-align:justify; color:red\">%s...ERROR (%s)</p>\n", fileName, e.getMessage()));
		}
		
	}
	
	private static interface Handler {
		boolean handle(String contentType, InputStream is, Callback l, Handler handlers []) throws Exception;
	}
	
	
	
	
    @Override
    public String handleRequest(Object input, Context context) {
        //context.getLogger().log("Input: "+ input.getClass().getName() +"," + input);
    	
        String bucket = "aon-ses-inbox" ;

        List<String> messageIds = getMessageIds(input);
        
		for ( String messageId: messageIds ) {
			System.out.println("messageId: " + messageId );
	    	String key = String.format("laboral@aon.solutions/%s", messageId);
    		MimeCallback callback = new MimeCallback();
    		DSLPDFSalaryBuilder  salaryBuilder = 
    		new DSLPDFSalaryBuilder();
			Optional<MimeMessage> mimeMessage =
    		handleMessage(
			bucket, 
			key, 
			callback, 
			SESRequestHandler::handleZIP, 			   					
			//SESRequestHandler::handleText,
			SESRequestHandler.handlePDF(salaryBuilder)
			);
			
			String domain = mimeMessage.map( SESRequestHandler::getDomain ).orElse(null);
			
    		try (
			Connection connection = getConnection(domain);
	      	){
    			DSLContext dslContext = getDSLContext(connection);
	    		dslContext.transaction(c->{	
		    		salaryBuilder.execute( dslContext, domain);
		    		try {
		    			
		    			Address[] to = mimeMessage.map( SESRequestHandler::getTo ).orElse( new Address[] {});		    			
		    			SESSMTPSender.send(to, salaryBuilder.getInserted());
		    		} catch ( Exception e ) {
		    			System.out.println("ERROR:" + e.getMessage());
		    		}
	    		
	    		});
	    	} catch (Exception e) {
				e.printStackTrace();
			}


		}
        return "That's all Folks!";
    }
    
    private static String getDomain(MimeMessage mimeMessage) {
    	try {
    		System.out.println("Subject: " + mimeMessage.getSubject() );
			return findDomain(mimeMessage.getSubject());
		} catch (MessagingException e) {
			return null;
		}
    }
    
    private static String findDomain( String str ) {
    	if ( AonStringUtils.isEmpty(str))
    		return null;
    	Matcher matcher = Pattern.compile(DOMAIN_NAME_PATTERN).matcher(str);
    	if ( !matcher.matches() )
    		return null;
    	return matcher.group();
    }

	private static Address[] getTo(MimeMessage mimeMessage) {
		try {
			return mimeMessage.getFrom();
		} catch (MessagingException e) {
			return new Address[] {};
		} 
	}
    
    @SuppressWarnings("unchecked")
	private static List<String> getMessageIds(Object input) {
        
    	List<String> messageIds = new ArrayList<String>();
    	
    	Map<String,?> map = (Map<String,?>) input;
    	List<Map<String,?>> records = (List<Map<String,?>>) map.get("Records");
    	
    	for ( Map<String,?> record : records ) {
    		Map<String,?>  ses = (Map<String, ?>)record.get("ses");
    		Map<String,?>  mail = (Map<String, ?>)ses.get("mail");
    		String messageId = (String) mail.get("messageId");
    		messageIds.add(messageId);
    	}
    	
    	return messageIds;
    }
    
    private static Optional<MimeMessage> handleMessage(String bucket, String key, Callback callback, Handler ...handlers ) {
    	MimeMessage mimeMessage = null;
    	AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();  
        S3Object s3Object = s3.getObject(new GetObjectRequest(bucket, key));
        try ( InputStream is = s3Object.getObjectContent()) {
        	mimeMessage = handleMIME(is , callback, handlers );
        } catch ( Exception e ) {
        	e.printStackTrace();
        }
        return Optional.ofNullable(mimeMessage);
    }
    
    private static MimeMessage handleMIME( InputStream is, Callback callback, Handler ...handlers ) throws Exception {
		Session session = Session.getInstance(System.getProperties());
		MimeMessage mimeMessage = new MimeMessage(session, is);
		Multipart multipart = (Multipart ) mimeMessage.getContent() ;
		for ( int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart  = multipart.getBodyPart(i);
			callback.startFile(bodyPart.getFileName());
			try {
				handle(bodyPart.getContentType(), bodyPart.getInputStream(), callback, handlers); 	
				callback.endFile(bodyPart.getFileName());
			} catch ( Exception e ) {
				callback.exception(bodyPart.getFileName(), e);
			}
		}
		return mimeMessage;
    }
    
    private static void handle ( String contentType, InputStream is, Callback callback, Handler handlers []) {
    	
		for (Handler handler : handlers ) {
			try {
				if (handler.handle(contentType, is, callback, handlers) ) {
					return;
				}
			} catch ( Exception e ) {
			}
		}
		
		//throw new RuntimeException("Fichero desconocido");
    }
    
     private static boolean handleText( String contentType, InputStream is, Callback callback, Handler handlers [] ) throws Exception {
    	return 
		( 
		isMimeType(contentType, "text/html") ||
		isMimeType(contentType, "text/plain") 
		)
		&& handleText(is, getCharset(contentType), callback, handlers);
    }

    private static boolean handleZIP( String contentType, InputStream is, Callback callback, Handler handlers []) throws Exception {
    	return 
		isMimeType(contentType, "application/zip") 
		&& handleZIP(is, callback, handlers);
    }

    private static Handler handlePDF(ISalaryBuilder<?> salaryBuilder) {
    	return (contentType,is,callback, handlers) -> {
    		return isMimeType(contentType, "application/pdf") && handlePDF(is, callback, salaryBuilder);
    	};
    }


    private static boolean handleURL(String spec, Callback callback,Handler handlers []) {
		URL url = null;
		URLConnection connection = null;
    	try {
			url = new URL(spec);
			connection = url.openConnection();
			handle(connection.getContentType(), connection.getInputStream(), callback, handlers);
			
    	} catch (MalformedURLException e) {
		} catch (IOException e) {
		} finally {
		
		}
    	return true;
    }
 
    private static boolean handleText( InputStream is, Charset charset, Callback callback, Handler handlers []) throws Exception {
     	String html = convert(is, charset);
    	Pattern urlPattern = Pattern.compile("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    	Matcher matcher = urlPattern.matcher(html);
    	while ( matcher.find() ) {
    		String url = matcher.group();
    		url = getDownload(url);
    		handleURL(url, callback, handlers);
    	}
    	return true ;
    }
    
    private static boolean handleZIP( InputStream is, Callback callback, Handler handlers []) throws Exception {
    	try ( ZipInputStream zipIs = new ZipInputStream(is) ) {
    		
    		for ( ZipEntry entry = zipIs.getNextEntry(); entry != null; entry = zipIs.getNextEntry() ) {
    			if ( entry.isDirectory() ) {
    				;
    			} else {
    				String contentType = getContentType(entry);
    				handle(contentType, zipIs, callback, handlers);
    			}
    			zipIs.closeEntry();
    		}
    	}
    	return true ;
    }
 
    private static boolean handlePDF( InputStream is, Callback callback, ISalaryBuilder<?> salaryBuilder) throws Exception {    	
    	SalaryPDFParser.parse(is, salaryBuilder);
    	return true ;
    }
    
    private static DSLContext getDSLContext (Connection connection) throws SQLException {

		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		return DSL.using(connection, SQLDialect.MARIADB, settings);
    }
    
	private static Connection getConnection(String domain) throws SQLException, AonConnectionException, ClassNotFoundException {
		
		System.out.println("getConnection (" + domain + ") {");
		
    	ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
    	String schema = ci.getDomainDatabase(domain);
    	
		System.out.println("schema = " + schema + ";");

		Class.forName(ci.getDriverClass(schema));

        Properties properties = new Properties();
        properties.setProperty("user", ci.getUser(schema));
        properties.setProperty("password", ci.getPassword(schema));
        properties.setProperty("useSSL", ci.getUseSSL(schema));
        properties.setProperty("serverTimezone", ci.getTimeZone(schema));
        
        properties.forEach((k,v)-> System.out.println( k + " = " + v + ";" ) );

        
		System.out.println("}");
		
        return DriverManager.getConnection(ci.getSchemaUrl(schema), properties);
    }

    private static Optional<Connection> getOptionalConnection(String ccc) throws SQLException, AonConnectionException {
    	ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
    	for ( String schema : connectionInfo.getSchemas() ) {
    		Connection connection = connectionInfo.getConnection(schema);
    		if ( getEnterpriseCCC(connection, ccc).isPresent() ) 
    			return Optional.of(connection);
    		connection.close();
    	}
    	return Optional.empty();
    }
    
    private static Optional<EnterpriseCccRecord> getEnterpriseCCC(Connection connection, String ccc ) throws SQLException{
		return 
		getDSLContext(connection).select()
    	.from(ENTERPRISE_CCC)
    	.where(ENTERPRISE_CCC.CCC.eq(ccc))
    	.fetchOptionalInto(ENTERPRISE_CCC)
    	;
    }
    
    private static String convert(InputStream inputStream, Charset charset) throws IOException {
    	 
    	try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
    		return br.lines().collect(Collectors.joining(System.lineSeparator()));
    	}
    }
    
	private static Charset getCharset(String contentType) {
	  Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");    
	  if (contentType == null)
	    return null;
	  Matcher m = charsetPattern.matcher(contentType);
	  if (m.find()) {
		try {
			return Charset.forName(m.group(1).trim().toUpperCase());
		} catch ( Exception e ) {
			
		}
	  }
	  return Charset.defaultCharset();
	}   
	
	private static boolean isMimeType(String contentType, String mimeType) {
		return AonStringUtils.containsIgnoreCase(contentType, mimeType);
	}
	
	private static String getDownload(String url ) {
		Pattern pattern = Pattern.compile("(?<=\\/d\\/)[^\\/]*");
		Matcher matcher = pattern.matcher(url);
		while ( matcher.find() ) {
			String id = matcher.group();
			return String.format("https://drive.google.com/uc?export=download&id=%s", id );
		}
			
		return url;
	}

	private static String getContentType(ZipEntry zipEntry) {
		return new MimetypesFileTypeMap().getContentType(zipEntry.getName());	
	}
	
//    public static void main(String[] args) throws Exception {
//
//    	DSLPDFSalaryBuilder dslpdfSalaryBuilder = new DSLPDFSalaryBuilder();
//		Connection connection = getConnection("payroll-test.aonsolutions.org");
//		DSLContext dslContext = getDSLContext(connection);
//    	
//		SalaryPDFParser.parse(new File("/home/rtrepiana/Downloads/nominas noviembre.PDF"), dslpdfSalaryBuilder);
//		
//		dslpdfSalaryBuilder.execute(dslContext, "payroll-test.aonsolutions.org");
//    	
//	try (
//		Connection connection = getConnection("ayudat.aonsolutions.net");
//		DSLContext dslContext = getDSLContext(connection);
//		FileInputStream is = new FileInputStream(args[0]);
//		){	
//		
//		JooqPDFSalaryBuilder  salaryBuilder = 
//		new JooqPDFSalaryBuilder(dslContext, "ayudat.aonsolutions.net")
//		;
//		dslContext.transaction((c)->{			
//			MimeMessage mimeMessage =
//			handleMIME(
//					is, 
//					new MimeCallback(),
//	   				SESRequestHandler::handleZIP, 
//    				SESRequestHandler.handlePDF(salaryBuilder)
//					);
//		salaryBuilder.execute();
//		SESSMTPSender.send(getTo(mimeMessage), salaryBuilder.getInserted());
//		//throw new RuntimeException();
//		});
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//    }
    
}
