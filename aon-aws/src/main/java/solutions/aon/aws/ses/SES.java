package solutions.aon.aws.ses;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.GetEmailIdentityRequest;
import software.amazon.awssdk.services.sesv2.model.GetEmailIdentityResponse;
import software.amazon.awssdk.services.sesv2.model.RawMessage;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.VerificationStatus;

public class SES {

	private static final Logger LOGGER = Logger.getLogger(SES.class.getName());
	private static final String EMAIL_SENT = "Email Sent!";
	private static final String EMAIL_NOT_SENT = "The email was not sent.";
	
	private static SesV2Client getSesV2Client() {
		return SesV2Client.builder()
				.region(Region.EU_WEST_1)
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build();
	}
	
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

            // Instantiate an Amazon SES client, which will make the service 
            // call with the supplied AWS credentials.
            
            SesV2Client client = getSesV2Client();
        	
            // Send the email.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            
			SendEmailRequest rawEmailRequest = SendEmailRequest.builder()
					.content(contentBuilder -> contentBuilder
							.raw(rawBuilder -> rawBuilder.data(SdkBytes.fromByteArray(outputStream.toByteArray()))))
					.build();

			client.sendEmail(rawEmailRequest);
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
            SesV2Client client = getSesV2Client();

            // Send the email.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            
			SendEmailRequest rawEmailRequest = SendEmailRequest.builder()
					.content(contentBuilder -> contentBuilder
							.raw(rawBuilder -> rawBuilder.data(SdkBytes.fromByteArray(outputStream.toByteArray()))))
					.build();
            
            client.sendEmail(rawEmailRequest);
            
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
    
	public static GetEmailIdentityResponse getEmailIdentity(String email) {
		try {
			return getSesV2Client().getEmailIdentity( builder -> builder.emailIdentity(email));
		} catch ( AwsServiceException | SdkClientException e ) {
			return null;
		}
		
	}

	public static boolean isVerifiedForSendingStatus(String email) {
		try {
	    	GetEmailIdentityResponse response = getSesV2Client().getEmailIdentity( builder -> builder.emailIdentity(email));
	    	return response.verifiedForSendingStatus();
		} catch ( AwsServiceException | SdkClientException e ) {
			return false;
		}
    }
	
	public static void sendVerificationEmail(String email) {
		SesV2Client client = getSesV2Client();
		try {
			client.deleteEmailIdentity(builder -> builder.emailIdentity(email));
		} catch ( AwsServiceException | SdkClientException e ) {
		}
		client.createEmailIdentity(builder -> builder.emailIdentity(email));

		//getSesV2Client().sendCustomVerificationEmail( builder -> builder.emailAddress(email).templateName("AonSolutionsTemplate") );
	}
	
	public static String verificationStatus(String email) {
		try {
	    	GetEmailIdentityResponse response = getSesV2Client().getEmailIdentity( builder -> builder.emailIdentity(email));
	    	System.out.println(response.verificationStatusAsString());
	    	return response.verificationStatusAsString();
		} catch ( AwsServiceException | SdkClientException e ) {
			return null;
		}		
	}
    

    public static void main(String[] args) {
    	sendVerificationEmail("diaznayra2@gmail.com");
	}
}
