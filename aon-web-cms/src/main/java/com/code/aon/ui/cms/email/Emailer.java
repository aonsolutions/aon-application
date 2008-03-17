package com.code.aon.ui.cms.email;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.code.aon.ui.cms.util.ControllerUtil;

public final class Emailer {

	public void sendEmail( Address[] aToEmailAddr,
			String aSubject, String aBody) {
		Properties fMailServerConfig = new Properties();
		fMailServerConfig.put("mail.host", ControllerUtil.getCurrentConfig().getHost());
		fMailServerConfig.put("mail.from", ControllerUtil.getCurrentConfig().getFrom_email());
		
		Session session = Session.getDefaultInstance(fMailServerConfig, null);
		MimeMessage message = new MimeMessage(session);
		try {
		
			message.setHeader("X-Mailer", "AonCMS 1.0");
			message.addRecipients(Message.RecipientType.TO, aToEmailAddr);
			message.setSubject(aSubject);
	       	MimeMultipart multipart1 =new MimeMultipart("related");
	       	MimeBodyPart part=new MimeBodyPart();
	       	part=new MimeBodyPart();
	       	part.setContent(aBody,"text/html");
	       	multipart1.addBodyPart(part);
	       	message.setContent(multipart1);
			Transport.send(message);
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
	}

}
