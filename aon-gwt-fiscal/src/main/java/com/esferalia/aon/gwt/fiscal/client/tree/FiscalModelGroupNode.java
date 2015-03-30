package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FiscalModelGroupNode extends TreeNode<Enterprise> {
	
	private VerticalPanel widget;

	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}

	@Override
	public void select(FiscalTree fiscalPanel) {
		if (widget ==null) {
			widget = new VerticalPanel( );
			widget.setStyleName(FiscalTree.AON_RESOURCES.css().aonFiscalTreeList());
			Label title = new Label(getText());
			title.setStyleName(FiscalTree.AON_RESOURCES.css().aonFiscalTreeTitle());
			widget.add(title);
		}
		fiscalPanel.content.setWidget(widget);
	}


	@Override
	public TreeNode<Enterprise> render(HasTreeItems parent,
			FiscalTree fiscalTree, Enterprise enterprise) {
    	InlineLabel label = new InlineLabel();
    	label.setText(FiscalTree.MSG.fiscalModels());
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconModel());
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(enterprise);
    	parent.addItem(this);
		return this;
	}

}
