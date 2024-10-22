package solutions.aon.in.invoice.aws.lambda;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import solutions.aon.aws.s3.S3;

public class S3AttachRequestHandler implements RequestStreamHandler {
    
    
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException{
    	try {
    		String event = new String(input.readAllBytes());
	    
    		String[] paths = getPaths(event);
    		String bucket = paths[0];
    		String messageId = paths[1];
    		String attachId = paths[2];

    		byte[] data = S3.download("aon-ses-inbox/" + bucket +"@aon.solutions", messageId);
    	
    		Session session = Session.getDefaultInstance(System.getProperties());
    		MimeMessage mimeMessage = new MimeMessage(session, new ByteArrayInputStream(data));
	    
    		InputStream attachInput = getAttachContent(mimeMessage, attachId);
    		byte [] buffer = new byte [1024];
    		for( int read = attachInput.read(buffer); read > 1 ; read = attachInput.read(buffer) ) {
    			output.write(buffer, 0, read);
    		}
	
    	} catch (MessagingException e) {
    		throw new IOException(e);
    	} catch (NoSuchAlgorithmException e) {
    		e.printStackTrace();
    	} finally {
    		output.close();
    		input.close();
    	}
    }
    
    private static String[] getPaths(String input) {
	String path = getParam(input, "path");
	System.out.println(path);
	return Arrays.stream(path.split("/"))
		.filter(s -> !s.isBlank()).toArray(String[]::new);
    }
    
    private static String getParam(String input, String param) {
	Pattern pattern = Pattern.compile("\\\""+param+"\\\"\s*:\s*\\\"(?<"+param+">[^\\\"]*)\\\"");
	Matcher matcher = pattern.matcher(input);
	matcher.find(); 
	return matcher.group(param);
    }
    

    private static InputStream getAttachContent(MimeMultipart mimeMultipart, String attachId) throws MessagingException, IOException, NoSuchAlgorithmException {
    	for ( int i = 0 ; i < mimeMultipart.getCount(); i++ ) {
    		BodyPart part = mimeMultipart.getBodyPart(i);
    		if ( part instanceof MimeBodyPart mimeBodyPart 
    			    && Objects.equals(mimeBodyPart.getContentID(), "<"+attachId+">")) {
    			    return mimeBodyPart.getInputStream();
    		    }
    		    if ( Objects.equals(part.getFileName(), attachId) ) { 
    			return part.getInputStream();
    		    }  	
    		    
    		    if (Objects.equals(convertToMD5(part.getInputStream()), attachId))                                                                                               {
    		    	return part.getInputStream();
    		    }
    	}
    	
    	for ( int i = 0 ; i < mimeMultipart.getCount(); i++ ) {
    		BodyPart part = mimeMultipart.getBodyPart(i);
    		Object bodyContent = part.getContent();
    		if (bodyContent instanceof MimeMultipart) {
    			try {
    				return getAttachContent((MimeMultipart) bodyContent, attachId);
    			} catch (IOException e)  {
    				System.out.println("Element not found");
    			}
    		}    		
    	}
    	throw new IOException();
    }

    
    private static InputStream getAttachContent(MimeMessage mimeMessage, String attachId ) throws IOException, MessagingException, NoSuchAlgorithmException {
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
	    if (Objects.equals(convertToMD5(part.getInputStream()), attachId))                                                                                               {
	    	return part.getInputStream();
	    }
	}
	throw new IOException("");
    }
    
    private static String convertToMD5(InputStream message) throws NoSuchAlgorithmException, IOException {
    	
    	MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(message.readAllBytes());
        String myHash = bytesToHex(digest).toUpperCase();
    	
    	return myHash;
    	
    }
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void main(String[] args) throws IOException {

	ByteArrayInputStream input = new ByteArrayInputStream("\"path\": \"/soporte/oroa7bn6t5fvqmd4ou99eq87a6o07ga83o195b01/D64D1B96F355A7AC39DBA5CC934649D1\"".getBytes());

	new S3AttachRequestHandler().handleRequest(input, System.out, null);
    }

}
