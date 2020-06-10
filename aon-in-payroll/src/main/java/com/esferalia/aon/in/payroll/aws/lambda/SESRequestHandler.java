package com.esferalia.aon.in.payroll.aws.lambda;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

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
import com.esferalia.aon.in.payroll.pdf.jooq.JooqPDFSalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SESRequestHandler implements RequestHandler<Object, String> {

	
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
       
    	try (
		Connection connection = getConnection();
		DSLContext dslContext = getDSLContext(connection);
      	){
    		for ( String messageId: messageIds ) {
	    		String key = String.format("laboral@aon.solutions/%s", messageId);
	    		MimeCallback callback = new MimeCallback();
	    		JooqPDFSalaryBuilder  salaryBuilder = 
	    		new JooqPDFSalaryBuilder(dslContext, "ayudat-aonsolutions-net");
	    		
	    		dslContext.transaction((c)->{			
		    		handleMessage(
							bucket, 
							key, 
							callback, 
			   				SESRequestHandler::handleZIP, 			   					
			   				//SESRequestHandler::handleText,
		    				SESRequestHandler.handlePDF(salaryBuilder)
		 					);
		    		salaryBuilder.execute();
		    		try {
		    			SESSMTPSender.send(salaryBuilder.getInserted());
		    		} catch ( Exception e ) {
		    			System.out.println("ERROR:" + e.getMessage());
		    		}
	    		});
    		}
    		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return "That's all Folks!";
    }
    
    @SuppressWarnings("unchecked")
	private static List<String> getMessageIds(Object input) {
        
    	List<String> messageIds = new ArrayList<String>();
    	
    	Map<String,?> map = (Map<String,?>) input;
    	List<Map<String,?>> records = (List<Map<String,?>>) map.get("Records");
    	
    	for ( Map<String,?> record : records ) {
    		Map<String,?>  ses = (Map<String, ?>)record.get("ses");
    		Map<String,?>  mail = (Map<String, ?>)ses.get("mail");
//    		Map<String,?>  commonHeaders = (Map<String, ?>)mail.get("commonHeaders");
//    		String messageId = (String) commonHeaders.get("messageId");
    		String messageId = (String) mail.get("messageId");
    		messageIds.add(messageId);
    	}
    	
    	return messageIds;
    }
    
//    private static String[] getMessageFrom(Object input, String messageId) {
//        
//    	List<String> messageFrom = new ArrayList<String>();
//    	
//    	Map<String,?> map = (Map<String,?>) input;
//    	List<Map<String,?>> records = (List<Map<String,?>>) map.get("Records");
//    	
//    	for ( Map<String,?> record : records ) {
//    		Map<String,?>  ses = (Map<String, ?>)record.get("ses");
//    		Map<String,?>  mail = (Map<String, ?>)ses.get("mail");
//    		if ( !messageId.equals((String) mail.get("messageId")))
//    				continue;
//
//    		Map<String,?>  commonHeaders = (Map<String, ?>)mail.get("commonHeaders");
//    		return (String []) commonHeaders.get("from");
//    	}
//    	
//    	return new String[] {};
//    }
    
    
    private static void handleMessage(String bucket, String key, Callback callback, Handler ...handlers ) {
    	AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();  
        S3Object s3Object = s3.getObject(new GetObjectRequest(bucket, key));
        try ( InputStream is = s3Object.getObjectContent()) {
        	handleMIME(is , callback, handlers );
        } catch ( Exception e ) {
        	
        }
        
    }
    
    private static void handleMIME( InputStream is, Callback callback, Handler ...handlers ) throws Exception {
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
		
		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
    	return dslContext;
    }
    
    private static Connection getConnection() throws SQLException {
		String port = "3306";
    	String host = System.getenv("DB_HOST");
		String user = System.getenv("DB_USER");
		String password = System.getenv("DB_PASSWD");
		String database = System.getenv("DB_NAME");
    	
		Properties properties = new Properties();
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		properties.setProperty("useSSL", "false");
		properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
		String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
		
		return DriverManager.getConnection(url, properties);
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
	
    public static void main(String[] args) {
    
	try (
		Connection connection = getConnection();
		DSLContext dslContext = getDSLContext(connection);
//		FileInputStream is = new FileInputStream("/Users/aonsolutions/Documents/pdf.mime");
		FileInputStream is = new FileInputStream("/Users/aonsolutions/Documents/zip.mime");
		){	
		
		JooqPDFSalaryBuilder  salaryBuilder = 
//		new CheckSalaryPDFBuilder<ISalary>()
		new JooqPDFSalaryBuilder(dslContext, "ayudat-aonsolutions-net")
		;
		dslContext.transaction((c)->{			
			handleMIME(
					is, 
					new MimeCallback(),
	   				SESRequestHandler::handleZIP, 
    				SESRequestHandler.handlePDF(salaryBuilder)
					);
		salaryBuilder.execute();
		SESSMTPSender.send(salaryBuilder.getInserted());
		//throw new RuntimeException();
		});
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
    
}
