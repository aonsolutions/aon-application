package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.mail.Folder;
import javax.mail.MessagingException;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.ui.webmail.listener.ITreeListener;
import com.icesoft.faces.component.datapaginator.DataPaginator;

public class FolderController implements ITreeListener{

	private static final Logger LOGGER = Logger.getLogger(FolderController.class.getName());
	
	private AonFolder folder;
	
	private DataPaginator paginator;

	private int pageObjectNumber = 20;
	
	/**
	 * @return the folder
	 */
	public AonFolder getFolder() {
		if (this.folder!=null && !this.folder.isOpen())
			this.folder.open(Folder.READ_WRITE);
		return this.folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(AonFolder folder) {
		if (this.folder!=null && !this.folder.isRoot() && this.folder.isOpen()){
			this.folder.close(true);
		}
		this.folder = folder;
		if (this.folder!=null)
			initFolderSelection();
	}

	private void initFolderSelection(){
    	if (paginator!=null)
    		paginator.gotoFirstPage();
		paginator = null;
		try {
			folder.refresh();
		} catch (WebmailException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the pAGE_OBJECTS
	 */
	public int getPageObjectNumber() {
		return pageObjectNumber;
	}

	public void nodeSelected(TreeObject selectedNodeObject){
		setFolder(selectedNodeObject.getFolder());
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	messageController.setReturnAction(AonConstants.NAVIGATION_FOLDER);
	}

	public void refresh(ActionEvent event) {
		try{
			if (folder!=null)
				folder.refresh();
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void replyToCheckedMessage(ActionEvent event) {
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	messageController.setMessage(folder.getSelectedMessage());
    	messageController.replyToSelectedMessage(null);
    }

	public void replyToAllCheckedMessage(ActionEvent event) {
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	messageController.setMessage(folder.getSelectedMessage());
    	messageController.replyToAllMessage(null);
    }

	public void forwardCheckedMessage(ActionEvent event) {
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	messageController.setMessage(folder.getSelectedMessage());
    	messageController.forwardMessage(null);
    }

    public void deleteCheckedMessages(ActionEvent event) {
    	try{
   			deleteMessages(folder.getSelectedMessages());
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    public void deleteAllMessages(ActionEvent event) {
    	try{
	    	deleteMessages(folder.getMessageList());
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    private void deleteMessages(List<AonMessage> messagesLst) throws MessagingException {
		if ((folder.getFolder().getFullName().equals(AonFolder.TRASH_FOLDER_NAME))
				|| (folder.getFolder().getFullName().equals(AonFolder.SPAM_FOLDER_NAME))){
			folder.deleteMessages(messagesLst);
		}else{
	    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
	    	AonFolder dest = webMailController.getServer().getAonFolder(AonFolder.TRASH_FOLDER_NAME);
	    	folder.moveMessages(messagesLst, dest);
		}
		if (paginator!=null &&
				currentPageObjects().size()==0)
			paginator.gotoPreviousPage();
    }
    
	// *************************************************************************
	// SELECT / UNSELECT ALL 
	// *************************************************************************
    public void selectAllMessages(ActionEvent event){
    	Iterator<AonMessage> iter = folder.getMessageList().iterator();
    	while (iter.hasNext()){
    		iter.next().setSelected(true);
    	}
    }

    public void deselectAllMessages(ActionEvent event){
    	Iterator<AonMessage> iter = folder.getMessageList().iterator();
    	while (iter.hasNext()){
    		iter.next().setSelected(false);
    	}
    }
    
    public void selectAllPageMessages(ActionEvent event){
    	Iterator<AonMessage> iter = currentPageObjects().iterator();
    	while (iter.hasNext()){
    		iter.next().setSelected(true);
    	}
    }

    public void deselectAllPageMessages(ActionEvent event){
    	Iterator<AonMessage> iter = currentPageObjects().iterator();
    	while (iter.hasNext()){
    		iter.next().setSelected(false);
    	}
    }
    
    private List<AonMessage> currentPageObjects() {
    	List<AonMessage> messages = new ArrayList<AonMessage>();
    	int currentPage = 1;
    	if (paginator!=null)
    		currentPage = paginator.getPageIndex();
    	currentPage--;
    	Object[] allMessages = folder.getMessageList().toArray();
    	for (int i = currentPage*pageObjectNumber;i < (currentPage*pageObjectNumber+pageObjectNumber); i++){
    		if (i < allMessages.length)
    			messages.add((AonMessage)allMessages[i]);
    	}
    	return messages;
    }
    
    //*************************************************************
    // NEW FOLDER POPUP
    //*************************************************************
    
    private boolean showNewFolderPanelPopup;
    
	/**
	 * @return the showNewFolderPanelPopup
	 */
	public boolean isShowNewFolderPanelPopup() {
		return showNewFolderPanelPopup;
	}

	/**
	 * @param showNewFolderPanelPopup the showNewFolderPanelPopup to set
	 */
	public void setShowNewFolderPanelPopup(boolean showNewFolderPanelPopup) {
		this.showNewFolderPanelPopup = showNewFolderPanelPopup;
	}
    
    private String newFolderName;
    
    /**
	 * @return the newFolderName
	 */
	public String getNewFolderName() {
		return newFolderName;
	}

	/**
	 * @param newFolderName the newFolderName to set
	 */
	public void setNewFolderName(String newFolderName) {
		this.newFolderName = newFolderName;
	}

	public void closeNewFolderPanelPopup(ActionEvent event){
		this.showNewFolderPanelPopup = false;
	}

	public void openNewFolderPanelPopup(ActionEvent event){
		this.newFolderName = "";
		this.showNewFolderPanelPopup = true;
	}

    //*************************************************************
    // RENAME FOLDER POPUP
    //*************************************************************
    
    private boolean showRenameFolderPanelPopup;
    
	/**
	 * @return the showRenameFolderPanelPopup
	 */
	public boolean isShowRenameFolderPanelPopup() {
		return showRenameFolderPanelPopup;
	}

	/**
	 * @param showRenameFolderPanelPopup the showRenameFolderPanelPopup to set
	 */
	public void setShowRenameFolderPanelPopup(boolean showRenameFolderPanelPopup) {
		this.showRenameFolderPanelPopup = showRenameFolderPanelPopup;
	}
    
    private String renameFolderName;
    
    /**
	 * @return the renameFolderName
	 */
	public String getRenameFolderName() {
		return renameFolderName;
	}

	/**
	 * @param renameFolderName the renameFolderName to set
	 */
	public void setRenameFolderName(String renameFolderName) {
		this.renameFolderName = renameFolderName;
	}

	public void closeRenameFolderPanelPopup(ActionEvent event){
		this.showRenameFolderPanelPopup = false;
	}

	public void openRenameFolderPanelPopup(ActionEvent event){
		this.renameFolderName = "";
		this.showRenameFolderPanelPopup = true;
	}

    //*************************************************************
    // RENAME FOLDER
    //*************************************************************

	public void renameFolder(ActionEvent event){
		try{
			this.folder.close(false);
	    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
			Folder newFolder = webMailController.getServer().getRoot().getFolder(renameFolderName);
			this.folder.getFolder().renameTo(newFolder);
			this.folder = webMailController.getServer().getAonFolder(renameFolderName);
	    	TreeController treeController = (TreeController)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
	    	treeController.loadTree();
			closeRenameFolderPanelPopup(null);
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

	
    //*************************************************************
    // NEW FOLDER
    //*************************************************************

	public void createFolder(ActionEvent event) {
		try{
	    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
	       	webMailController.getServer().createAonFolder(null, newFolderName, Folder.HOLDS_MESSAGES);
	    	TreeController treeController = (TreeController)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
	    	treeController.loadTree();
	    	closeNewFolderPanelPopup(null);
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    //*************************************************************
    // DEL FOLDER
    //*************************************************************
	public void deleteFolder(ActionEvent event) {
		try{
			if (this.folder.getMessageCount()==0){
				this.folder.deleteFolder(true);
				this.folder = null;
		    	TreeController treeController = (TreeController)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
		    	treeController.loadTree();
			}
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    //*************************************************************
    // SELECTIONS
    //*************************************************************
	public boolean isFolderSelected(){
		return (folder!=null);
	}

    //*************************************************************
    // PAGINATOR
    //*************************************************************
	public void paginatorAction(ActionEvent event){
		paginator = (DataPaginator)event.getSource();
	}
	
    //*************************************************************
    // SENDER OR DESTINY COLUMN
    //*************************************************************
	public boolean isSentItemColumn(){
		if (folder.getFolder().getFullName().equals(AonFolder.SENT_FOLDER_NAME) ||
				folder.getFolder().getFullName().equals(AonFolder.DRAFT_FOLDER_NAME))
			return true;
		return false;
	}

	
}
