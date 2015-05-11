package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class ModelTreeNode extends TreeNode<FiscalModelType> {

	@Override
	public FiscalModelType getTreeObject() {
		return (FiscalModelType) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		fiscalTree.renderGenericContent(this);
	}

	@Override
	public ModelTreeNode render(HasTreeItems parent,final FiscalModelType model) {
		InlineLabel label = new InlineLabel();
		label.setText("Modelo " + model.getValue());
		label.addStyleName(AON.AON_CSS.aonTreeYear());
		setWidget(label);
		setUserObject(model);
		parent.addItem(this);
		return this;
	}

}
