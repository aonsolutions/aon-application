package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.DefaultTreeNodeCallBack;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.content.EnterpriseForm;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class EnterpriseDataTreeNode extends TreeNode<Enterprise> {

	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		EnterpriseForm widget = new EnterpriseForm();
		widget.select(getTreeObject());
		widget.setCallback(new DefaultTreeNodeCallBack<Enterprise>());
		fiscalTree.setContent(widget);
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
