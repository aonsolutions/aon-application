package com.esferalia.aon.gwt.fiscal.deposit.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class EnterpriseDataTreeNode extends TreeNode<Enterprise> {

	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}

	@Override
	public void select(final Deposit deposit) {

	}

	@Override
	public EnterpriseDataTreeNode render(HasTreeItems parent
			, Enterprise enterprise) {
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.enterpriseData());
		label.addStyleName(AON.AON_CSS.aonIconCompanyData());
		label.addStyleName(AON.AON_CSS.aonTreeIconNode());
		setWidget(label);
		setUserObject(enterprise);
		parent.addItem(this);
		return this;
	}
}
