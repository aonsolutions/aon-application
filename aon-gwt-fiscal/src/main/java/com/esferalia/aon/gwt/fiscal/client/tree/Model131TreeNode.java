package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class Model131TreeNode extends TreeNode<Mod131> {

	private Model131Form widget;

	@Override
	public void select(FiscalTree fiscalPanel) {
		if (widget ==null) {
			widget = new Model131Form();
		}
		widget.select(this );
		fiscalPanel.content.setWidget(widget);
	}
	
	@Override
	public Mod131 getTreeObject() {
		return (Mod131) this.getUserObject();
	}

	@Override
	public Model131TreeNode render(HasTreeItems parent, FiscalTree fiscalPanel, Mod131 mod131) {
    	InlineLabel label = new InlineLabel();
    	label.setText(FiscalTree.MSG.fiscalModelType( mod131.getModel()) 
    			+ " - " 
    			+ FiscalTree.MSG.fiscalPeriod(mod131.getPeriod())
    			+ (mod131.isReplacement()? " - Sust.":"") );
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode() );
   		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconModule() );
    	this.setWidget(label);
    	this.setUserObject(mod131);
    	parent.addItem(this);
		return this;
	}
	
}
