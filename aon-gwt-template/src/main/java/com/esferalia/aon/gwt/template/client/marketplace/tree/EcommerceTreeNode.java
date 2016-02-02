package com.esferalia.aon.gwt.template.client.marketplace.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.template.client.marketplace.Marketplace;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class EcommerceTreeNode extends TreeNode<Ecommerce>{

	@Override
	public void select(Marketplace marketplace) {
	
	}

	@Override
	public Ecommerce getTreeObject() {
		return null;
	}

	@Override
	public TreeNode<Ecommerce> render(HasTreeItems parent, Ecommerce ecommerce) {
		InlineLabel label = new InlineLabel();
		if(ecommerce.equals(Ecommerce.AMAZON)){
			label.setText(ecommerce.getName()); 
	    	label.addStyleName("aon-icon-amazon");
		}
		else if(ecommerce.equals(Ecommerce.EBAY)){
			label.setText(ecommerce.getName());
			label.addStyleName("aon-icon-ebay");
		}
		else if(ecommerce.equals(Ecommerce.GENERIC)){
			label.setText(ecommerce.getName());
			label.addStyleName("aon-icon-marketplace-generic");
		}
		
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setTreeObject(ecommerce);
    	setWidget(label);
    	parent.addItem(this);
    	return this;
	}

}
