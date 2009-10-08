package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.mail.Folder;
import javax.mail.MessagingException;

import org.apache.commons.lang.ArrayUtils;
import org.richfaces.event.DropEvent;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.bean.AonMessageSortableList;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.sun.mail.imap.IMAPFolder;

public class FolderController implements WebMailConstants {

	private static final Logger LOGGER = Logger.getLogger(FolderController.class.getName());
	
	private AonFolder folder;
	
	private WebMailController webMailController;
	
	private FoldersTreeBean treeController;
	
	private int currentPage = 1;
	
	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public void resetCurrentPage() {
		setCurrentPage( 1 );
	}
	
   @PostConstruct
   public void initBean() {
	   getWebMailController().setFolderController(this);
	   getTreeController().setFolderController(this);
	   // Nos aseguramos que esta creado el managed bean de Login
	   AonUtil.getRegisteredBean(BEAN_LOGIN);
   }	

	/**
	 * @return the folder
	 */
	public AonFolder getFolder() {
		if (this.folder!=null && !this.folder.isOpen()) {
			this.folder.open(Folder.READ_WRITE);
		}
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
		try {
			folder.refresh();
		} catch (WebmailException e) {
			LOGGER.log(Level.SEVERE,"Error refreshing folder " + folder.getName(), e);
		}
	}
	
	public void nodeSelected(AonFolder selected){
		setFolder(selected);
		resetCurrentPage();
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(BEAN_MESSAGE);
    	messageController.setReturnAction(NAVIGATION_FOLDER);
	}

