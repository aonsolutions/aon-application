package com.code.aon.ui.document.tree;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.richfaces.model.TreeNode;

public class AonTreeNode<T> implements TreeNode<T> {
	
	private static final long serialVersionUID = -8044143981186926401L;

	private T data;
	private TreeNode<T> parent;
	
	private Map<Object, TreeNode<T>> childrenMap = new TreeMap<Object, TreeNode<T>>();
	
	public T getData() {
		return data;
	}

	public TreeNode<T> getChild(Object identifier) {
		return (TreeNode<T>) childrenMap.get(identifier);
	}

	public void addChild(Object identifier, TreeNode<T> child) {
		child.setParent(this);
		childrenMap.put(identifier, child);
	}

	public void removeChild(Object identifier) {
		TreeNode<T> treeNode = childrenMap.remove(identifier);
		if (treeNode != null) {
			treeNode.setParent(null);
		}
	}

	public void setData(T data) {
		this.data = data;
	}

	public TreeNode<T> getParent() {
		return parent;
	}

	public void setParent(TreeNode<T> parent) {
		this.parent = parent;
	}

	public Iterator<Map.Entry<Object, TreeNode<T>>> getChildren() {
		return childrenMap.entrySet().iterator();
	}

	public boolean isLeaf() {
		return childrenMap.isEmpty();
	}

}