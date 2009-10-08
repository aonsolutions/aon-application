package com.code.aon.webmail.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.webmail.WebmailException;

public class AonFolder extends AonMessageSortableList {

    public static final String OTHER_FOLDER_NAME = "other";

	// Draft folder
    public static final String DRAFT_FOLDER_NAME = "Borrador";

    // Trash folder
    public static final String TRASH_FOLDER_NAME = "Papelera";

    // Sent Items folder decloration
    public static final String SENT_FOLDER_NAME = "Enviados";

    // Sent Items folder decloration
    public static final String INBOX_FOLDER_NAME = "INBOX";

    // Spam folder decloration
    public static final String SPAM_FOLDER_NAME = "spam";
	
	private static final Logger LOGGER = Logger.getLogger(AonFolder.class.getName());
	
	private AonServer server;

	public AonFolder(Folder folder, AonServer server) {
		this( folder, server, false );
	}

	public AonFolder(Folder folder, AonServer server, boolean sortable) {
		super(DATE_COLUMN,folder, sortable);
		this.server = server;		
	}
	
	public AonServer getServer() {
		return server;
	}

    public void refresh() throws WebmailException {
    	refreshMessageList();
       	sort();	
    }

	public ArrayList<AonFolder> getFolderList() throws WebmailException {
		ArrayList<AonFolder> folderList = null;
		try {
            Folder[] folders = folder.list();
            folderList = new ArrayList<AonFolder>(folders.length);
            for (int i = 0; i < folders.length; i++) {
                if (folders[i] != null) {
                	folderList.add(new AonFolder(folders[i],getServer()));
                }
            }
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE, "Folder list " + folder.getName(), e);
			throw new WebmailException(e);
		}
		return folderList;
	}

	public void deleteFolder(boolean content) throws WebmailException{
		try {
			close(false);
			folder.delete(content);
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE,"Deleting folder failed", e);
			throw new WebmailException(e);
		}
	}
	
	private synchronized void refreshMessageList() throws WebmailException {
		try {
			open(Folder.READ_WRITE);
			Message[] messages = folder.getMessages();
			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.FLAGS);
			profile.add(FetchProfile.Item.ENVELOPE);
			folder.fetch(messages, profile);

			int realLength = 0;
			AonMessage[] list = new AonMessage[messages.length];
			for (int i = 0; i < messages.length; i++) {
				if (messages[i] != null && !messages[i].isExpunged()) {
					AonMessage aonMessage = new AonMessage();
                	aonMessage.setParent(this);
                	aonMessage.setMessage((MimeMessage)messages[i]);
                	list[realLength++] = aonMessage;
                }
            }
			if ( realLength != messages.length ) {
				list = (AonMessage[]) ArrayUtils.subarray( list, 0, realLength);
			}
			setMessageList(list);
        } catch (MessagingException e) {
			LOGGER.log(Level.SEVERE,"Error reading messages ", e);
			throw new WebmailException(e);
        }
    }

    public boolean open(int mode){
    	try {
    		if (!folder.isOpen()){
    			folder.open(mode);
    		}
			return true;
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE,"Error opening folder " + folder.getName() + " in mode " +mode, e);
		}
		return false;
    }
    
    public boolean close(boolean mode){
    	try {
	    	folder.close(mode);
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE,"Error closing folder " + folder.getName() + " in mode " +mode, e);
		}
		return false;
    }


    public boolean isHoldFolders(){
    	try {
    		return ( folder.getType() & Folder.HOLDS_FOLDERS ) != 0;
    	} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE,"Error getting folder type", e);
		}
    	return false;
    }
    
    public boolean isHoldMessages() {
    	try {
    		return ( folder.getType() & Folder.HOLDS_MESSAGES ) != 0;
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE,"Error getting folder type", e);
		}
    	return false;    	
    }
    
	public boolean isRoot(){
		try {
			if (folder.getParent()==null) {
				return true;
			}
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE,"Error checking if is root", e);
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
		folder.copyMessages(messages, desfFolder);
        folder.setFlags(messages,new Flags(Flags.Flag.DELETED), true);
        folder.expunge();
        destinationFolder.close(true);
    }
    
    public void deleteMessages(AonMessage[] messagesToDelete) throws MessagingException{
    	for(int pos=0; pos<messagesToDelete.length; pos++){
    		messagesToDelete[pos].getMessage().setFlag(Flags.Flag.DELETED, true);
    		messagesToDelete[pos].setSelected(false);
    	}
        folder.expunge();
    }

    public int getMessageCount() throws WebmailException{
		try {
			if ( isHoldMessages() ) {			
				return folder.getMessageCount();
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
				count = folder.getUnreadMessageCount();				
			}
		} catch (MessagingException e) {
			LOGGER.log( Level.WARNING, "Error getting unread message count", e );
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
    	return folder.getName();
    }

    public String getFullName() {
    	return folder.getFullName();
    }
    
    public boolean isOpen(){
    	return folder.isOpen();
    }
    
    //**************************************************************
    // TYPE
    //**************************************************************

    public String getFolderTypeName(){
		if ( isInboxFolder() ) {
			return INBOX_FOLDER_NAME;
		} else if ( isTrashFolder() ) {
			return TRASH_FOLDER_NAME;
		} else if ( isSentFolder() ) {
			return SENT_FOLDER_NAME;
		} else if ( isSpamFolder() ) {
			return SPAM_FOLDER_NAME;
		} else if ( isDraftFolder() ) {
			return DRAFT_FOLDER_NAME;
		}
    	return OTHER_FOLDER_NAME;
    }

    public boolean isInboxFolder() {
    	return INBOX_FOLDER_NAME.equals( getFolder().getFullName() );	
    }
    
    public boolean isDraftFolder() {
    	return getServer().getDraftFolderName().equals( getFolder().getFullName() );	
    }

    public boolean isSentFolder() {
    	return getServer().getSentFolderName().equals( getFolder().getFullName() );	
    }

    public boolean isSpamFolder() {
    	return getServer().getSpamFolderName().equals( getFolder().getFullName() );	
    }

    public boolean isTrashFolder() {
    	return getServer().getTrashFolderName().equals( getFolder().getFullName() );	
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
}
