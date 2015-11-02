package com.esferalia.aon.gwt.template.client.marketplace.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.template.client.marketplace.Marketplace;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class OrdersTreeNode extends TreeNode<Integer>{

	@Override
	public void select(Marketplace marketplace) {
	
	}

	@Override
	public Integer getTreeObject() {
		return null;
	}

	@Override
	public TreeNode<Integer> render(HasTreeItems parent, Integer t) {
		InlineLabel label = new InlineLabel();
    	label.setText("Pedidos"); 
    	label.addStyleName("aon-icon-registradores");
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setTreeObject(t);
    	setWidget(label);
    	parent.addItem(this);
    	return this;
	}

}
