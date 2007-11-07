package com.code.aon.ui.webmail.bean;

import java.util.ArrayList;
import java.util.Collections;
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
import javax.mail.internet.MimeMessage;

import com.code.aon.ui.webmail.exception.WebmailException;

public class AonMessageSortableList extends AonSortableList implements MessageCountListener,MessageChangedListener {

	private static final Logger LOGGER = Logger.getLogger(AonMessageSortableList.class.getName());

	protected ArrayList<AonMessage> messageList = new ArrayList<AonMessage>();

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
	public ArrayList<AonMessage> getMessageList() {
		if (!oldSort.equals(sort) || oldAscending != ascending) {
			sort();
		}
		return messageList;
	}

	/**
	 * @param messageList the messageList to set
	 */
	public void setMessageList(ArrayList<AonMessage> messageList) {
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
		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2){
				AonMessage c1 = (AonMessage) o1;
				AonMessage c2 = (AonMessage) o2;
				if (column == null) {
					return 0;
				}
				try {
					if (column.equals(SUBJECT_COLUMN)) {
						int a = c1.getSubject().compareToIgnoreCase(c2.getSubject());
						int d = c2.getSubject().compareToIgnoreCase(c1.getSubject());
						return ascending ? a : d ;
					} else if (column.equals(FROM_COLUMN)) {
						int a = c1.getSender().compareToIgnoreCase(c2.getSender());
						int d = c2.getSender().compareToIgnoreCase(c1.getSender());
						return ascending ? a : d ;
					} else if (column.equals(TO_COLUMN)) {
						int a = c1.getRecipientsTo().compareToIgnoreCase(c2.getRecipientsTo());
						int d = c2.getRecipientsTo().compareToIgnoreCase(c1.getRecipientsTo());
						return ascending ? a : d ;
					} else if (column.equals(DATE_COLUMN)) {
						if (c1.getMessage().getSentDate() != null
								&& c2.getSentDate() != null) {
							int a = c1.getSentDate().compareTo(c2.getSentDate());
							int d = c2.getSentDate().compareTo(c1.getSentDate());
							return ascending ? a : d;
						} else {
							return 0;
						}
					} else {
						return 0;
					}
				} catch (MessagingException e) {
					LOGGER.log(Level.ALL,"Sort error", e);
					return 0;
				} catch (WebmailException e) {
					LOGGER.log(Level.ALL,"Sort error", e);
					return 0;
				}
			}
		};
		if (messageList != null) {
			Collections.sort(messageList, comparator);
		}
	}

    public static String FROM_COLUMN = "from";   
    
    public static String TO_COLUMN = "to";
    
    public static String SUBJECT_COLUMN = "subject";
    
    public static String DATE_COLUMN = "date";

    
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
    	//this.messageCountEvent = messageCountEvent;
    }
    
    protected synchronized void removeMessage(Message[] messages) {
        if (messages != null) {
            Message message;
            AonMessage aonMessage;
            for (int i = messages.length - 1; i >= 0; i--) {
                message = messages[i];
                aonMessage = findMessage(message);
                if (aonMessage != null){
                    int index = messageList.indexOf(aonMessage);
                    if (index >= 0){
                        messageList.remove(index);
                    }
                }
            }
        }
    }

    protected synchronized AonMessage findMessage(Message message){
        if (message == null)
            return null;
        for (int i = this.messageList.size() - 1; i >= 0 ; i--) {
            if (message.equals(messageList.get(i).getMessage())){
                return messageList.get(i);
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
            			LOGGER.log(Level.ALL,"Error getting message flags",e);
                    }
                }
            }
        }
    }

}
