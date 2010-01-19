package com.code.aon.ui.cms.email;

import java.util.Arrays;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.code.aon.ui.cms.util.ControllerUtil;

public final class Emailer {

	private static int PAGE = 10; 
	
	public void sendEmail( Address[] aToEmailAddr,
			String aSubject, String aBody) throws Exception {
		Properties fMailServerConfig = new Properties();
		fMailServerConfig.put("mail.host", ControllerUtil.getCurrentConfig().getSmtp_server());
		fMailServerConfig.put("mail.from", ControllerUtil.getCurrentConfig().getFrom_email());
		fMailServerConfig.put("mail.smtp.auth", "true");

		
		Session session = Session.getDefaultInstance(fMailServerConfig);
		Transport transport = session.getTransport("smtp");
		
		try{
			transport.connect(ControllerUtil.getCurrentConfig().getSmtp_user(), ControllerUtil.getCurrentConfig().getSmtp_password());
			
			MimeMessage message = new MimeMessage(session);
		
			message.setHeader("X-Mailer", "AonCMS 1.0");
			message.setSubject(aSubject);
	       	MimeMultipart multipart1 =new MimeMultipart("related");
	       	MimeBodyPart part=new MimeBodyPart();
	       	part=new MimeBodyPart();
	       	part.setContent(aBody,"text/html");
	       	multipart1.addBodyPart(part);
	       	message.setContent(multipart1);
	       	Address[] addresses_from = new InternetAddress[1];
	       	addresses_from[0] = new InternetAddress(ControllerUtil.getCurrentConfig().getFrom_email(),ControllerUtil.getCurrentConfig().getFrom_name());
	       	message.addFrom(addresses_from);
	       	message.addRecipients(Message.RecipientType.TO, addresses_from);
	       	boolean repeat = true;
	       	while (repeat){
	       		if (aToEmailAddr.length>PAGE){
			       	message.addRecipients(Message.RecipientType.BCC, Arrays.copyOfRange(aToEmailAddr, 0, PAGE));
		       		transport.sendMessage(message,Arrays.copyOfRange(aToEmailAddr, 0, PAGE));
			       	aToEmailAddr = Arrays.copyOfRange(aToEmailAddr, PAGE, aToEmailAddr.length);
	       		}else{
			       	message.addRecipients(Message.RecipientType.BCC, aToEmailAddr);
		       		transport.sendMessage(message,aToEmailAddr);
			       	repeat = false;
	       		}
	       	}
		}catch (Exception e) {
			throw new Exception(e);
		}finally{
       		transport.close();
		}
	}

}