	public void refresh(ActionEvent event) {
		try{
			if (folder!=null) {
				folder.refresh();
			}
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

    public void deleteCheckedMessages(ActionEvent event) {
    	try{
   			deleteMessages(folder.getSelectedMessages());
   			resetCurrentPage();
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    public void deleteAllMessages(ActionEvent event) {
    	try{
	    	deleteMessages(folder.getMessageList());
	    	resetCurrentPage();
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    private void deleteMessages(AonMessage[] messagesLst) throws MessagingException {
		if ((folder.getFolder().getFullName().equals(AonFolder.TRASH_FOLDER_NAME))
				|| (folder.getFolder().getFullName().equals(AonFolder.SPAM_FOLDER_NAME))){
			folder.deleteMessages(messagesLst);
			try {
				folder.refresh();
			} catch (WebmailException e) {
			}
		}else{
	    	AonFolder dest = getWebMailController().getServer().getAonFolder(AonFolder.TRASH_FOLDER_NAME);
	    	AonFolder treeDest = getTreeController().recoverTreeNode(dest);
	    	moveSelectedMessages(treeDest);
	    	getTreeController().loadTree();
		}
    }

	// *************************************************************************
	// SELECT / UNSELECT ALL 
	// *************************************************************************
    public void selectAllMessages(ActionEvent event){
    	for( AonMessage message : folder.getMessageList() ) {
    		message.setSelected(true);
    	}
    }

    public void deselectAllMessages(ActionEvent event){
    	for( AonMessage message : folder.getMessageList() ) {
    		message.setSelected(false);
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
    	int currentPage = getCurrentPage();
    	currentPage--;
    	AonMessage[] allMessages = folder.getMessageList();
    	int pageObjectNumber = folder.getPageSize();
    	for (int i = currentPage*pageObjectNumber;i < (currentPage*pageObjectNumber+pageObjectNumber); i++){
    		if (i < allMessages.length) {
    			messages.add(allMessages[i]);
    		}
    	}
    	return messages;
    }

    //*************************************************************
    // NEW FOLDER POPUP
    //*************************************************************
    
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

	public void openNewFolderPanelPopup(ActionEvent event){
		this.newFolderName = "";
	}

    //*************************************************************
    // RENAME FOLDER POPUP
    //*************************************************************
    
    
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

	public void openRenameFolderPanelPopup(ActionEvent event){
		this.renameFolderName = "";
	}

    //*************************************************************
    // RENAME FOLDER
    //*************************************************************

	public void renameFolder(ActionEvent event){
		try{
			this.folder.close(false);
			Folder newFolder = getWebMailController().getServer().getRoot().getFolder(renameFolderName);
			this.folder.getFolder().renameTo(newFolder);
			this.folder = webMailController.getServer().getAonFolder(renameFolderName);
	    	getTreeController().loadTree();
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

	
    //*************************************************************
    // NEW FOLDER
    //*************************************************************

	public void createFolder(ActionEvent event) {
       	getWebMailController().getServer().createAonFolder(null, newFolderName, Folder.HOLDS_MESSAGES);
    	getTreeController().loadTree();
    }

    //*************************************************************
    // DEL FOLDER
    //*************************************************************
	public void deleteFolder(ActionEvent event) {
		try{
			if (this.folder.getMessageCount()==0){
				this.folder.deleteFolder(true);
				this.folder = null;
		    	getTreeController().loadTree();
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
    // SENDER OR DESTINY COLUMN
    //*************************************************************
	public boolean isSentItemColumn(){
		if (folder.getFolder().getFullName().equals(AonFolder.SENT_FOLDER_NAME) ||
				folder.isDraftFolder())
			return true;
		return false;
	}

	//********************************************************************************************
	// DESTINY FOLDER SELECTION POPUP
	//********************************************************************************************
    public void moveSelectedMessages(AonFolder dest){
		try {
			AonMessage[] messages = folder.getSelectedMessages();
			if (! ArrayUtils.isEmpty(messages) ) {
				folder.moveMessages(messages, dest);
			}
			try {
				folder.refresh();
				dest.refresh();
			} catch (WebmailException e) {
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }

    // *******************************************************
    // ORDER BY
    // *******************************************************
    
	public void orderBy(String column) throws WebmailException {
		folder.setSort(column);
		folder.setAscending(!folder.isAscending());
		folder.refresh();
	}

	public void orderByTo(ActionEvent event) {
		try{
			orderBy(AonMessageSortableList.TO_COLUMN);
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void orderByFrom(ActionEvent event) {
		try{
			orderBy(AonMessageSortableList.FROM_COLUMN);
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void orderBySubject(ActionEvent event) {
		try{
			orderBy(AonMessageSortableList.SUBJECT_COLUMN);
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void orderByDate(ActionEvent event) {
		try{
			orderBy(AonMessageSortableList.DATE_COLUMN);
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public boolean isRefreshNeeded() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();
		if ( parameters.containsKey("aonDesktop") && (getFolder() != null) ) {
			String name = getFolder().getName();
			if ( AonFolder.INBOX_FOLDER_NAME.equals(name) ) {
				try {
					if ( getFolder().getMessageCount() != getFolder().getModel().getRowCount() ) {
						getFolder().refresh();
						return true;
					}
				} catch (WebmailException e) {
					AonUtil.addErrorMessage(e.getMessage());
					throw new AbortProcessingException(e);
				}
			}
		}
		return false;
	}
	
	public void messagesDrop( DropEvent event ) {
		AonMessage message = (AonMessage) event.getDragValue();
		if (! message.isSelected() ) {
			message.setSelected(true);
		}
		AonFolder dest = (AonFolder) event.getDropValue();
		moveSelectedMessages(dest);
		resetCurrentPage();
	}
	
	public String getSelectedMessageAction() {
		if ( getFolder().isDraftFolder() ) {
			return NAVIGATION_MESSAGE_NEW;
		}
		return NAVIGATION_MESSAGE;
	}

    public void changeSelectedMessage(ActionEvent event) throws MessagingException {
    	AonMessage aonMessage = getFolder().getSelectedMessage();
    	MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
    	if ( getFolder().isDraftFolder() ) {
    		IMAPFolder imapFolder = (IMAPFolder) getFolder().getFolder();
    		long uid = imapFolder.getUID(aonMessage.getMessage());
    		messageController.editDraftMessage(aonMessage, uid);
    	} else {
	       	messageController.setMessage( aonMessage );      				
		}
    }

	public WebMailController getWebMailController() {
		if ( webMailController == null ) {
	    	setWebMailController( (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL) );
		}
		return webMailController;
	}

	public void setWebMailController(WebMailController webMailController) {
		this.webMailController = webMailController;
	}

	public FoldersTreeBean getTreeController() {
		if ( treeController == null ) {
	    	setTreeController( (FoldersTreeBean)AonUtil.getRegisteredBean(BEAN_TREE) );
		}
		return treeController;
	}

	public void setTreeController(FoldersTreeBean treeController) {
		this.treeController = treeController;
	}
	
}
