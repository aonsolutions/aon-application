package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.ITreeNodeCallback;
import com.esferalia.aon.gwt.fiscal.client.tree.content.Model131Form;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class Model131TreeNode extends TreeNode<Mod131> {

	@Override
	public void select(final FiscalTree fiscalTree) {
		Model131Form widget = new Model131Form();
		widget.select(this.getTreeObject());
		widget.setCallback(new ITreeNodeCallback<Mod131>() {
			
			@Override
			public void delete(Mod131 t) {
				 getParentItem().removeItem(Model131TreeNode.this);
			}

			@Override
			public void changeLabel(Mod131 mod131) {
				setLabel(mod131);
			}

		});
		fiscalTree.setContent(widget);
	}
	
	@Override
	public Mod131 getTreeObject() {
		return (Mod131) this.getUserObject();
	}

	@Override
	public Model131TreeNode render(HasTreeItems parent,final Mod131 mod131) {
    	this.setUserObject(mod131);
    	parent.addItem(this);
    	setLabel(mod131);
		return this;
	}

	public void setLabel(Mod131 mod131) {
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.fiscalPeriod(mod131.getPeriod())
				+ (mod131.isReplacement()? " - Sust.":"") 
				);
		label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
		label.addStyleName(TreeNode.getAdministrationIcon(mod131.getAdministration()));
		this.setWidget(label);	
	}
}
