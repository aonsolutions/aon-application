package com.code.aon.ui.form.tree.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.shared_tomahawk.util.MessageUtils;

import com.code.aon.common.tree.ITreeNodeTO;
import com.code.aon.common.tree.enumeration.TreeNodeType;

/**
 * AbstractTreeController is a controller that handles tree structures.
 */
public abstract class AbstractTreeController {

	/** Key to identify an aon error. (value is ""aon_error"") */
	public static final String AON_ERROR = "aon_error";

	/** The tree component. */
	private HtmlTree treeComponent;

	/** The tree model. */
	protected TreeModel treeModel;

	/** The parent node. */
	private TreeNode parent;

	/** The current node. */
	private TreeNode currentNode;

	/** The root node. */
	private TreeNode rootNode;

	/** The nodes. */
	private HashMap<Integer, TreeNode> nodes = new HashMap<Integer, TreeNode>();

	/** Is new. */
	private boolean isNew = false;

	/**
	 * Gets the tree component.
	 * 
	 * @return the tree component
	 */
	public HtmlTree getTreeComponent() {
		return treeComponent;
	}

	/**
	 * Sets the tree component.
	 * 
	 * @param treeComponent the tree component
	 */
	public void setTreeComponent(HtmlTree treeComponent) {
		this.treeComponent = treeComponent;
	}

