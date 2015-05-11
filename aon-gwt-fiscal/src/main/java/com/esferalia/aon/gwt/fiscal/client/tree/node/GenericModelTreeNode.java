package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.DefaultTreeNodeCallBack;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.content.GenericModelForm;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class GenericModelTreeNode extends TreeNode<FiscalModel> {

	@Override
	public void select(final FiscalTree fiscalTree) {
		GenericModelForm widget = new GenericModelForm();
		widget.select(this.getTreeObject());
		widget.setCallback(new DefaultTreeNodeCallBack<FiscalModel>());
		fiscalTree.setContent(widget);
	}
	
	@Override
	public FiscalModel getTreeObject() {
		return (FiscalModel) this.getUserObject();
	}

	@Override
	public GenericModelTreeNode render(HasTreeItems parent,final FiscalModel fm) {
    	this.setUserObject(fm);
    	parent.addItem(this);
    	setLabel(fm);
		return this;
	}

	public void setLabel(FiscalModel fm) {
		InlineLabel label = new InlineLabel();
		label.setText(
				AON.MSG.fiscalPeriod(fm.getPeriod())
				+ (fm.isReplacement()? " - Sust.":"") 
				);
		label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
		label.addStyleName(TreeNode.getAdministrationIcon(fm.getAdministration()));
		this.setWidget(label);	
	}
}
