package com.code.aon.webmail.bean;

import java.util.ArrayList;
import java.util.List;

import jakarta.mail.FetchProfile;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.webmail.WebmailException;

public class AonFolder extends AonMessageSortableList {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(AonFolder.class);

	public AonFolder(Folder folder, AonServer server) {
		this( folder, server, false );
	}

	private AonFolder(Folder folder, AonServer server, boolean sortable) {
		super(DATE_COLUMN, server, folder, sortable);
	}

    public void refresh() throws WebmailException {
    	refreshMessageList();
       	sort();	
    }

	public ArrayList<AonFolder> getFolderList() throws WebmailException {
		ArrayList<AonFolder> folderList = null;
		try {
            Folder[] folders = getFolder().list();
            folderList = new ArrayList<AonFolder>(folders.length);
            for (int i = 0; i < folders.length; i++) {
                if (folders[i] != null) {
                	folderList.add(new AonFolder(folders[i],getServer()));
                }
            }
		} catch (MessagingException e) {
			String msg = "Folder list " + getFolder().getName();
			LOGGER.error(msg, e);
			throw new WebmailException(msg,e);
		}
		return folderList;
	}

	public void deleteFolder(boolean content) throws WebmailException{
		try {
			close(false);
			getFolder().delete(content);
		} catch (MessagingException e) {
			LOGGER.error("Deleting folder failed", e);
			throw new WebmailException(e);
		}
	}
	
	private synchronized void refreshMessageList() throws WebmailException {
		try {
			open(Folder.READ_WRITE);
			Message[] messages = getFolder().getMessages();
			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.FLAGS);
			profile.add(FetchProfile.Item.ENVELOPE);
			getFolder().fetch(messages, profile);

			int realLength = 0;
			AonMessage[] list = new AonMessage[messages.length];
			for (int i = 0; i < messages.length; i++) {
				if (messages[i] != null && !messages[i].isExpunged()) {
					AonMessage aonMessage = new AonMessage( (MimeMessage)messages[i] );
                	aonMessage.setParent(this);                	
                	list[realLength++] = aonMessage;
                }
            }
			if ( realLength != messages.length ) {
				list = (AonMessage[]) ArrayUtils.subarray( list, 0, realLength);
			}
			setMessageList(list);
        } catch (MessagingException e) {
			LOGGER.error("Error reading messages ", e);
			throw new WebmailException(e);
        }
    }

    public boolean open(int mode){
    	try {
    		if (!getFolder().isOpen()){
    			getFolder().open(mode);
    		}
			return true;
		} catch (MessagingException e) {
			LOGGER.error("Error opening folder {} in mode {}: {}",getFolder().getName(),mode);
		}
		return false;
    }
    
    public boolean close(boolean mode){
    	try {
	    	getFolder().close(mode);
		} catch (MessagingException e) {
			LOGGER.error("Error closing folder {} in mode ",getFolder().getName(),mode);
		}
		return false;
    }


    public boolean isHoldFolders(){
    	try {
    		return ( getFolder().getType() & Folder.HOLDS_FOLDERS ) != 0;
    	} catch (MessagingException e) {
			LOGGER.error("Error getting folder type", e);
		}
    	return false;
    }
    
    public boolean isHoldMessages() {
    	try {
    		return ( getFolder().getType() & Folder.HOLDS_MESSAGES ) != 0;
		} catch (MessagingException e) {
			LOGGER.error("Error getting folder type", e);
		}
    	return false;    	
    }
    
	public boolean isRoot(){
		try {
			if (getFolder().getParent()==null) {
				return true;
			}
		} catch (MessagingException e) {
			LOGGER.error("Error checking if is root", e);
		}
		return false;
	}

    public void moveMessages(AonMessage[] messagesToMove, AonFolder destinationFolder) throws MessagingException {
    	Message[] messages = new Message[messagesToMove.length];
    	for(int pos=0; pos<messagesToMove.length; pos++){
    		messages[pos] = messagesToMove[pos].getMessage();
    		messagesToMove[pos].setSelected(false);
    	}
    	destinationFolder.open(Folder.READ_WRITE);
    	Folder desfFolder = destinationFolder.getFolder();
		getFolder().copyMessages(messages, desfFolder);
        getFolder().setFlags(messages,new Flags(Flags.Flag.DELETED), true);
        getFolder().expunge();
        destinationFolder.close(true);
    }
    
    public void deleteMessages(AonMessage[] messagesToDelete) throws MessagingException{
    	for(int pos=0; pos<messagesToDelete.length; pos++){
    		messagesToDelete[pos].getMessage().setFlag(Flags.Flag.DELETED, true);
    		messagesToDelete[pos].setSelected(false);
    	}
        getFolder().expunge();
    }

    public int getMessageCount() throws WebmailException{
		try {
			if ( isHoldMessages() ) {			
				return getFolder().getMessageCount();
			}
			return 0;
		} catch (MessagingException e) {
			throw new WebmailException(e);
		}
    }
    
    public int getUnreadMessageCount() {
    	int count = 0;
		try {
			if ( isHoldMessages() ) {
				count = getFolder().getUnreadMessageCount();				
			}
		} catch (MessagingException e) {
			LOGGER.warn( "Error getting unread message count", e );
		}    	
		return count;
    }
    
    public boolean isDeleteable() throws WebmailException{
    	if (isRoot()){
    		return false;
    	}else if (getMessageCount()!=0){
    		return false;
    	} else if (isTrashFolder() || isInboxFolder() || isSentFolder() || isSpamFolder()) {
    		return false;
    	}
    	return true;
    }

    public boolean isRenameable() {
    	if (isRoot()){
    		return false;
    	} else if (isTrashFolder() || isInboxFolder() || isSentFolder() || isSpamFolder()) {
    		return false;
    	}
    	return true;
    }

    public boolean isPurgableAllMessages() {
    	if ( isTrashFolder() || isSpamFolder() ) {
    		return true;
    	}
    	return false;
    }

    public String getName() {
    	return getFolder().getName();
    }

    public String getFullName() {
    	return getFolder().getFullName();
    }
    
    public boolean isOpen(){
    	return getFolder().isOpen();
    }
    
    //**************************************************************
    // TYPE
    //**************************************************************

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
    	return IMailConstants.INBOX_FOLDER_NAME.equals( getFolder().getFullName() );	
    }
    
    public boolean isDraftFolder() {
    	return StringUtils.equals(getServer().getDraftFolderName(), getFolder().getFullName());
    }

    public boolean isSentFolder() {
    	return StringUtils.equals(getServer().getSentFolderName(), getFolder().getFullName());    	
    }

    public boolean isSpamFolder() {
    	return StringUtils.equals(getServer().getSpamFolderName(), getFolder().getFullName());    	
    }

    public boolean isTrashFolder() {
    	return StringUtils.equals(getServer().getTrashFolderName(), getFolder().getFullName());    	
    }
    
    //**************************************************************
    // SELECTED ROWS
    //**************************************************************

    public AonMessage[] getSelectedMessages(){
    	List<AonMessage> selectedAonMessages = new ArrayList<AonMessage>();
    	for (AonMessage aonMessage : getMessageList()) {
    		if (aonMessage.isSelected()) {
    			selectedAonMessages.add(aonMessage);
    		}
		}
    	return selectedAonMessages.toArray(new AonMessage[selectedAonMessages.size()]);
    }
    
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AonFolder) {
			AonFolder f = (AonFolder) obj;
			if (!StringUtils.equals(getName(), f.getName())) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}    
	
}
