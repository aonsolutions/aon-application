package com.code.aon.ui.webmail.tree;

import java.util.ArrayList;

import javax.faces.FacesException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.FolderController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonServer;

public class FoldersTreeBean implements WebMailConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(FoldersTreeBean.class);
	
	private TreeNode rootNode;

	private AonFolder current;

	private FolderController folderController;

	private void addNodes(TreeNode node) {
		AonFolder folder = (AonFolder) node.getData();
		String name = folder.getName();
		ArrayList<AonFolder> lst;
		try {
			lst = folder.getFolderList();
			for (int i = 0; i < lst.size(); i++) {
				AonFolder aonFolder = lst.get(i);
				TreeNodeImpl nodeImpl = new TreeNodeImpl();
				nodeImpl.setData(aonFolder);
				String id = aonFolder.getFolderTypeName();
				if ( AonFolder.OTHER_FOLDER_NAME.equals(id) ) {
					id = name + i;
				}
				node.addChild(id, nodeImpl);
				if ( aonFolder.isHoldFolders() ) {
					addNodes( nodeImpl );
				}
			}
		} catch (WebmailException e) {
			throw new FacesException(e.getMessage(), e);
		}
	}

	public void initTree( AonServer server ) {
		loadTree( server );
		setCurrent( (AonFolder) rootNode.getChild(AonFolder.INBOX_FOLDER_NAME).getData() );
		getFolderController().nodeSelected( getCurrent() );
	}
	
	public void loadTree() {
		WebMailController webMailController = (WebMailController) AonUtil
				.getRegisteredBean(WebMailConstants.BEAN_WEBMAIL);
		loadTree( webMailController.getServer() );
	}
	
	public void loadTree( AonServer server ) {
		AonFolder folder = new AonFolder(server.getRoot(), server);
		rootNode = new TreeNodeImpl();
		rootNode.setData(folder);
		addNodes(rootNode);
	}

	public TreeNode getTreeNode() {
		if (rootNode == null) {
			try{
				loadTree();
			} catch (Exception e) {
				LOGGER.error( "Error loading folder tree", e);
			}
		}
		return rootNode;
	}

	public AonFolder recoverTreeNode(String folderName){
		AonFolder top = (AonFolder)rootNode.getData();
		ArrayList<AonFolder> lst;
		try {
			lst = top.getFolderList();
			for (int i = 0; i < lst.size(); i++) {
				AonFolder current = lst.get(i);
				if ( StringUtils.equals(folderName,current.getName()) ) {
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
   		message.moveSelectedMessage(destinyFolder);
   		getFolderController().updateModel();
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