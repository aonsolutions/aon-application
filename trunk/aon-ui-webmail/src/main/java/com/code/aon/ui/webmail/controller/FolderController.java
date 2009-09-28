package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Flags.Flag;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.richfaces.event.DropEvent;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonMessageSortableList;
import com.code.aon.webmail.bean.AonServer;
import com.sun.mail.imap.IMAPFolder;

public class FolderController implements WebMailConstants {

	private static final Logger LOGGER = Logger.getLogger(FolderController.class.getName());
	
	private AonFolder folder;
	
	private ArrayDataModel model;
	
	private WebMailController webMailController;
	
	private FoldersTreeBean treeController;
	
	private boolean createAsSubfolder;
	
	private int currentPage = 1;
	
	public FolderController() {
		this.model = new ArrayDataModel();
	}

	public int getCurrentPage() {
		return currentPage;
	}
	
	public DataModel getModel() {
		AonMessage[] list = getFolder().getMessageList();
		if (! ObjectUtils.equals(list, this.model.getWrappedData()) ) {
			this.model.setWrappedData(list);
		}
		return this.model;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public void resetCurrentPage() {
		setCurrentPage( 1 );
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
   			deleteMessages(folder.getSelectedMessages(), false);
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    public void purgeCheckedMessages(ActionEvent event) {
    	try{
   			deleteMessages(folder.getSelectedMessages(), true);
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }
    
    public void purgeAllMessages(ActionEvent event) {
    	try {
	    	deleteMessages(folder.getMessageList(), true);
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    protected void deleteMessages(AonMessage[] messages, boolean purge) throws MessagingException {
    	AonServer server = getWebMailController().getServer();
		if ( purge ) {
			folder.deleteMessages(messages);
			try {
				folder.refresh();
			} catch (WebmailException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		} else {
	    	AonFolder treeDest = getTreeController().recoverTreeNode(server.getTrashFolderName());
	    	moveMessages(treeDest, messages);
	    	getTreeController().loadTree();
		}
    	resetCurrentPage();		
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
			nodeSelected( webMailController.getServer().getAonFolder(renameFolderName) );
	    	getTreeController().setCurrent(this.folder);			
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
		AonFolder parent = null;
		if ( isCreateAsSubfolder() ) {
			parent = getTreeController().getCurrent();
		}
       	getWebMailController().getServer().createAonFolder(parent, newFolderName, Folder.HOLDS_MESSAGES);
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
		    	getTreeController().initTree(getWebMailController().getServer());
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
		if (folder.isSentFolder() || folder.isDraftFolder())
			return true;
		return false;
	}

	//********************************************************************************************
	// DESTINY FOLDER SELECTION POPUP
	//********************************************************************************************
    public void moveSelectedMessages(AonFolder dest){
    	moveMessages(dest, folder.getSelectedMessages());
    }

    public void moveMessages(AonFolder dest, AonMessage[] messages){
		try {
			if (! ArrayUtils.isEmpty(messages) ) {
				folder.moveMessages(messages, dest);
			}
			try {
				folder.refresh();
				dest.refresh();
			} catch (WebmailException e) {
			}
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
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

	public void orderBySize(ActionEvent event) {
		try{
			orderBy(AonMessageSortableList.SIZE_COLUMN);
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
					if ( getFolder().getMessageCount() != getFolder().getMessageListCount() ) {
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
    	AonMessage aonMessage = getSelectedMessage();
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
	
    public AonMessage getSelectedMessage() {
    	return (AonMessage) getModel().getRowData();
    }

    public void markAsReadCheckedMessages(ActionEvent event) {
    	try{
    		for( AonMessage message : folder.getSelectedMessages() ) {
    			message.getMessage().setFlag( Flag.SEEN, true );
    		}
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    public void markAsUnreadCheckedMessages(ActionEvent event) {
    	try{
    		for( AonMessage message : folder.getSelectedMessages() ) {
    			message.getMessage().setFlag( Flag.SEEN, false );
    		}
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

	public boolean isCreateAsSubfolder() {
		return createAsSubfolder;
	}

	public void setCreateAsSubfolder(boolean createAsSubfolder) {
		this.createAsSubfolder = createAsSubfolder;
	}
    
}
