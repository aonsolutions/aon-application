package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

abstract class TreeNode<T> extends TreeItem {

	public abstract void select(FiscalTree fiscalPanel);
	public abstract T getTreeObject();
	
	public void setTreeObject(T t) {
		setUserObject(t);
	}

	public abstract TreeNode<T> render(HasTreeItems parent,FiscalTree fiscalTree, T t);

	public static void renderTree(Tree tree, FiscalTree fiscalTree,
			Enterprise enterprise, boolean removeAll) {
		if (removeAll && tree.getItemCount() > 0) {
			tree.removeItems();
		}
		TreeNode<Enterprise> rootNode = TreeNodeTypes.ENTERPRISE.getInstance();
		rootNode.render(tree, fiscalTree, enterprise);
		TreeNodeTypes.ENTERPRISE_DATA.getInstance().render(rootNode,fiscalTree, enterprise);
		TreeNodeTypes.FISCAL_ACTIVITY_GROUP.getInstance().render(rootNode,fiscalTree, enterprise);
		TreeNodeTypes.FISCAL_MODEL_YEAR_GROUP.getInstance().render(rootNode,fiscalTree, enterprise);
		rootNode.setState(true);
		tree.addItem(rootNode);
		tree.setSelectedItem(rootNode);
	}
}
