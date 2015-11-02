package com.esferalia.aon.gwt.template.client.marketplace.tree;

import com.esferalia.aon.gwt.template.client.marketplace.Marketplace;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.TreeItem;

public abstract class TreeNode<T> extends TreeItem {

	
	public abstract void select(Marketplace marketplace);
	public abstract T getTreeObject();
	
	public void setTreeObject(T t) {
		setUserObject(t);
	}

	public abstract TreeNode<T> render(HasTreeItems parent,T t);
	
	
	
}
