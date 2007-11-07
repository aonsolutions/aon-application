package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.ui.webmail.listener.ITreeListener;

public class TreeController {

    private DefaultTreeModel model;

    private TreeObject selectedNodeObject = null;

    private List<ITreeListener> listeners = new ArrayList<ITreeListener>();
    
    public TreeController() throws WebmailException {
    }

    public void loadTree() throws WebmailException{
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
		
		AonFolder folder = new AonFolder(webMailController.getServer().getRoot());
		ArrayList<AonFolder> lst1 = folder.getFolderList();
        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode();
        TreeObject rootObject =
                new TreeObject(rootTreeNode, this);
        rootObject.setText("<b><i>"+webMailController.getServer().getAccount().getEmail()+"</i></b>");
        rootObject.setFolder(folder);
        rootObject.setExpanded(true);
        rootTreeNode.setUserObject(rootObject);

        model = new DefaultTreeModel(rootTreeNode);

        getFolderContent(rootTreeNode,lst1);
    }

    public void initTree() throws WebmailException{
    	setSelectedNodeObject(selectedNodeObject);
    }

    private void getFolderContent(DefaultMutableTreeNode treeNode,ArrayList<AonFolder> lst1) throws WebmailException{
		Iterator<AonFolder> iter1 = lst1.iterator(); 
        while (iter1.hasNext()){
        	AonFolder aonFolder = iter1.next();
            DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
            TreeObject branchObject =
                    new TreeObject(branchNode, this);
            branchObject.setLeaf(aonFolder.isLeaf());
            branchObject.setFolder(aonFolder);
            branchNode.setUserObject(branchObject);
            treeNode.add(branchNode);
    		if (aonFolder.getFolder().getFullName().equals(AonFolder.INBOX_FOLDER_NAME)){
    			selectedNodeObject = branchObject;
    		}
            if (!aonFolder.isLeaf())
            	getFolderContent(branchNode,aonFolder.getFolderList());
        }
    }

    /**
     * Gets the tree's default model.
     *
     * @return tree model.
     */
    public DefaultTreeModel getModel() {
        return model;
    }

    /**
     * Gets the tree node.
     *
     * @return the tree node
     */
    public TreeObject getSelectedNodeObject() {
        return selectedNodeObject;
    }

    /**
     * Sets the tree node.
     *
     * @param selectedNodeObject the new tree node
     */
    public void setSelectedNodeObject(TreeObject selectedNodeObject) {
    	if (this.selectedNodeObject != null)
    		this.selectedNodeObject.setExpanded(false);
        this.selectedNodeObject = selectedNodeObject;
        this.selectedNodeObject.setExpanded(true);
        nodeSelected();
    }

    private void nodeSelected(){
    	for (ITreeListener l: listeners) {
    		l.nodeSelected(this.selectedNodeObject);
    	}
    }

	/**
	 * @return the listeners
	 */
	public List<ITreeListener> getListeners() {
		return listeners;
	}

	/**
	 * @param listeners the listeners to set
	 */
	public void setListeners(List<ITreeListener> listeners) {
		this.listeners = listeners;
	}

}
