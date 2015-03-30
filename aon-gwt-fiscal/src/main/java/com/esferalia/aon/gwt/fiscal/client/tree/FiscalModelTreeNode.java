package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class FiscalModelTreeNode extends TreeNode<FiscalModel> {

	private FiscalModelForm widget;

	@Override
	public void select(FiscalTree fiscalPanel) {
		if (widget ==null) {
			widget = new FiscalModelForm();
		}
		widget.select(this );
		fiscalPanel.content.setWidget(widget);
	}
	
	@Override
	public FiscalModel getTreeObject() {
		return (FiscalModel) this.getUserObject();
	}

	@Override
	public FiscalModelTreeNode render(HasTreeItems parent, FiscalTree fiscalPanel, FiscalModel fm) {
    	InlineLabel label = new InlineLabel();
    	label.setText(FiscalTree.MSG.fiscalModelType(fm.getModel()) 
    			+ " - " 
    			+ FiscalTree.MSG.fiscalPeriod(fm.getPeriod())
    			+ (fm.isReplacement()? " - Sust.":"") );
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode() );
   		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconModule() );
    	this.setWidget(label);
    	this.setUserObject(fm);
    	parent.addItem(this);
		return this;
	}
	
}
