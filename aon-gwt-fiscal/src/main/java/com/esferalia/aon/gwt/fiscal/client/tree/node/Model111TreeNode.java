package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.ITreeNodeCallback;
import com.esferalia.aon.gwt.fiscal.client.tree.content.Model111AEATForm;
import com.esferalia.aon.gwt.fiscal.client.tree.content.Model111Form;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class Model111TreeNode extends TreeNode<Mod111> {

	@Override
	public void select(final FiscalTree fiscalTree) {
		Mod111 mod111 = getTreeObject();
		Model111Form widget = null;
		if (mod111.getAdministration() == Administration.COMMON_TERRITORY) {
			widget =  new Model111AEATForm();
		} else {
			widget =  new Model111Form();
		}
		widget.select(this.getTreeObject());
		widget.setCallback(new ITreeNodeCallback<Mod111>() {
			
			@Override
			public void delete(Mod111 t) {
				 getParentItem().removeItem(Model111TreeNode.this);
			}

			@Override
			public void changeLabel(Mod111 mod111) {
				setLabel(mod111);
			}

		});
		fiscalTree.setContent(widget);
	}
	
	@Override
	public Mod111 getTreeObject() {
		return (Mod111) this.getUserObject();
	}

	@Override
	public Model111TreeNode render(HasTreeItems parent,final Mod111 mod111) {
    	this.setUserObject(mod111);
    	parent.addItem(this);
    	setLabel(mod111);
		return this;
	}

	public void setLabel(Mod111 mod111) {
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.fiscalPeriod(mod111.getPeriod())
				+ (mod111.isReplacement()? " - Sust.":"") 
				);
		label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
		label.addStyleName(TreeNode.getAdministrationIcon(mod111.getAdministration()));
		this.setWidget(label);	
	}
}
