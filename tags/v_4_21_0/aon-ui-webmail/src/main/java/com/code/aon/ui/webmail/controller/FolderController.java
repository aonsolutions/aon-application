package com.code.aon.ui.webmail.controller;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Flags.Flag;

import org.apache.commons.lang.ArrayUtils;
import org.richfaces.event.DropEvent;
import org.richfaces.model.ModifiableModel;
import org.richfaces.model.Ordering;
import org.richfaces.model.SequenceDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.tree.FoldersTreeBean;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonMessageSortableList;
import com.code.aon.webmail.bean.AonServer;
import com.sun.mail.imap.IMAPFolder;

public class FolderController implements IMessageContainer, WebMailConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(FolderController.class);
	
	private static final int PAGE_SIZE = 20;
	
	private AonFolder folder;
	
	private ModifiableModel model;
	
	private WebMailController webMailController;
	
	private FoldersTreeBean treeController;
	
	private boolean createAsSubfolder;
	
	private int currentPage = 1;
	
	private int currentIndex;

	private String tableState;
	
	private Ordering dateOrder = Ordering.DESCENDING;
	
	public FolderController() {
		this.model = new ModifiableModel(new SequenceDataModel(), "to");
	}

	public int getCurrentPage() {
		return currentPage;
	}
	
	public int getPageSize() {
		return PAGE_SIZE;
	}
	
	public DataModel getModel() {
		return this.model;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public void resetCurrentPage() {
		setCurrentPage( 1 );
		updateModel();		
	}

	public void updateModel() {
		this.model.setWrappedData(folder.getMessageList());		
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
			LOGGER.error( "Error refreshing folder " + folder.getName(), e);
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
				resetCurrentPage();
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
				LOGGER.error( e.getMessage(), e);
			}
		} else {
	    	AonFolder treeDest = getTreeController().recoverTreeNode(server.getTrashFolderName());
	    	moveMessages(treeDest, messages);
	    	getTreeController().loadTree();
		}
    	resetCurrentPage();		
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
    	this.currentIndex = getModel().getRowIndex();
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

	public String getTableState() {
		return tableState;
	}

	public void setTableState(String tableState) {
		this.tableState = tableState;
	}

	public Ordering getDateOrder() {
		return dateOrder;
	}

	public void setDateOrder(Ordering dateOrder) {
		this.dateOrder = dateOrder;
	}
	
	public String getTableHeight() {
		int count = getFolder().getMessageListCount();
		int first = (getCurrentPage()-1) * getPageSize();
		int visible = Math.min( count-first, getPageSize());
		return ((visible * 26)+27) + "px";
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
}