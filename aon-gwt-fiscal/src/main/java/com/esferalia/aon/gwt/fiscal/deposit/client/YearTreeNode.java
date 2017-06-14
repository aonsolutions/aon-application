package com.esferalia.aon.gwt.fiscal.deposit.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class YearTreeNode extends TreeNode<Integer> {


	@Override
	public Integer getTreeObject() {
		return (Integer) getUserObject();
	}

	@Override
	public void select(final Deposit deposit) {
		
	}

	@Override
	public YearTreeNode render(HasTreeItems parent,final Integer year) {
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.fiscalYear() + " " + year.toString());
		label.addStyleName(AON.AON_CSS.aonTreeModel());
		setWidget(label);
		setUserObject(year);
		parent.addItem(this);
		return this;
	}

}
