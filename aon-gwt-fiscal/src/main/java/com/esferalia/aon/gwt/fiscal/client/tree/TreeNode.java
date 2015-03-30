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

	public abstract TreeNode<T> render(HasTreeItems parent,
			FiscalTree fiscalPanel, T t);

	public static void renderTree(Tree tree, FiscalTree fiscalPanel,
			Enterprise enterprise, boolean removeAll) {
		if (removeAll && tree.getItemCount() > 0) {
			tree.removeItems();
		}

		TreeNode<Enterprise> rootNode = TreeNodeTypes.ENTERPRISE.getInstance();
		rootNode.render(tree, fiscalPanel, enterprise);
		TreeNodeTypes.ENTERPRISE_DATA.getInstance().render(rootNode,fiscalPanel, enterprise);
		TreeNodeTypes.FISCAL_ACTIVITY_GROUP.getInstance().render(rootNode,fiscalPanel, enterprise);
		TreeNodeTypes.FISCAL_MODEL_GROUP.getInstance().render(rootNode,fiscalPanel, enterprise);
		rootNode.setState(true);
		tree.addItem(rootNode);
		tree.setSelectedItem(rootNode);
	}
}
