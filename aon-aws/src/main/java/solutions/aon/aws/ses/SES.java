package solutions.aon.aws.ses;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
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
import software.amazon.awssdk.services.sesv2.model.GetEmailIdentityResponse;
import software.amazon.awssdk.services.sesv2.model.SendCustomVerificationEmailResponse;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.Tag;

public class SES {

	private static final Logger LOGGER = Logger.getLogger(SES.class.getName());
	private static final String EMAIL_SENT = "Email Sent!";
	private static final String EMAIL_NOT_SENT = "The email was not sent.";

	private static SesV2Client getSesV2Client() {
		return SesV2Client.builder().region(Region.EU_WEST_1).credentialsProvider(getCredentialsProvider()).build();
	}

	private static DefaultCredentialsProvider getCredentialsProvider() {
		return DefaultCredentialsProvider.create();
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
	
	private static String getEmailDomain(String email) {
		if ( email == null )
			return null;
		int atIndex = email.lastIndexOf('@');
		return atIndex > -1 ? email.substring(atIndex + 1 ): null;
	}

	private static String getParentDomain(String domain) {
		if ( domain == null )
			return null;
		int dotIndex = domain.indexOf('.');
		return dotIndex > -1 ? domain.substring(dotIndex + 1 ): null;
	}

	private static boolean isValidDomain(String domain) {
		return  domain != null && domain.indexOf('.') > -1;
	}

	public static String sendEmailWithAttachment(SESMessage msg) {
		try {
			Session session = Session.getDefaultInstance(new Properties());

			// Create a new MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Add subject, from and to lines.
			message.setSubject(msg.getSubject(), "UTF-8");

			if (!isVerifiedForSendingStatus(msg.getFrom())) {
				msg.setFrom(null);
			}

			message.setFrom(new InternetAddress(msg.getAliasFrom()));

			String to = String.join(",", msg.getTo().toArray(String[]::new));
			message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(to));

			String bcc = String.join(",", msg.getBcc().toArray(String[]::new));
			message.setRecipients(jakarta.mail.Message.RecipientType.BCC, InternetAddress.parse(bcc));

			String cc = String.join(",", msg.getCc().toArray(String[]::new));
			message.setRecipients(jakarta.mail.Message.RecipientType.CC, InternetAddress.parse(cc));

			if (msg.isReplyTo()) {
				message.setReplyTo(InternetAddress.parse(msg.getReplyTo()));
			}

			// Create a multipart/alternative child container.
			MimeMultipart msgBody = new MimeMultipart("alternative");

			// Create a wrapper for the HTML and text parts.
			MimeBodyPart wrap = new MimeBodyPart();

			// Define the HTML part.
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(msg.getBody(), "text/html; charset=UTF-8");

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

	public static void sendEmail(String domain, MimeMessage message) throws MessagingException, IOException {
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
		SESMessage msg = new SESMessage().setFrom(from).setTo(toList).setSubject(subject).setBody(body);
		return sendEmail(msg);
	}

	public static String sendEmailWithBCC(String from, String to, String bbc, String subject, String body) {
		LinkedList<String> toList = new LinkedList<>();
		toList.add(to);

		LinkedList<String> bbcList = new LinkedList<>();
		bbcList.add(bbc);

		SESMessage msg = new SESMessage().setFrom(from).setTo(toList).setBcc(bbcList).setSubject(subject).setBody(body);
		return sendEmail(msg);
	}

	public static String sendEmailWithAttachment(String from, List<String> toList, String subject, String body,
			List<File> files) {
		SESMessage msg = new SESMessage().setFrom(from).setTo(toList).setSubject(subject).setBody(body).setFiles(files);
		return sendEmailWithAttachment(msg);
	}

	public static String sendEmailWithAttachment(String from, String to, String subject, String body,
			List<File> files) {
		LinkedList<String> toList = new LinkedList<>();
		toList.add(to);

		SESMessage msg = new SESMessage().setFrom(from).setTo(toList).setSubject(subject).setBody(body).setFiles(files);
		return sendEmailWithAttachment(msg);
	}

	public static GetEmailIdentityResponse getEmailIdentity(String email) {
		try {
			return getSesV2Client().getEmailIdentity(builder -> builder.emailIdentity(email));
		} catch (AwsServiceException | SdkClientException e) {
			return null;
		}

	}

	public static boolean isVerifiedForSendingStatus(String email) {
		GetEmailIdentityResponse emailIdentity = SES.getEmailIdentity(email);
		for (String domain = getEmailDomain(email); emailIdentity == null
				&& isValidDomain(domain); domain = getParentDomain(domain)) {
			emailIdentity = SES.getEmailIdentity(domain);
		}
		return emailIdentity != null && emailIdentity.verifiedForSendingStatus();
	}

	public static void deleteEmailIdentity(String email) {
		getSesV2Client().deleteEmailIdentity(builder -> builder.emailIdentity(email));
	}

//	public static void createEmailIdentity(String email, Map<String, String> tags) {
//		Tag[] sesv2Tags = tags.entrySet().stream()
//				.map(entry -> Tag.builder().key(entry.getKey()).value(entry.getValue()).build()).toArray(Tag[]::new);
//		getSesV2Client().createEmailIdentity(createRequestBuilder -> createRequestBuilder.emailIdentity(email).tags(sesv2Tags));
//	}
//
//	public static void createEmailIdentity(String email, String configurationSetName, Map<String, String> tags) {
//		Tag[] sesv2Tags = tags.entrySet().stream()
//				.map(entry -> Tag.builder().key(entry.getKey()).value(entry.getValue()).build()).toArray(Tag[]::new);
//		getSesV2Client().createEmailIdentity(builder -> builder.emailIdentity(email)
//				.configurationSetName(configurationSetName).tags(sesv2Tags));
//	}

	public static void sendVerificationEmail(String email, String templateName)
			throws InterruptedException, ExecutionException {
		sendVerificationEmail(email, null, Collections.emptyMap(), templateName);
	}

	public static void sendVerificationEmail(String email, String configurationSetName, Map<String, String> tags,
			String templateName) throws InterruptedException, ExecutionException {
		SesV2Client sesV2Client = getSesV2Client();

		SendCustomVerificationEmailResponse response = sesV2Client.sendCustomVerificationEmail(builder -> builder
				.emailAddress(email).templateName(templateName).configurationSetName(configurationSetName));

		System.out.println(response.sdkHttpResponse().statusText());

		try {
			Tag[] sesv2Tags = tags.entrySet().stream()
					.map(entry -> Tag.builder().key(entry.getKey()).value(entry.getValue()).build())
					.toArray(Tag[]::new);

			getSesV2Client().serviceClientConfiguration().credentialsProvider().resolveIdentity().get().accountId()
					.ifPresent(accountId -> {
						Region region = sesV2Client.serviceClientConfiguration().region();
						String resourceArn = String.format("arn:aws:ses:%s:%s:identity/%s", region.id(), accountId,
								email);
						sesV2Client.tagResource(builder -> builder.resourceArn(resourceArn).tags(sesv2Tags));
					});
		} catch (InterruptedException | ExecutionException e) {
			sesV2Client.deleteEmailIdentity(builder -> builder.emailIdentity(email));
			throw e;
		}
	}

	public static String verificationStatus(String email) {
		try {
			GetEmailIdentityResponse response = getSesV2Client()
					.getEmailIdentity(builder -> builder.emailIdentity(email));
			return response.verificationStatusAsString();
		} catch (AwsServiceException | SdkClientException e) {
			return null;
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		sendVerificationEmail("raultrepiana@outlook.com", "AonSolutionsTemplate");
	}
}
