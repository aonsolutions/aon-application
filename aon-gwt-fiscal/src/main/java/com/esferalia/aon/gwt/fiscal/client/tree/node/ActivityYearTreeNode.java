package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class ActivityYearTreeNode extends TreeNode<Integer> {

	@Override
	public Integer getTreeObject() {
		return (Integer) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		fiscalTree.renderGenericContent(this);
	}

	@Override
	public ActivityYearTreeNode render(HasTreeItems parent,final Integer year) {
		InlineLabel label = new InlineLabel();
		label.setText(year.toString());
		label.addStyleName(AON.AON_CSS.aonTreeYear());
		setWidget(label);
		setUserObject(year);
		parent.addItem(this);
		return this;
	}
}
