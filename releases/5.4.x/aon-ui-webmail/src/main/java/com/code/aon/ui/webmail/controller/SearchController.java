package com.code.aon.ui.webmail.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.mail.MessagingException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.model.ModifiableModel;
import org.richfaces.model.Ordering;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.MessageDataModel;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonSearcher;

public class SearchController implements IMessageContainer, WebMailConstants {

	private ModifiableModel model;
	
	private Ordering[] sortOrders;
	
	private String bodyText;

	private String address_cc;

	private String address_from;

	private String subject;
	
	private int currentIndex;
	
	private boolean messagesFound;
	
	private boolean showResults;
	
	/**
	 * @return the bodyText
	 */
	public String getBodyText() {
		return bodyText;
	}

	/**
	 * @param bodyText the bodyText to set
	 */
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}

	
	
	/**
	 * @return the address_cc
	 */
	public String getAddress_cc() {
		return address_cc;
	}

	/**
	 * @param address_cc the address_cc to set
	 */
	public void setAddress_cc(String address_cc) {
		this.address_cc = address_cc;
	}

	/**
	 * @return the address_from
	 */
	public String getAddress_from() {
		return address_from;
	}

	/**
	 * @param address_from the address_from to set
	 */
	public void setAddress_from(String address_from) {
		this.address_from = address_from;
	}
	
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void init(ActionEvent event){
		bodyText = null;
		address_cc = null;
		address_from = null;
		subject = null;
		sortOrders = new Ordering[]{ Ordering.UNSORTED, Ordering.UNSORTED, Ordering.DESCENDING };
		setShowResults(false);
		setMessagesFound(false);
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(WebMailConstants.BEAN_MESSAGE);
    	messageController.setReturnAction(WebMailConstants.NAVIGATION_SEARCH);		
	}
	
	public void searchMessagesCurrentFolder(ActionEvent event) {
		try {
			setShowResults(false);
			FolderController folderController = (FolderController)AonUtil.getRegisteredBean(WebMailConstants.BEAN_FOLDER);
			AonFolder sourceFolder = folderController.getFolder();
			AonSearcher as = new AonSearcher();
			as.setAonFolder(sourceFolder);
			if (! StringUtils.isEmpty(bodyText) ) {
				as.addStringTerm(bodyText,AonSearcher.BODYTERM);
			}
			if (! StringUtils.isEmpty(address_cc) ) {
				as.addStringTerm(address_cc,AonSearcher.ADDRESS_CC);
			}
			if (! StringUtils.isEmpty(address_from) ) {
				as.addStringTerm(address_from,AonSearcher.ADDRESS_FROM);
			}
			if (! StringUtils.isEmpty(subject) ) {
				as.addStringTerm(subject,AonSearcher.SUBJECT);
			}
			AonMessage[] list = as.search();
			setMessagesFound(! ArrayUtils.isEmpty(list) );
			this.model = new MessageDataModel(list);
			setShowResults(true);
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onExit(ActionEvent event) {
		MessageController messageController = (MessageController)AonUtil.getRegisteredBean(WebMailConstants.BEAN_MESSAGE);
		messageController.setReturnAction(WebMailConstants.NAVIGATION_FOLDER);
	}
	
    public void changeSelectedMessage(ActionEvent event) throws MessagingException {
    	AonMessage aonMessage = (AonMessage) getModel().getRowData();
    	this.currentIndex = getModel().getRowIndex();
    	MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
       	messageController.setMessage( aonMessage );      				
    }	
    
    public DataModel getModel() {
    	return this.model;
    }

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public boolean isShowResults() {
		return showResults;
	}

	public void setShowResults(boolean showResults) {
		this.showResults = showResults;
	}

	public boolean isMessagesFound() {
		return messagesFound;
	}

	public void setMessagesFound(boolean messagesFound) {
		this.messagesFound = messagesFound;
	}

	public Ordering[] getSortOrders() {
		return sortOrders;
	}

	public void setSortOrders(Ordering[] sortOrders) {
		this.sortOrders = sortOrders;
	}
	
}
