package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class EnterpriseDataTreeNode extends TreeNode<Enterprise> {

	private EnterpriseForm widget;

	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}

	@Override
	public void select(FiscalTree fiscalPanel) {
		if (widget == null) {
			widget = new EnterpriseForm();
		}
		widget.setEnterprise(getTreeObject());
		fiscalPanel.content.setWidget(widget);
	}

	@Override
	public EnterpriseDataTreeNode render(HasTreeItems parent, FiscalTree fiscalPanel,
			Enterprise enterprise) {
		InlineLabel label = new InlineLabel();
		label.setText(FiscalTree.MSG.enterpriseData());
		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconCompanyData());
		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode());
		setWidget(label);
		setUserObject(enterprise);
		parent.addItem(this);
		return this;
	}
}