	/**
	 * Gets the tree model.
	 * 
	 * @return the tree model
	 */
	public TreeModel getTreeModel() {
		try {
			if (treeModel == null) {
				treeModel = initializeTreeModel();
			}
			return treeModel;
		} catch (AonTreeException e) {
			String[] args = { e.getMessage() };
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, AON_ERROR,
					args);
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	/**
	 * Gets the node selected.
	 * 
	 * @return the node selected
	 */
	public TreeNode getNodeSelected() {
		TreeNode node = (TreeNode) this.treeComponent.getNode();
		return node;
	}

	/**
	 * Clear nodes map.
	 */
	protected void clearNodesMap() {
		nodes.clear();
	}

	/**
	 * Creates a node using to and linked with parent.
	 * 
	 * @param to the to
	 * @param parent the parent
	 * 
	 * @return the tree node
	 */
	@SuppressWarnings("unchecked")
	public TreeNode createNode(ITreeNodeTO to, TreeNode parent) {
		StringBuffer parentId = new StringBuffer();
		if (parent != null) {
			parentId.append(parent.getIdentifier());
			parentId.append(TreeModel.SEPARATOR);
			parentId.append(parent.getChildCount());
		}
		TreeNode node = new TreeNode(parentId.toString(), to);
		if (to.getParent() != null) {
			List children = parent.getChildren();
			children.add(node);
		}
		registerNode(node);
		return node;
	}

	/**
	 * Register node.
	 * 
	 * @param node the node
	 */
	protected void registerNode(TreeNode node) {
		nodes.put(node.getTo().getId(), node);
	}

	/**
	 * Gets the current node's parent.
	 * 
	 * @return the current node's parent.
	 */
	protected TreeNode getCurrentNodeParent() {
		return parent;
	}

	/**
	 * Gets current <code>treeNode</code>'s parent.
	 * 
	 * @param treeNode the tree node
	 * 
	 * @return the parent
	 */
	protected TreeNode getParent(ITreeNodeTO treeNode) {
		ITreeNodeTO parentNode = treeNode.getParent();
		return getNode(parentNode.getId());
	}

	/**
	 * Gets the node.
	 * 
	 * @param identifier the identifier
	 * 
	 * @return the node
	 */
	public TreeNode getNode(Integer identifier) {
		return nodes.get(identifier);
	}

	/**
	 * Gets the current node.
	 * 
	 * @return the current node
	 */
	public TreeNode getCurrentNode() {
		return currentNode;
	}

	/**
	 * Sets the current node.
	 * 
	 * @param currentNode the current node
	 */
	public void setCurrentNode(TreeNode currentNode) {
		this.currentNode = currentNode;
		setTo((currentNode == null) ? null : currentNode.getTo());
	}

	/**
	 * ActionListener that will be fired when a node is selected
	 * 
	 * @param event the ActionEvent
	 */
	public void onNodeSelected(ActionEvent event) {
		getTreeComponent().setNodeSelected(event);
		setCurrentNode((TreeNode) getTreeComponent().getNode());
		setNew(false);
	}

	/**
	 * ActionListener that will be fired when a node is added
	 * 
	 * @param event the ActionEvent
	 */
	@SuppressWarnings("unused")
	public void onAddNode(ActionEvent event) {
		try {
			parent = getCurrentNode();
			setCurrentNode(null);
			setNew(true);
			ITreeNodeTO to = createITreeNodeTO();
			to.setParent(parent.getTo());
			to.setType(TreeNodeType.LEAF);
			setTo(to);
		} catch (AonTreeException e) {
			String[] args = { e.getMessage() };
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, AON_ERROR,
					args);
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	/**
	 * Checks if is new.
	 * 
	 * @return true, if is new
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * Sets the new.
	 * 
	 * @param isNew
	 */
	protected void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * Gets the root node.
	 * 
	 * @return the root node
	 */
	public TreeNode getRootNode() {
		return rootNode;
	}

	/**
	 * Sets the root node.
	 * 
	 * @param rootNode the root node
	 */
	public void setRootNode(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * Creates the tree model.
	 * 
	 * @param rootNode the root node
	 */
	protected void createTreeModel(TreeNode rootNode) {
		setRootNode(rootNode);
		treeModel = new TreeModelBase(rootNode);
		registerNode(rootNode);
		setTo(rootNode.getTo());

	}

	/**
	 * Initialize tree model.
	 * 
	 * @return the tree model
	 * 
	 * @throws AonTreeException the aon tree exception
	 */
	protected TreeModel initializeTreeModel() throws AonTreeException {
		clearNodesMap();
		List<ITreeNodeTO> list = getList();
		for (ITreeNodeTO treeNode : list) {
			nodeAdded(treeNode);
			TreeNode node = null;
			if (treeNode.getParent() == null) {
				node = new TreeNode("0", treeNode);
				createTreeModel(node);
			} else {
				TreeNode parent = getNode(treeNode.getParent().getId());
				node = createNode(treeNode, parent);
			}
			collapseNode( node );
		}
		afterInitializeModel();
		selectRootNode();
		return treeModel;
	}

	/**
	 * Gets the parent description.
	 * 
	 * @return the parent description
	 */
	public String getParentDescription() {
		return parent == null ? "ROOT" : parent.getDescription();
	}

	/**
	 * Initialize controller.
	 */
	protected void initializeController() {
		Collection<TreeNode> c = nodes.values();
		for (TreeNode node : c) {
			String[] path = treeModel.getPathInformation(node.getIdentifier());
			treeComponent.collapsePath(path);
		}
		treeModel = null;
		currentNode = null;
		parent = null;
		setTo(null);
		isNew = false;
		nodes.clear();
	}

	/**
	 * Select root node.
	 */
	protected void selectRootNode() {
		String rootIdentifier = getRootNode().getIdentifier();
		String[] rootPath = treeModel.getPathInformation(rootIdentifier);
		getTreeComponent().expandPath(rootPath);
		getTreeComponent().setNodeId(rootIdentifier);
		getTreeComponent().setNodeSelected(null);
		setCurrentNode((TreeNode) getTreeComponent().getNode());
	}

	/**
	 * Gets the list.
	 * 
	 * @return the list
	 * 
	 * @throws AonTreeException the aon tree exception
	 */
	protected List<ITreeNodeTO> getList() throws AonTreeException {
		throw new UnsupportedOperationException(
				"This method must be implemented in child class!");
	}

	/**
	 * After initialize model.
	 * 
	 * @throws AonTreeException the aon tree exception
	 */
	protected void afterInitializeModel() throws AonTreeException {
		throw new UnsupportedOperationException(
				"This method must be implemented in child class!");
	}

	/**
	 * Node added.
	 * 
	 * @param treeNode the tree node
	 */
	@SuppressWarnings("unused")
	protected void nodeAdded(ITreeNodeTO treeNode) {
		throw new UnsupportedOperationException(
				"This method must be implemented in child class!");
	}

	/**
	 * Creates the I tree node TO.
	 * 
	 * @return the I tree node TO
	 * 
	 * @throws AonTreeException the aon tree exception
	 */
	protected ITreeNodeTO createITreeNodeTO() throws AonTreeException {
		throw new UnsupportedOperationException(
				"This method must be implemented in child class!");
	}

	/**
	 * Sets the to.
	 * 
	 * @param to the to
	 */
	@SuppressWarnings("unused")
	protected void setTo(ITreeNodeTO to) {
		throw new UnsupportedOperationException(
				"This methos must be implemented in child class!");
	}

	/**
	 * Expand node.
	 * 
	 * @param node the node
	 */
	protected void expandNode(TreeNode node) {
		String[] nodePath = getTreeModel().getPathInformation(
				node.getIdentifier());
		getTreeComponent().expandPath(nodePath);
	}
	
	/**
	 * Collapse node.
	 * 
	 * @param node the node
	 */
	protected void collapseNode(TreeNode node) {
		String[] nodePath = getTreeModel().getPathInformation(
				node.getIdentifier());
		getTreeComponent().collapsePath(nodePath);
	}
}
