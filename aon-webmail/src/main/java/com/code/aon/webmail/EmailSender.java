package com.code.aon.webmail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonServer;
import com.code.aon.webmail.bean.IMimeType;

public class EmailSender {
	
	private AonServer server;
	
	private MailAccount mailAccount;
	
	private Address from;
	
	public EmailSender( Address from, MailAccount mailAccount ) {
		setFrom( from );
		setMailAccount( mailAccount );
	}

	public MailAccount getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(MailAccount mailAccount) {
		this.mailAccount = mailAccount;
	}

	public Address getFrom() {
		return from;
	}

	public void setFrom(Address from) {
		this.from = from;
	}

	public void connect() throws MessagingException {
		this.server = new AonServer(mailAccount);
		server.connect();
	}
	
	public void sendMessage( Address to, String subject, String content ) throws WebmailException, MessagingException {
		sendMessage(to, subject, content, null);
	}
	
	public void sendMessage( Address to, String subject, String content, MimeType mimeType, AonFile ... attachemnts  ) throws WebmailException {
		sendMessage(to, subject, content, mimeType, null, attachemnts);
	}

	public void sendMessage( Address to, String subject, String content, MimeType mimeType, SecurityInfo si, AonFile ... attachemnts  ) throws WebmailException {
		AonMessage aonMessage = server.createAonMessage(from);
		aonMessage.setRecipientsTo( new Address[]{to} );
		aonMessage.setSubject(subject);
		if (! ArrayUtils.isEmpty(attachemnts) ) {
	       	MimeMultipart multipart =new MimeMultipart(IMimeType.RELATED);
	       	MimeBodyPart mainPart = new MimeBodyPart();
	       	String type = (mimeType != null) ? mimeType.getName() : MimeType.MIME_TXT.getName();
	       	try {
		       	mainPart.setContent( content, type );
		       	multipart.addBodyPart(mainPart);
				for ( AonFile file : attachemnts ) {
					MimeBodyPart bodyPart = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(file.getFile());
					bodyPart.setFileName( file.getFileName() );
					bodyPart.setDataHandler(new DataHandler(fds));
					multipart.addBodyPart(bodyPart);
				}
	       	} catch ( MessagingException e ) {
	       		throw new WebmailException( e.getMessage(), e );
	       	}
	       	if ( si != null ) {
	       		multipart = EmailSecurity.sign( multipart, si );
	       	}
			aonMessage.setContent(multipart);				
		} else {
			aonMessage.setContent(content);	
		}
		server.sendMessage(aonMessage);
	}
	
	public void disconnect() {
		server.disconnect();
		server = null;
	}

}
