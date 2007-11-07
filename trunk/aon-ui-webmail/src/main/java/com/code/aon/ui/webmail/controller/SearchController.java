package com.code.aon.ui.webmail.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonMessageSortableList;
import com.code.aon.ui.webmail.bean.AonSearcher;
import com.code.aon.ui.webmail.exception.WebmailException;

public class SearchController {

	private AonMessageSortableList sortableList;
	
	private String bodyText;

	private String address_cc;

	private String address_from;

	private String subject;
	
	/**
	 * @return the sortableList
	 */
	public AonMessageSortableList getSortableList() {
		return sortableList;
	}

	/**
	 * @param sortableList the sortableList to set
	 */
	public void setSortableList(AonMessageSortableList sortableList) {
		this.sortableList = sortableList;
	}

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
		sortableList = null;
		bodyText = null;
		address_cc = null;
		address_from = null;
		subject = null;
	}
	
	public void searchMessagesCurrentFolder(ActionEvent event) {
		try{
			FolderController folderController = (FolderController)AonUtil.getRegisteredBean(AonConstants.BEAN_FOLDER);
			AonFolder sourceFolder = folderController.getFolder();
			sortableList = new AonMessageSortableList(sourceFolder.getFolder());
			AonSearcher as = new AonSearcher();
			as.setAonFolder(sourceFolder);
			if (bodyText!=null &&
					bodyText.trim().length()>0)
				as.addStringTerm(bodyText,AonSearcher.BODYTERM);
			if (address_cc!=null &&
					address_cc.trim().length()>0)
				as.addStringTerm(address_cc,AonSearcher.ADDRESS_CC);
			if (address_from!=null &&
					address_from.trim().length()>0)
				as.addStringTerm(address_from,AonSearcher.ADDRESS_FROM);
			if (subject!=null &&
					subject.trim().length()>0)
				as.addStringTerm(subject,AonSearcher.SUBJECT);
			sortableList.setMessageList(as.search());
	    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
	    	messageController.setReturnAction(AonConstants.NAVIGATION_SEARCH);
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public boolean isResultFound(){
		return !this.sortableList.getMessageList().isEmpty();
	}

	public boolean isResultSelected(){
		if (this.sortableList==null)
			return false;
		return true;
	}

}
