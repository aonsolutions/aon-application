package com.code.aon.google.apis;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

public class GmailUtils {

	
	public static Gmail serviceInitialize(DomainGserviceaccount g) 
			throws IOException, GeneralSecurityException{
		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = g.getEmailAddress();

		InputStream keyStream = new ByteArrayInputStream(g.getPrivateKey());;
		PrivateKey serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), keyStream, "notasecret",
		          "privatekey", "notasecret");
		String googleAccount = g.getGoogleAccount();
		GoogleCredential credential;
		if(googleAccount != null)
			credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						Arrays.asList(GmailScopes.MAIL_GOOGLE_COM))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey)
				.setServiceAccountUser(googleAccount)
				.build();
		else
			credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						Arrays.asList(GmailScopes.MAIL_GOOGLE_COM))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey)
				.build();
	
		Gmail client= new com.google.api.services.gmail.Gmail.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential )
				.setApplicationName("AON SOLUTIONS").build();
		client.users().messages().list(googleAccount).execute();
		return client;
		
	}

	  /**
	   * Send an email from the user's mailbox to its recipient.
	   *
	   * @param service Authorized Gmail API instance.
	   * @param userId User's email address. The special value "me"
	   * can be used to indicate the authenticated user.
	   * @param email Email to be sent.
	   * @throws MessagingException
	   * @throws IOException
	   */
	  public static void sendMessage(Gmail service, String userId, MimeMessage email)
	      throws MessagingException, IOException {
	    Message message = createMessageWithEmail(email);
	    message = service.users().messages().send(userId, message).execute();
	    System.out.println("Message id: " + message.getId());
	    System.out.println(message.toPrettyString());
	  }

	  /**
	   * Create a Message from an email
	   *
	   * @param email Email to be set to raw of message
	   * @return Message containing base64 encoded email.
	   * @throws IOException
	   * @throws MessagingException
	   */
	  public static Message createMessageWithEmail(MimeMessage email)
	      throws MessagingException, IOException {
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    email.writeTo(bytes);
	    String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
	    Message message = new Message();
	    message.setRaw(encodedEmail);
	    return message;
	  }

	  /**
	   * Create a MimeMessage using the parameters provided.
	   *
	   * @param to Email address of the receiver.
	   * @param from Email address of the sender, the mailbox account.
	   * @param subject Subject of the email.
	   * @param bodyText Body text of the email.
	   * @return MimeMessage to be used to send email.
	   * @throws MessagingException
	   */
	  public static MimeMessage createEmail(String to, String from, String subject,
			  String bodyText) throws MessagingException {
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    MimeMessage email = new MimeMessage(session);

	    email.setFrom(new InternetAddress(from));
	    email.addRecipient(jakarta.mail.Message.RecipientType.TO,
	                       new InternetAddress(to));
	    email.setSubject(subject);
	    email.setContent(bodyText, "text/html");
	    return email;
	  }

	  /**
	   * Create a MimeMessage using the parameters provided.
	   *
	   * @param to Email address of the receiver.
	   * @param from Email address of the sender, the mailbox account.
	   * @param subject Subject of the email.
	   * @param bodyText Body text of the email.
	   * @param fileDir Path to the directory containing attachment.
	   * @param filename Name of file to be attached.
	   * @return MimeMessage to be used to send email.
	   * @throws MessagingException
	   */
	  public static MimeMessage createEmailWithAttachment(String to, String from, String subject,
	      String bodyText, String fileDir, String filename) 
	    		  throws MessagingException, IOException {
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    MimeMessage email = new MimeMessage(session);
	    InternetAddress tAddress = new InternetAddress(to);
	    InternetAddress fAddress = new InternetAddress(from);

	    email.setFrom(fAddress);
	    email.addRecipient(jakarta.mail.Message.RecipientType.TO, tAddress);
	    email.setSubject(subject);

	    MimeBodyPart mimeBodyPart = new MimeBodyPart();
	    mimeBodyPart.setContent(bodyText, "text/html");
	    mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

	    Multipart multipart = new MimeMultipart();
	    multipart.addBodyPart(mimeBodyPart);

	    mimeBodyPart = new MimeBodyPart();
	    DataSource source = new FileDataSource(fileDir + filename);

	    mimeBodyPart.setDataHandler(new DataHandler(source));
	    mimeBodyPart.setFileName(filename);
	    String contentType = Files.probeContentType(FileSystems.getDefault()
	        .getPath(fileDir, filename));
	    mimeBodyPart.setHeader("Content-Type", contentType + "; name=\"" + filename + "\"");
	    mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");

	    multipart.addBodyPart(mimeBodyPart);

	    email.setContent(multipart);

	    return email;
	  }
	  
	  public static MimeMessage createEmailWithAttachments(String to, String from, String subject,
		      String bodyText, Vector<BodyPart> bodyParts) 
		    		  throws MessagingException, IOException {
		    Properties props = new Properties();
		    Session session = Session.getDefaultInstance(props, null);

		    MimeMessage email = new MimeMessage(session);
		    InternetAddress tAddress = new InternetAddress(to);
		    InternetAddress fAddress = new InternetAddress(from);

		    email.setFrom(fAddress);
		    email.addRecipient(jakarta.mail.Message.RecipientType.TO, tAddress);
		    email.setSubject(subject);

		    MimeBodyPart mimeBodyPart = new MimeBodyPart();
		    mimeBodyPart.setContent(bodyText, "text/html");
		    mimeBodyPart.setHeader("Content-Type", "text/html; charset=\"UTF-8\"");

		    Multipart multipart = new MimeMultipart();
		    multipart.addBodyPart(mimeBodyPart);

		    bodyParts.stream().forEach(b->{try {
				multipart.addBodyPart(b);
			} catch (Exception e) {
				e.printStackTrace();
			}});

		    email.setContent(multipart);

		    return email;
		  }
	  
	  public static void send(Gmail gmail, String to, String subject, String body) 
			  throws IOException, MessagingException{
			MimeMessage m =createEmail(to, "me", subject, body);		  
			sendMessage(gmail,"me", m);
	  }
	  
	  public static void main(String[] args) 
			  throws MessagingException, IOException, GeneralSecurityException {
		  String domainName = "energilandia.aibanez.net";
		  Integer domainId = 534;
		  String login = "roberto";
		  System.out.println(login + " - " + domainId + " - " + domainName);
		  DomainGserviceaccount  g = AON.getDomainGserviceaccount(domainName, domainId, login);
		  Gmail gmail = serviceInitialize(g);
		  System.out.println(g);
		  send(gmail, "aibanez@aonsolutions.es", "PRUEBA", "PRUEBA");
	  }

}
