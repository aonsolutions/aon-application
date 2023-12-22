package solutions.aon.in.invoice.aws.lambda;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class S3AttachRequestHandler implements RequestStreamHandler {
    
    
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException{
	try {
	    String event = new String(input.readAllBytes());
	    
	    String[] paths = getPaths(event);
	    String bucket = paths[0];
	    String messageId = paths[1];
	    String attachId = paths[2];

	    AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
	    S3Object s3Object = s3.getObject("aon-ses-inbox/" + bucket +"@aon.solutions", messageId);

	    S3ObjectInputStream s3ObjectIs = s3Object.getObjectContent();
	    Session session = Session.getDefaultInstance(System.getProperties());
	    MimeMessage mimeMessage = new MimeMessage(session, s3ObjectIs);
	    
	    InputStream attachInput = getAttachContent(mimeMessage, attachId);
	    byte [] buffer = new byte [1024];
	    for( int read = attachInput.read(buffer); read > 1 ; read = attachInput.read(buffer) ) {
		output.write(buffer, 0, read);
	    }
	
	} catch (MessagingException e) {
	    throw new IOException(e);
	} finally {
	    output.close();
	    input.close();
	}

    }
    
    
    private static String[] getPaths(String input) {
	String path = getParam(input, "path");
	return Arrays.stream(path.split("/"))
		.filter(s -> !s.isBlank()).toArray(String[]::new);
    }
    
    private static String getParam(String input, String param) {
	Pattern pattern = Pattern.compile("\\\""+param+"\\\"\s*:\s*\\\"(?<"+param+">[^\\\"]*)\\\"");
	Matcher matcher = pattern.matcher(input);
	matcher.find(); 
	return matcher.group(param);
    }
    
    
    private static InputStream getAttachContent(MimeMessage mimeMessage, String attachId ) throws IOException, MessagingException {
	MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
	for ( int i = 0 ; i < mimeMultipart.getCount(); i++ ) {
	    BodyPart  part = mimeMultipart.getBodyPart(i);
	    if ( part instanceof MimeBodyPart mimeBodyPart 
		    && Objects.equals(mimeBodyPart.getContentID(), "<"+attachId+">")) {
		    return mimeBodyPart.getInputStream();
	    }
	    if ( Objects.equals(part.getFileName(), attachId) ) { 
		return part.getInputStream();
	    }
	}
	throw new IOException("");
    }

    public static void main(String[] args) throws IOException {
	ByteArrayInputStream input = new ByteArrayInputStream("\"path\": \"/facturas/p0ka88cor625i4s9ntrs1bu6aqrvjild29nftg01/f_kbhsa5rn0\"".getBytes());
	new S3AttachRequestHandler().handleRequest(input, System.out, null);
    }

}
