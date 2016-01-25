package com.esferalia.aon.gwt.fiscal.deposit.client;

import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.TreeItem;

public abstract class TreeNode<T> extends TreeItem {

	
	public abstract void select(Deposit deposit);
	public abstract T getTreeObject();
	
	public void setTreeObject(T t) {
		setUserObject(t);
	}

	public abstract TreeNode<T> render(HasTreeItems parent,T t);

}
