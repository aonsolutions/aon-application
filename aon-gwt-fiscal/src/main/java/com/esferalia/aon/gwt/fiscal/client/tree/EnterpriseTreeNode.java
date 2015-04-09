package com.esferalia.aon.gwt.fiscal.client.tree;

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
	public void select(FiscalTree fiscalTree) {
		EnterpriseMatrixPanel widget = new EnterpriseMatrixPanel();
		widget.setEnterprise(getTreeObject());
		fiscalTree.content.setWidget(widget);
	}

	@Override
	public EnterpriseTreeNode render(HasTreeItems parent,
			FiscalTree fiscalTree,Enterprise enterprise) {
		InlineLabel label = new InlineLabel();
		label.setText(enterprise.toString());
		label.addStyleName(AON.AON_CSS.aonIconCompany());
		label.addStyleName(AON.AON_CSS.aonTreeIconNode());
		setWidget(label);
		setUserObject(enterprise);
		parent.addItem(this);
    	fiscalTree.toolbar.addListener(this);
		return this;
	}

}
