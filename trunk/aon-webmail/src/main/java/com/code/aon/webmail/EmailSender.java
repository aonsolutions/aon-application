package com.code.aon.webmail;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonServer;

public class EmailSender {
	
	private AonServer server;
	
	private IMailAccount mailAccount;
	
	private Address from;
	
	public EmailSender( Address from, IMailAccount mailAccount ) {
		setFrom( from );
		setMailAccount( mailAccount );
		this.server = new AonServer(mailAccount);
	}

	public IMailAccount getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(IMailAccount mailAccount) {
		this.mailAccount = mailAccount;
	}

	public Address getFrom() {
		return from;
	}

	public void setFrom(Address from) {
		this.from = from;
	}
	
	public void sendMessage( Address[] to, String subject, String content ) throws WebmailException {
		sendMessage(to, subject, content, null);
	}
	
	public void sendMessage( Address[] to, String subject, String content, MimeType mimeType, AonFile ... attachemnts  ) throws WebmailException {
		sendMessage(to, subject, content, mimeType, null, attachemnts);
	}

	public void sendMessage( Address[] to, String subject, String content, MimeType mimeType, SecurityInfo si, AonFile ... attachemnts  ) throws WebmailException {
		AonMessage aonMessage = server.createAonMessage(from);
		aonMessage.setRecipientsTo( to );
		aonMessage.setSubject(subject);
       	String type = (mimeType != null) ? mimeType.getName() : MimeType.MIME_TXT.getName();
       	MimeBodyPart mainPart = new MimeBodyPart();
       	try {
	       	mainPart.setContent( content, type );
	       	MimeMultipart multipart = new MimeMultipart();
	       	multipart.addBodyPart(mainPart);
			if (! ArrayUtils.isEmpty(attachemnts) ) {
				for ( AonFile file : attachemnts ) {
					BodyPart bodyPart = WebmailUtil.getBodyPart(file);
					multipart.addBodyPart(bodyPart);
				}
		       	if ( si != null ) {
		       		multipart = EmailSecurity.sign( multipart, si );
		       	}				
			}
			aonMessage.setContent(multipart);
       	} catch ( MessagingException e ) {
       		throw new WebmailException( e.getMessage(), e );
       	}
		server.sendMessage(aonMessage);
	}

}
