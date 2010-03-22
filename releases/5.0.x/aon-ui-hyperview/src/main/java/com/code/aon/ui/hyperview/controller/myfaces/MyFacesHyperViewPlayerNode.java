package com.code.aon.ui.hyperview.controller.myfaces;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.code.aon.hyperview.model.HyperViewNode;
import com.code.aon.hyperview.player.ContextItem;
import com.code.aon.hyperview.player.IHyperViewPlayerNode;
import com.code.aon.ui.form.tree.controller.TreeNode;

/**
 * @author Consulting & Development. euke - 14-mar-2006
 * 
 */
public class MyFacesHyperViewPlayerNode extends TreeNode implements IHyperViewPlayerNode {

	private IHyperViewPlayerNode parent;

	private DefaultMutableTreeNode definitionNode;

	private String description;

	private Map<String, ContextItem> params = new HashMap<String, ContextItem>();

	public MyFacesHyperViewPlayerNode(String identifier,
			DefaultMutableTreeNode definitionNode) {
		super(identifier, (HyperViewNode) definitionNode.getUserObject());
		this.definitionNode = definitionNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.hyperview.controller.IHyperViewNode#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.hyperview.controller.IHyperViewNode#getDescription()
	 */
	@Override
	public String getDescription() {
		return description == null ? super.getDescription() : description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.hyperview.controller.IHyperViewNode#getDefinitionNode()
	 */
	public DefaultMutableTreeNode getDefinitionNode() {
		return definitionNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.hyperview.controller.IHyperViewNode#getHyperViewNode()
	 */
	public HyperViewNode getHyperViewNode() {
		return (HyperViewNode) definitionNode.getUserObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.hyperview.controller.IHyperViewNode#getParams()
	 */
	public Map<String, ContextItem> getParams() {
		return params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.hyperview.controller.IHyperViewNode#setParams(java.util.Map)
	 */
	public void setParams(Map<String, ContextItem> params) {
		this.params = params;
	}

	@Override
	public boolean isLeaf() {
//		if ( isDeployed() ) {
			return super.isLeaf();	
//		}
//		return (TreeNodeType.LEAF.toString().equals(getType()));
	}

	public boolean isHyperCube() {
		return getHyperViewNode().isHyperCube();
	}
	
	public IHyperViewPlayerNode getParent() {
		return parent;
	}

	public void setParent(IHyperViewPlayerNode parent) {
		this.parent = parent;
	}
	
}
