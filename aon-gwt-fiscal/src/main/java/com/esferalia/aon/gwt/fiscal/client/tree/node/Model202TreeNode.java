package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.ITreeNodeCallback;
import com.esferalia.aon.gwt.fiscal.client.tree.content.Model202Form;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class Model202TreeNode extends TreeNode<Mod202>  {

	@Override
	public void select(final FiscalTree fiscalTree) {
		Model202Form widget = new Model202Form();
		widget.select( this.getTreeObject() );
		widget.setCallback(new ITreeNodeCallback<Mod202>() {

			@Override
			public void delete(Mod202 mod202) {
				 getParentItem().removeItem(Model202TreeNode.this);
			}
			@Override
			public void changeLabel(Mod202 mod202) {
				setLabel(mod202);
			}
		});
		fiscalTree.setContent(widget);
	}
	
	@Override
	public Mod202 getTreeObject() {
		return (Mod202) this.getUserObject();
	}

	@Override
	public Model202TreeNode render(HasTreeItems parent,final  Mod202 mod202) {
    	this.setUserObject(mod202);
    	parent.addItem(this);
    	setLabel(mod202);
		return this;
	}

	public void setLabel(Mod202 mod202) {
		InlineLabel label = new InlineLabel();
		label.setText(mod202.getPeriod().getDescription()
				+ (mod202.isReplacement()? " - Sust.":"") 
				);
		label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
		label.addStyleName(TreeNode.getAdministrationIcon(mod202.getAdministration()));
		this.setWidget(label);	
	}

}
