package com.esferalia.aon.gwt.fiscal.deposit.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class YearTreeNode extends TreeNode<EnterpriseYear> {


	@Override
	public EnterpriseYear getTreeObject() {
		return (EnterpriseYear) getUserObject();
	}

	@Override
	public void select(final Deposit deposit) {
		
	}

	@Override
	public YearTreeNode render(HasTreeItems parent,final EnterpriseYear enterpriseYear) {
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.fiscalYear() + " " + enterpriseYear.getYear().toString());
		label.addStyleName(AON.AON_CSS.aonTreeModel());
		setWidget(label);
		setUserObject(enterpriseYear);
		parent.addItem(this);
		return this;
	}

}
