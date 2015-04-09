package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class Model131TreeNode extends TreeNode<Mod131> {

	private Model131Form widget;

	@Override
	public void select(FiscalTree fiscalTree) {
		if (widget ==null) {
			widget = new Model131Form();
		}
		widget.select(this );
		fiscalTree.toolbar.setVisibleCopyButton(false);
		fiscalTree.toolbar.setVisibleDraftButton(false);
		fiscalTree.toolbar.setVisiblePasteButton(false);
		fiscalTree.content.setWidget(widget);
	}
	
	@Override
	public Mod131 getTreeObject() {
		return (Mod131) this.getUserObject();
	}

	@Override
	public Model131TreeNode render(HasTreeItems parent, FiscalTree fiscalTree,Mod131 mod131) {
    	InlineLabel label = new InlineLabel();
    	label.setText(AON.MSG.fiscalModelType( mod131.getModel()) 
    			+ " - " 
    			+ AON.MSG.fiscalPeriod(mod131.getPeriod())
    			+ (mod131.isReplacement()? " - Sust.":"") );
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
   		label.addStyleName(AON.AON_CSS.aonIconModule() );
    	this.setWidget(label);
    	this.setUserObject(mod131);
    	parent.addItem(this);
    	fiscalTree.toolbar.addListener(this);
		return this;
	}
	
}
