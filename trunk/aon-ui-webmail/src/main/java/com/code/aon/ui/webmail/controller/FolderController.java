package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.mail.Folder;
import javax.mail.MessagingException;

import org.richfaces.component.html.HtmlDatascroller;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.ui.webmail.listener.ITreeListener;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;

public class FolderController implements ITreeListener{

	private static final Logger LOGGER = Logger.getLogger(FolderController.class.getName());
	
	private AonFolder folder;
	
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

	public void nodeSelected(AonFolder selected){
		setFolder(selected);
		htmlDatascroller = null;
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
		//if (currentPageObjects().size()==0)
			//paginator.gotoPreviousPage();
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
    	if (htmlDatascroller!=null)
    		currentPage = htmlDatascroller.getPageIndex();
    	currentPage--;
    	Object[] allMessages = folder.getMessageList().toArray();
    	for (int i = currentPage*pageObjectNumber;i < (currentPage*pageObjectNumber+pageObjectNumber); i++){
    		if (i < allMessages.length)
    			messages.add((AonMessage)allMessages[i]);
    	}
    	return messages;
    }

    private HtmlDatascroller htmlDatascroller = null;
    
	public void dataScrollActionListener(ActionEvent event) {
    	htmlDatascroller = (HtmlDatascroller) event.getComponent();
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
	    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
			Folder newFolder = webMailController.getServer().getRoot().getFolder(renameFolderName);
			this.folder.getFolder().renameTo(newFolder);
			this.folder = webMailController.getServer().getAonFolder(renameFolderName);
	    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
	    	treeBean.loadTree();
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

	
    //*************************************************************
    // NEW FOLDER
    //*************************************************************

	public void createFolder(ActionEvent event) {
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
       	webMailController.getServer().createAonFolder(null, newFolderName, Folder.HOLDS_MESSAGES);
    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
    	treeBean.loadTree();
    }

    //*************************************************************
    // DEL FOLDER
    //*************************************************************
	public void deleteFolder(ActionEvent event) {
		try{
			if (this.folder.getMessageCount()==0){
				this.folder.deleteFolder(true);
				this.folder = null;
		    	FoldersTreeBean treeBean = (FoldersTreeBean)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
		    	treeBean.loadTree();
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
				folder.getFolder().getFullName().equals(AonFolder.DRAFT_FOLDER_NAME))
			return true;
		return false;
	}

	//********************************************************************************************
	// DESTINY FOLDER SELECTION POPUP
	//********************************************************************************************
    private boolean showFoldersPanelPopup;
    
	public boolean isShowFoldersPanelPopup() {
		return showFoldersPanelPopup;
	}

	public void closeFoldersPanelPopup(ActionEvent event){
		this.showFoldersPanelPopup = false;
	}

	public void openFoldersPanelPopup(ActionEvent event){
		this.showFoldersPanelPopup = true;
	}

    public void moveSelectedMessages(AonFolder dest){
		try {
			List<AonMessage> messages = folder.getSelectedMessages();
			if ( messages.size()>0 ) {
					folder.moveMessages(messages, dest);
			}
			folder.refresh();
			closeFoldersPanelPopup(null);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (WebmailException e) {
			e.printStackTrace();
		}
    }

}
