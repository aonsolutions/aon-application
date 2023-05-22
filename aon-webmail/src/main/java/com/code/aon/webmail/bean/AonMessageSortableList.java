package com.code.aon.webmail.bean;

import java.util.Arrays;
import java.util.Comparator;

import jakarta.mail.Folder;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;

public class AonMessageSortableList extends AonSortableList {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(AonMessageSortableList.class);

    public static String FROM_COLUMN = "from";   
    
    public static String TO_COLUMN = "to";
    
    public static String SUBJECT_COLUMN = "subject";
    
    public static String DATE_COLUMN = "date";
    
    public static String SIZE_COLUMN = "size";
    
    private AonMessage[] messageList;

	private AonServer server;
    
	private transient Folder folder;
	
	private String fullName;
	
	private boolean sortable;
	
	public AonMessageSortableList(String column, AonServer server, Folder folder, boolean sortable) {
		super(column);
		this.sortable = sortable;
		this.server = server;
		this.folder = folder;
		this.fullName = folder.getFullName();
	}

	/**
	 * @return the folder
	 */
	public Folder getFolder() {
		if ( folder == null ) {
			folder = server.getFolder(fullName);
		}
		return folder;
	}

	public AonServer getServer() {
		return server;
	}	

	public String getName() {
    	return folder.getName();
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
		if ( messageList!=null && sortable) {
			try {
				Comparator<AonMessage> comparator = AonMessageComparator.getComparator(column, ascending);
				Arrays.sort(messageList, comparator);
			} catch ( Throwable th ) {
				LOGGER.error("Error sorting message list", th);
			}
		}
	}

}
