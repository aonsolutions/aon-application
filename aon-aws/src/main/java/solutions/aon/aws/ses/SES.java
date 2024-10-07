package solutions.aon.aws.ses;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class SES {
	
	private static final Logger LOGGER  = Logger.getLogger(SES.class.getName());
	private static final String EMAIL_SENT = "Email Sent!";
	private static final String EMAIL_NOT_SENT = "The email was not sent.";
	
	public static String sendEmail(SESMessage msg) {	
		return sendEmailWithAttachment(msg);
//		if(msg.hasAttach()) {
//			return sendEmailWithAttachment(msg);
//		}
//        Destination destination = new Destination()
//        		.withToAddresses(msg.getTo())
//        		.withBccAddresses(msg.getBcc())
//        		.withCcAddresses(msg.getCc());
//        
//        Content subject = new Content().withData(msg.getSubject());
//        Content textBody = new Content().withData(msg.getBody());
//        Body body = new Body().withHtml(textBody);
//
//        Message message = new Message().withSubject(subject).withBody(body);
//        
//        SendEmailRequest request = new SendEmailRequest()
//        		.withSource(msg.getFrom())
//        		.withDestination(destination)
//        		.withMessage(message);
//        if(msg.isReplyTo()) {
//        	request.withReplyToAddresses(msg.getReplyTo());
//        }
//        
//        return sendEmail(request);
	}
	
    public static String sendEmailWithAttachment(SESMessage msg) {	
        try {
        	Session session = Session.getDefaultInstance(new Properties());
    	
        	// Create a new MimeMessage object.
        	MimeMessage message = new MimeMessage(session);
        
        	// Add subject, from and to lines.
        	message.setSubject(msg.getSubject(), "UTF-8");
        	message.setFrom(new InternetAddress(msg.getAliasFrom()));
        
        	String to =  String.join(",", msg.getTo().toArray(String[]::new));
        	message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));
        	
        	String bcc =  String.join(",", msg.getBcc().toArray(String[]::new));
        	message.setRecipients(jakarta.mail.Message.RecipientType.BCC, InternetAddress.parse(bcc));

        	String cc =  String.join(",", msg.getCc().toArray(String[]::new));
        	message.setRecipients(jakarta.mail.Message.RecipientType.CC, InternetAddress.parse(cc));
        	
        	if(msg.isReplyTo()) {
            	message.setReplyTo(InternetAddress.parse(msg.getReplyTo()));
        	}
        	
        	// Create a multipart/alternative child container.
        	MimeMultipart msgBody = new MimeMultipart("alternative");
        
        	// Create a wrapper for the HTML and text parts.        
        	MimeBodyPart wrap = new MimeBodyPart();
                
        	// Define the HTML part.
        	MimeBodyPart htmlPart = new MimeBodyPart();
        	htmlPart.setContent(msg.getBody(),"text/html; charset=UTF-8");
                
        	// Add the text and HTML parts to the child container.
        	msgBody.addBodyPart(htmlPart);
        
        	// Add the child container to the wrapper object.
        	wrap.setContent(msgBody);
        
        	// Create a multipart/mixed parent container.
        	MimeMultipart multipart = new MimeMultipart("mixed");
        
        	// Add the parent container to the message.
        	message.setContent(multipart);
        
        	// Add the multipart/alternative part to the message.
        	multipart.addBodyPart(wrap);
        
        	for (File file : msg.getFiles()) {
        		// Define the attachment
        		MimeBodyPart att = new MimeBodyPart(); 
        		DataSource bds = new FileDataSource(file);
        		att.setDataHandler(new DataHandler(bds)); 
        		att.setFileName(bds.getName());             
        		// Add the attachment to the message.
        		multipart.addBodyPart(att);
        	}

        	LOGGER.info("Attempting to send an email through Amazon SES "
                    + "using the AWS SDK for Java...");

            // Instantiate an Amazon SES client, which will make the service 
            // call with the supplied AWS credentials.
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().build();
           
            // Send the email.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

            SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
            client.sendRawEmail(rawEmailRequest);
            LOGGER.info(EMAIL_SENT);
            return "ok";
        } catch (Exception e) {
        	// Display an error if something goes wrong.
        	LOGGER.warning(EMAIL_NOT_SENT);
			e.printStackTrace();
			return e.getMessage();
        }
    }
    
    public static void sendEmail(String domain, MimeMessage message) throws MessagingException, IOException{	
    	try {
            // Instantiate an Amazon SES client, which will make the service 
            // call with the supplied AWS credentials.
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().build();

            // Send the email.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
            
            SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
            client.sendRawEmail(rawEmailRequest);
            LOGGER.info(EMAIL_SENT + " from " + domain);
        } catch (Exception e) {
            // Display an error if something goes wrong.
        	LOGGER.warning(EMAIL_NOT_SENT + " from " + domain);
			e.printStackTrace();
        }
    }
	
    public static String sendEmailToList(String from, List<String> toList, String subject, String body) {
    	SESMessage msg = new SESMessage()
    			.setFrom(from)
    			.setTo(toList)
    			.setSubject(subject)
    			.setBody(body);
    	return sendEmail(msg);
    }
    
    public static String sendEmailWithBCC(String from, String to, String bbc, String subject, String body) {
    	LinkedList<String> toList = new LinkedList<>();
       	toList.add(to);
       	
    	LinkedList<String> bbcList = new LinkedList<>();
    	bbcList.add(bbc);

    	SESMessage msg = new SESMessage()
    			.setFrom(from)
    			.setTo(toList)
    			.setBcc(bbcList)
    			.setSubject(subject)
    			.setBody(body);
    	return sendEmail(msg);
    }
    
    public static String sendEmail(SendEmailRequest request) {
    	try {
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().build();
            
            client.sendEmail(request);
            LOGGER.info(EMAIL_SENT);
            return "ok";
        } catch (Exception e) {
            LOGGER.warning(EMAIL_NOT_SENT);
            e.printStackTrace();
            return e.getMessage();
        }
    }
    
    public static String sendEmailWithAttachment(String from, List<String> toList, String subject, String body, List<File> files) {	
    	SESMessage msg = new SESMessage()
    			.setFrom(from)
    			.setTo(toList)
    			.setSubject(subject)
    			.setBody(body)
    			.setFiles(files);
    	return sendEmailWithAttachment(msg);
    }
    
    public static String sendEmailWithAttachment(String from, String to, String subject, String body, List<File> files) {	
    	LinkedList<String> toList = new LinkedList<>();
       	toList.add(to);
       	
       	SESMessage msg = new SESMessage()
    			.setFrom(from)
    			.setTo(toList)
    			.setSubject(subject)
    			.setBody(body)
    			.setFiles(files);
    	return sendEmailWithAttachment(msg);    	
    }
}
