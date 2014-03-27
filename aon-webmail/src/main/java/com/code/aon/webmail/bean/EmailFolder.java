package com.code.aon.webmail.bean;

import javax.mail.Folder;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailFolder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailFolder.class);
	
	private String name;
	
	private String fullName;
	
	private AonServer server;

	public EmailFolder(Folder folder, AonServer server) {
		this.name = folder.getName();
		this.fullName = folder.getFullName();
		this.server = server;
	}

	public AonServer getServer() {
		return server;
	}
	
    public String getName() {
    	return name;
    }	
    
    public String getFullName() {
		return fullName;
	}

	public static boolean isHoldFolders(Folder folder) {
    	try {
    		return ( folder.getType() & Folder.HOLDS_FOLDERS ) != 0;
    	} catch (MessagingException e) {
			LOGGER.error("Error getting folder type", e);
		}
    	return false;
    }    
	
    public String getFolderTypeName(){
		if ( isInboxFolder() ) {
			return IMailConstants.INBOX_FOLDER_NAME;
		} else if ( isTrashFolder() ) {
			return IMailConstants.TRASH_FOLDER_NAME;
		} else if ( isSentFolder() ) {
			return IMailConstants.SENT_FOLDER_NAME;
		} else if ( isSpamFolder() ) {
			return IMailConstants.SPAM_FOLDER_NAME;
		} else if ( isDraftFolder() ) {
			return IMailConstants.DRAFT_FOLDER_NAME;
		}
    	return IMailConstants.OTHER_FOLDER_NAME;
    }

    public boolean isInboxFolder() {
    	return IMailConstants.INBOX_FOLDER_NAME.equals(this.fullName);	
    }
    
    public boolean isDraftFolder() {
    	return getServer().getDraftFolderName().equals(this.fullName);	
    }

    public boolean isSentFolder() {
    	return getServer().getSentFolderName().equals(this.fullName);	
    }

    public boolean isSpamFolder() {
    	return getServer().getSpamFolderName().equals(this.fullName);	
    }

    public boolean isTrashFolder() {
    	return getServer().getTrashFolderName().equals(this.fullName);	
    }
	
}
