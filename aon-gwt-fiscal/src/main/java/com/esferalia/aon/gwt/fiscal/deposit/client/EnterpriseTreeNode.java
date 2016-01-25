package com.esferalia.aon.gwt.fiscal.deposit.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class EnterpriseTreeNode extends TreeNode<Enterprise> {

	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}
	
	@Override
	public void select(final Deposit deposit) {

	}

	@Override
	public EnterpriseTreeNode render(HasTreeItems parent,Enterprise enterprise) {
		InlineLabel label = new InlineLabel();
		label.setText(enterprise.toString());
		label.addStyleName(AON.AON_CSS.aonIconCompany());
		label.addStyleName(AON.AON_CSS.aonTreeIconNode());
		setWidget(label);
		setUserObject(enterprise);
		parent.addItem(this);
		return this;
	}

}
