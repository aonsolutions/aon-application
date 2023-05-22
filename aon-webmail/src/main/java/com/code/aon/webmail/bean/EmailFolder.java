package com.code.aon.webmail.bean;

import java.io.Serializable;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;

public class EmailFolder implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
    	return StringUtils.equals(getServer().getDraftFolderName(), this.fullName);    	
    }

    public boolean isSentFolder() {
    	return StringUtils.equals(getServer().getSentFolderName(), this.fullName);    	
    }

    public boolean isSpamFolder() {
    	return StringUtils.equals(getServer().getSpamFolderName(), this.fullName);
    }

    public boolean isTrashFolder() {
    	return StringUtils.equals(getServer().getTrashFolderName(), this.fullName);
    }
	
}
