package com.code.aon.ui.webmail.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.FacesException;

import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.controller.FolderController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.ui.webmail.listener.ITreeListener;

public class FoldersTreeBean {

	private String account = null;
	
	private TreeNode rootNode = null;

	private AonFolder current;

	private List<ITreeListener> listeners = new ArrayList<ITreeListener>();

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

	public void loadTree() {
		WebMailController webMailController = (WebMailController) AonUtil
				.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
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
			}catch (Exception e) {
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
		nodeSelected(current);
	}

	public AonFolder getCurrent() {
		return current;
	}

	private void nodeSelected(AonFolder node) {
		for (ITreeListener l : listeners) {
			l.nodeSelected(node);
		}
	}

	/**
	 * @return the listeners
	 */
	public List<ITreeListener> getListeners() {
		return listeners;
	}

	/**
	 * @param listeners
	 *            the listeners to set
	 */
	public void setListeners(List<ITreeListener> listeners) {
		this.listeners = listeners;
	}

	public void moveMessagesToFolder(NodeSelectedEvent event) {
		UITree tree = (UITree) event.getComponent();
		AonFolder destinyFolder = (AonFolder) tree.getRowData();
   		FolderController folders = (FolderController)AonUtil.getRegisteredBean(AonConstants.BEAN_FOLDER);
   		folders.moveSelectedMessages(destinyFolder);
   		folders.resetCurrentPage();
	}

	public void moveMessageToFolder(NodeSelectedEvent event) {
		UITree tree = (UITree) event.getComponent();
		AonFolder destinyFolder = (AonFolder) tree.getRowData();
   		MessageController message = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
   		message.moveSelectedMessageAndMove(destinyFolder);
	}

	public String getAccount() {
		return account;
	}
	
	public boolean isTreeLoaded(){
		return rootNode==null?false:true;
	}
}