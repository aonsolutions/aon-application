package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.DefaultTreeNodeCallBack;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.content.EnterpriseMatrixPanel;
import com.esferalia.aon.gwt.fiscal.client.tree.content.EnterpriseYear;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class EnterpriseTreeNode extends TreeNode<Enterprise> {

	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}
	
	@Override
	public void select(final FiscalTree fiscalTree) {
		EnterpriseMatrixPanel widget = new EnterpriseMatrixPanel();
		EnterpriseYear enterpriseYear = new EnterpriseYear();
		enterpriseYear.setEnterprise(getTreeObject());
		widget.select(enterpriseYear);
		widget.setCallback(new DefaultTreeNodeCallBack<EnterpriseYear>());
		fiscalTree.setContent(widget);
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
