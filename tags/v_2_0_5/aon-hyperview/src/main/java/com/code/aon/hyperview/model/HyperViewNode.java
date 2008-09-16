package com.code.aon.hyperview.model;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.tree.ITreeNodeTO;
import com.code.aon.common.tree.enumeration.TreeNodeType;

public class HyperViewNode extends HyperViewContentsContainer implements ITreeNodeTO {

	private ITreeNodeTO parent;

	private TreeNodeType type = TreeNodeType.LEAF;

	private String label;

	private String iconOpen;

	private String iconClose;

	private List<HyperViewNode> children;

	private HyperViewSQLNode dynamicNode;

	private boolean hyperCube;

	public String getIconClose() {
		return iconClose;
	}

	public void setIconClose(String iconClose) {
		this.iconClose = iconClose;
	}

	public String getIconOpen() {
		return iconOpen;
	}

	public void setIconOpen(String iconOpen) {
		this.iconOpen = iconOpen;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<HyperViewNode> getChildren() {
		return children;
	}

	public void setChildren(List<HyperViewNode> children) {
		this.children = children;
	}

	public void addNode(HyperViewNode node) {
		if (this.children == null) {
			this.children = new LinkedList<HyperViewNode>();
		}
		node.setParent(this);
		this.setType(TreeNodeType.GROUP);
		this.children.add(node);
	}

	public void setDynamicNode(HyperViewSQLNode node) {
		this.dynamicNode = node;
	}

	public HyperViewSQLNode getDynamicNode() {
		return dynamicNode;
	}

	public boolean isDynamic() {
		return (getDynamicNode() != null);
	}

	public ITreeNodeTO getParent() {
		return parent;
	}

	public void setParent(ITreeNodeTO parent) {
		this.parent = parent;
	}

	public TreeNodeType getType() {
		return type;
	}

	public void setType(TreeNodeType type) {
		this.type = type;
	}

	public Integer getId() {
		return null;
	}
	
	public boolean isHyperCube() {
		return hyperCube;	
	}
	
	
	public void setHyperCube(boolean hyperCube) {
		this.hyperCube = hyperCube;	
	}
	
	public void activateHyperCube() {
		this.hyperCube = true;
	}
	public void hyperCubeContents() {
		//nothing
	}
	
}
