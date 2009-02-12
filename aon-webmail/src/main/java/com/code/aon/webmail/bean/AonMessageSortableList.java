package com.code.aon.webmail.bean;

import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.webmail.WebmailException;

public class AonMessageSortableList extends AonSortableList implements MessageCountListener,MessageChangedListener {

	private static final Logger LOGGER = Logger.getLogger(AonMessageSortableList.class.getName());

    public static String FROM_COLUMN = "from";   
    
    public static String TO_COLUMN = "to";
    
    public static String SUBJECT_COLUMN = "subject";
    
    public static String DATE_COLUMN = "date";
    
    private AonMessage[] messageList;

	protected Folder folder;
	
	public AonMessageSortableList(Folder folder) {
		this(AonMessageSortableList.DATE_COLUMN,folder);
	}

	public AonMessageSortableList(String column,Folder folder) {
		super(column);
		this.folder = folder;
		if (this.folder != null) {
			this.folder.addMessageCountListener(this);
			this.folder.addMessageChangedListener(this);
		}
	}

	/**
	 * @return the folder
	 */
	public Folder getFolder() {
		return folder;
	}


	/**
	 * @return the messageList
	 */
	public AonMessage[] getMessageList() {
		if (!oldSort.equals(sort) || oldAscending != ascending) {
			sort();
		}
		return messageList;
	}
	
	public int getMessageListCount() {
		return ArrayUtils.getLength(messageList);
	}

	/**
	 * @param messageList the messageList to set
	 */
	public void setMessageList(AonMessage[] messageList) {
		this.messageList = messageList;
	}

	@Override
	protected boolean isDefaultAscending(String column) {
		if (column.equals(SUBJECT_COLUMN)) {
			return true;
		} else if (column.equals(FROM_COLUMN)) {
			return true;
		} else if (column.equals(TO_COLUMN)) {
			return true;
		} else if (column.equals(DATE_COLUMN)) {
			return false;
		}
		return true;
	}

	protected void sort(){
		sort(getSort(), isAscending());
		oldSort = sort;
		oldAscending = ascending;
	}

	/**
	 * Sort the list.
	 */
	protected void sort(final String column, final boolean ascending) {
		if (messageList != null) {
			try {
				Comparator<AonMessage> comparator = AonMessageComparator.getComparator(column, ascending);
				Arrays.sort(messageList, comparator);
			} catch ( Throwable th ) {
				LOGGER.log(Level.SEVERE, "Error sorting message list", th);
			}
		}
	}
    
    /**
     * Invoked when messages are added into a folder.  
     */
    public void messagesAdded(MessageCountEvent messageCountEvent) {
    	//this.messageCountEvent = messageCountEvent;
    }

    /**
     * Invoked when messages are removed (expunged) from a folder. 
     */
    public void messagesRemoved(MessageCountEvent messageCountEvent) {
        if (messageCountEvent.getMessages() != null) {
            removeMessage(messageCountEvent.getMessages());
        }
    }
    
    protected synchronized void removeMessage(Message[] messages) {
        if (messages != null) {
        	boolean changed = false;
        	AonMessage[] list = getMessageList();
        	for( Message message : messages ) {
                AonMessage aonMessage = findMessage(message);
                if (aonMessage != null){
                    int index = ArrayUtils.indexOf( list, aonMessage );
                    if (index >= 0) {
                    	changed = true;
                    	list = (AonMessage[]) ArrayUtils.remove( list, index );
                    }
                }
            }
        	if ( changed ) {
        		setMessageList( list );
        	}
        }
    }

    protected synchronized AonMessage findMessage(Message message){
        if (message == null) {
            return null;
        }
        for (int i = this.messageList.length - 1; i >= 0 ; i--) {
            if (message.equals(messageList[i].getMessage())){
                return messageList[i];
            }
        }
        return null;
    }

    /**
     * Invoked when a message is changed.  The messageChangedEvent holds
     * the message that has been changed, this method must update respective
     * message in the messageList.
     * @throws WebmailException 
     */
    public void messageChanged(MessageChangedEvent messageChangedEvent){
        if (messageChangedEvent != null) {
            if (messageChangedEvent.getMessageChangeType()
                    == MessageChangedEvent.FLAGS_CHANGED ){
                Message changedMessage = messageChangedEvent.getMessage();
                AonMessage foundMessage = findMessage(changedMessage);
                if (foundMessage != null){
                    try {
                        foundMessage.setMessageFlag(changedMessage.getFlags());
                    } catch (MessagingException e) {
            			LOGGER.log(Level.SEVERE,"Error getting message flags",e);
                    }
                }
            }
        }
    }

}
