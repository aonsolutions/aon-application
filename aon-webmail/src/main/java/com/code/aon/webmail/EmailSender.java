package com.code.aon.webmail;

import java.io.Serializable;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonServer;

public class EmailSender implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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

	public AonMessage sendMessage( Address[] to, String subject, String content ) throws WebmailException {
		return sendMessage(to, subject, content, null);
	}
	
	public AonMessage sendMessage( Address[] to, String subject, String content, MimeType mimeType, AonFile ... attachemnts  ) throws WebmailException {
		AonMessage aonMessage = createMessage(to, subject);
		addMessageContent(aonMessage, content, mimeType, attachemnts);
		sendMessage(aonMessage);
		return aonMessage;
	}
	
	public AonMessage createMessage( Address[] to, String subject, String content, MimeType mimeType, AonFile ... attachemnts  ) throws WebmailException {
		AonMessage aonMessage = createMessage(to, subject);
		addMessageContent(aonMessage, content, mimeType, attachemnts);
		return aonMessage;
	}

	public AonMessage createMessage() throws WebmailException {
		AonMessage aonMessage = server.createAonMessage(from);
		return aonMessage;
	}	
	
	public AonMessage createMessage( String subject  ) throws WebmailException {
		AonMessage aonMessage = createMessage();
		aonMessage.setSubject(subject);
		return aonMessage;
	}	
	
	public AonMessage createMessage( Address[] to, String subject  ) throws WebmailException {
		AonMessage aonMessage = createMessage(subject);
		aonMessage.setRecipientsTo( to );
		return aonMessage;
	}	
	
	public void addMessageContent( AonMessage aonMessage, String content, MimeType mimeType, AonFile ... attachemnts  ) throws WebmailException {
       	String type = (mimeType != null) ? mimeType.getName() : MimeType.MIME_TXT.getName();
       	MimeBodyPart mainPart = new MimeBodyPart();
       	try {
	       	mainPart.setContent( content, type );
	       	MimeMultipart multipart = new MimeMultipart();
	       	multipart.addBodyPart(mainPart);
			if (! ArrayUtils.isEmpty(attachemnts) ) {
				for ( AonFile file : attachemnts ) {
					if ( file != null ) {
						BodyPart bodyPart = WebmailUtil.getBodyPart(file);
						multipart.addBodyPart(bodyPart);						
					}
				}
			}
			aonMessage.setContent(multipart);
       	} catch ( MessagingException e ) {
       		throw new WebmailException( e.getMessage(), e );
       	}		
	}
	
	public void sendMessage( AonMessage aonMessage ) throws WebmailException {
		server.sendMessage(aonMessage);
	}

	public void storeMessage( AonMessage aonMessage ) throws WebmailException {
		String folderName = server.getSentFolderName();
		if ( (folderName != null) && server.isIMAP() ) {
	    	Message[] messages = new Message[]{aonMessage.getMessage()};
			try {
				messages[0].setFlag(Flag.SEEN, true);
				AonFolder folder = server.getAonFolder(folderName);
				folder.open(Folder.READ_WRITE);
		    	Folder desfFolder = folder.getFolder();
		    	desfFolder.appendMessages(messages);
		    	desfFolder.expunge();
		    	folder.close(false);		
			} catch (MessagingException e) {
				throw new WebmailException( e.getMessage(), e );
			}
		}
	}

	public void setNumberOfMessagesPerTransport(int numberOfMessagesPerTransport) {
		server.setNumberOfMessagesPerTransport(numberOfMessagesPerTransport);
	}
	
	public void close() {
		server.close();
	}
	
}
