package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.NewMod200Command;
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
		widget.setCallback(new TreeNodeCallback<Enterprise>() {

			@Override
			public void delete(Enterprise t) {
			}

			@Override
			public void changeLabel(Enterprise t) {
			}
			@Override
			public void newMod202() {
				NewMod200Command newMod200Command =  fiscalTree.getNewMod200Command();
				newMod200Command.execute();
			}
		});
		fiscalTree.content.setWidget(widget);
	}

	@Override
	public EnterpriseDataTreeNode render(HasTreeItems parent
			, FiscalTree fiscalTree
			, Enterprise enterprise) {
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.enterpriseData());
		label.addStyleName(AON.AON_CSS.aonIconCompanyData());
		label.addStyleName(AON.AON_CSS.aonTreeIconNode());
		setWidget(label);
		setUserObject(enterprise);
		parent.addItem(this);
    	fiscalTree.toolbar.addListener(this);
		return this;
	}
}
