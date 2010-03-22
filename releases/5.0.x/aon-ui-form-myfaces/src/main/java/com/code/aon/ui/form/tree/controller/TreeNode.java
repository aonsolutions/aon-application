package com.code.aon.ui.form.tree.controller;

import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.code.aon.common.tree.ITreeNodeTO;

/**
 * TreeNode represents a node within a Tree.
 * 
 * @author Consulting & Development. euke - 14-mar-2006
 */
public class TreeNode extends TreeNodeBase {

	/** The object contained in the node */
	private ITreeNodeTO to;
	
	/** Indicates if the node has been executed */
	private boolean deployed = false;
	
	/**
	 * Creates a node with the especified identifier and the especified ITreeNodeTO.
	 * 
	 * @param to the ITreeNode object
	 * @param identifier the identifier of the node
	 */
	public TreeNode(String identifier,ITreeNodeTO to) {
		super();
		setIdentifier( identifier);
		setTo(to);
	}

	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	public ITreeNodeTO getTo() {
		return to;
	}

	/**
	 * Sets the to.
	 * 
	 * @param to the to
	 */
	public void setTo(ITreeNodeTO to) {
		this.to = to;
		setDescription(to.getLabel());
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	
	/* (non-Javadoc)
	 * @see org.apache.myfaces.custom.tree2.TreeNodeBase#getType()
	 */
	public String getType() {
		return to.getType().toString();
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	
	/* (non-Javadoc)
	 * @see org.apache.myfaces.custom.tree2.TreeNodeBase#getDescription()
	 */
	@Override
	public String getDescription() {
		return to.getLabel();
	}
	
	/**
	 * Checks if is deployed.
	 * 
	 * @return true, if is deployed
	 */
	public boolean isDeployed() {
		return deployed ;
	}
	
	/**
	 * Sets the deployed.
	 * 
	 * @param deployed the deployed
	 */
	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}
}
