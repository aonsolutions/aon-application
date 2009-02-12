package com.code.aon.ui.webmail.tree;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;

import org.apache.commons.lang.ObjectUtils;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.FolderController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonFolder;

public class FoldersTreeBean implements WebMailConstants {

	private static final Logger LOGGER = Logger.getLogger(FoldersTreeBean.class.getName());
	
	private String account = null;
	
	private TreeNode rootNode = null;

	private AonFolder current;

	private FolderController folderController;

	private void addNodes(TreeNode node) {
		AonFolder folder = (AonFolder) node.getData();
		ArrayList<AonFolder> lst;
		try {
			lst = folder.getFolderList();
			for (int i = 0; i < lst.size(); i++) {
				AonFolder aonFolder = lst.get(i);
				TreeNodeImpl nodeImpl = new TreeNodeImpl();
				nodeImpl.setData(aonFolder);
				node.addChild(new Integer(i + 1), nodeImpl);
			}
		} catch (WebmailException e) {
			throw new FacesException(e.getMessage(), e);
		}
	}

	public void initTree() {
		loadTree();
		setCurrent( (AonFolder) rootNode.getChild(1).getData() );
		getFolderController().nodeSelected( getCurrent() );
	}
	
	public void loadTree() {
		WebMailController webMailController = (WebMailController) AonUtil
				.getRegisteredBean(WebMailConstants.BEAN_WEBMAIL);
		account = webMailController.getServer().getAccount().getEmail();
		AonFolder folder = new AonFolder(webMailController.getServer()
				.getRoot());
		rootNode = new TreeNodeImpl();
		rootNode.setData(folder);
		addNodes(rootNode);
	}

	public TreeNode getTreeNode() {
		if (rootNode == null) {
			try{
				loadTree();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error loading folder tree", e);
			}
		}
		return rootNode;
	}

	public AonFolder recoverTreeNode(AonFolder aonFolder){
		AonFolder top = (AonFolder)rootNode.getData();
		ArrayList<AonFolder> lst;
		try {
			lst = top.getFolderList();
			for (int i = 0; i < lst.size(); i++) {
				AonFolder current = lst.get(i);
				String name = current.getName();
				if (aonFolder.getName().equals(name)){
					return current;
				}
			}
		} catch (WebmailException e) {
			throw new FacesException(e.getMessage(), e);
		}
		return null;
	}
	
	public void processSelection(NodeSelectedEvent event) {
		UITree tree = (UITree) event.getComponent();
		current = (AonFolder) tree.getRowData();
		getFolderController().nodeSelected(current);
	}

	public AonFolder getCurrent() {
		return current;
	}
	
	public void setCurrent(AonFolder current) {
		this.current = current;
	}

	public void moveMessagesToFolder(NodeSelectedEvent event) {
		UITree tree = (UITree) event.getComponent();
		AonFolder destinyFolder = (AonFolder) tree.getRowData();
   		getFolderController().moveSelectedMessages(destinyFolder);
   		getFolderController().resetCurrentPage();
	}

	public void moveMessageToFolder(NodeSelectedEvent event) {
		UITree tree = (UITree) event.getComponent();
		AonFolder destinyFolder = (AonFolder) tree.getRowData();
   		MessageController message = (MessageController)AonUtil.getRegisteredBean(WebMailConstants.BEAN_MESSAGE);
   		message.moveSelectedMessageAndMove(destinyFolder);
	}

	public String getAccount() {
		return account;
	}
	
	public boolean isTreeLoaded(){
		return rootNode==null?false:true;
	}

	public FolderController getFolderController() {
		if ( folderController == null ) {
	    	setFolderController( (FolderController)AonUtil.getRegisteredBean(BEAN_FOLDER) );
		}
		return folderController;
	}

	public void setFolderController(FolderController folderController) {
		this.folderController = folderController;
	}
	
	public Boolean adviseNodeSelected(UITree tree) {
		boolean selected = false;
		if ( tree.isRowAvailable() ) {
			AonFolder folder = (AonFolder) tree.getRowData();
			selected = ObjectUtils.equals(folder, getCurrent());
		}
		return selected;
	}
	

}