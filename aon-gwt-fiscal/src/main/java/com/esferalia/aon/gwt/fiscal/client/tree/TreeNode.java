package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.widget.OptionsToolbar;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.NewContextMenu;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.TreeItem;

abstract class TreeNode<T> extends TreeItem implements OptionsToolbar.Listener {

	NewContextMenu newContextMenu;
	
	public void setNewContextMenu(NewContextMenu newContextMenu) {
		this.newContextMenu = newContextMenu;
	}
	
	public abstract void select(FiscalTree fiscalPanel);
	public abstract T getTreeObject();
	
	public void setTreeObject(T t) {
		setUserObject(t);
	}

	public abstract TreeNode<T> render(HasTreeItems parent, FiscalTree fiscalTree,T t);

	@Override
	public void onNewButtonClick(ClickEvent event) {
	}

	@Override
	public void onPasteButtonClick(ClickEvent event) {
	}

	@Override
	public void onCopyButtonClick(ClickEvent event) {
	}

	@Override
	public void onDraftButtonClick(ClickEvent event) {
	}

	@Override
	public void onCollapseAllButtonClick(ClickEvent event) {
	}
}
