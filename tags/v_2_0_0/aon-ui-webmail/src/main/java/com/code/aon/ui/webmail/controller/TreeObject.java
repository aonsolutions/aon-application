package com.code.aon.ui.webmail.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.swing.tree.DefaultMutableTreeNode;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.tree.IceUserObject;

public class TreeObject extends IceUserObject {

    private TreeController tree;

    private static String nodeToolTip;

    private AonFolder folder;
    
    public TreeObject(DefaultMutableTreeNode wrapper, TreeController tree) {
        super(wrapper);
        this.tree = tree;
        setTooltip(nodeToolTip);
        setExpanded(false);
    }

    /**
	 * @return the folder
	 */
	public AonFolder getFolder() {
		return folder;
	}


	/**
	 * @param folder the folder to set
	 */
	public void setFolder(AonFolder folder) {
		this.folder = folder;
        assignNodeIcon();
	}

    public void deleteNode(ActionEvent event) {
        ((DefaultMutableTreeNode) getWrapper().getParent()).remove(getWrapper());
    }

    /**
     * Registers a user click with this object and updates the selected node in the TreeBean.
     *
     * @param event that fired this method
     */
    public void nodeClicked(ActionEvent event) {
    	if (this.isLeaf()){
    		tree.setSelectedNodeObject(this);
    	}
    }
    
    public void purgeFolder(ActionEvent event) {
    	if (this.isLeaf()){
    		FolderController folderController = (FolderController)AonUtil.getRegisteredBean(AonConstants.BEAN_FOLDER);
    		tree.setSelectedNodeObject(this);
    		folderController.deleteAllMessages(event);
   			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, AonConstants.NAVIGATION_FOLDER);
    	}
    }
    
    public boolean isPurgable(){
		if (folder.getFolder().getFullName().equals(AonFolder.SPAM_FOLDER_NAME)){
			return true;
		}else if (folder.getFolder().getFullName().equals(AonFolder.TRASH_FOLDER_NAME)){
			return true;
		}
		return false;
    }
    
    @Override
    public String getText(){
        String name = folder.getFolder().getName();
        if (folder.isRoot()==true){
        	name = super.getText();
        }
    	return name;
    }
    
    public String getUnread(){
        if (folder.isLeaf() && folder.isRoot()==false){
        	int messages = 0;
			try {
				messages = folder.getFolder().getUnreadMessageCount();
			} catch (MessagingException e) {
			}
        	if (messages>0)
        		return " ("+messages+")";
        }
    	return null;
    }
    
    private void assignNodeIcon(){
		if (folder.getFolder().getFullName().equals(AonFolder.INBOX_FOLDER_NAME)){
			setBranchContractedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-inbox.png");
			setBranchExpandedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-inbox.png");
		}else if (folder.getFolder().getFullName().equals(AonFolder.SENT_FOLDER_NAME)){
			setBranchContractedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-sent.png");
			setBranchExpandedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-sent.png");
		}else if (folder.getFolder().getFullName().equals(AonFolder.TRASH_FOLDER_NAME)){
			setBranchContractedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-trash.png");
			setBranchExpandedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-trash.png");
		}else if (folder.getFolder().getFullName().equals(AonFolder.DRAFT_FOLDER_NAME)){
			setBranchContractedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-draft.png");
			setBranchExpandedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-draft.png");
		}else if (folder.getFolder().getFullName().equals(AonFolder.SPAM_FOLDER_NAME)){
			setBranchContractedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-spam.png");
			setBranchExpandedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-spam.png");
		}else{
			setBranchContractedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-folder.png");
			setBranchExpandedIcon("/css/iceCss/images/aon-icon/aon-icon-tree-folder.png");
		}
    }
    
    public void navigationDropAction(DropEvent event) {
        if (event.getEventType() == DndEvent.DROPPED) {
        	try {
        		AonMessage aonMessage = (AonMessage)event.getTargetDragValue();
        		FolderController folderController = (FolderController)AonUtil.getRegisteredBean(AonConstants.BEAN_FOLDER);
        		AonFolder sourceFolder = folderController.getFolder();
        		if (!sourceFolder.getSelectedMessages().contains(aonMessage)) {
            		aonMessage.setSelected(true);
        		}
        		List<AonMessage> messages = sourceFolder.getSelectedMessages();
        		if ( messages.size()>0 ) {
       				sourceFolder.moveMessages(messages, this.folder);
        		}
        		sourceFolder.refresh();
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (WebmailException e) {
				e.printStackTrace();
			}
        }
    }

}
