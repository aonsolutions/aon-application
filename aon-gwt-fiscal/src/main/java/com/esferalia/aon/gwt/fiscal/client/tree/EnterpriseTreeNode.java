package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class EnterpriseTreeNode extends TreeNode<Enterprise> {

	private EnterpriseMatrixPanel widget;

	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}
	
	@Override
	public void select(FiscalTree fiscalPanel) {
		if (widget == null) {
			widget = new EnterpriseMatrixPanel();
		}
		widget.setEnterprise(getTreeObject());
		fiscalPanel.content.setWidget(widget);
	}

	@Override
	public EnterpriseTreeNode render(HasTreeItems parent, FiscalTree fiscalPanel,
			Enterprise enterprise) {
		InlineLabel label = new InlineLabel();
		label.setText(enterprise.toString());
		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconCompany());
		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode());
		setWidget(label);
		setUserObject(enterprise);
		parent.addItem(this);
		return this;
	}

}
