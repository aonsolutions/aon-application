package com.esferalia.aon.gwt.template.client.marketplace.tree;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.template.client.JsTemplates;
import com.esferalia.aon.gwt.template.client.marketplace.AmazonOrders;
import com.esferalia.aon.gwt.template.client.marketplace.Marketplace;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class OrdersTreeNode extends TreeNode<Ecommerce>{

	Marketplace m;
	Ecommerce ecommerce;
	
	@Override
	public void select(Marketplace marketplace) {
		m= marketplace;
		//if(getEcommerce().equals(Ecommerce.AMAZON)){
			marketplace.getImpl().getAmazonOrdersList(getDomain(), m.getUser().getLogin(),new AsyncCallback<List<Order>>() {
			
				@Override
				public void onSuccess(List<Order> result) {
					AmazonOrders ao = new AmazonOrders(result, m.getUser().getLogin());
					m.setContent(ao);
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		//}
	}

	@Override
	public Ecommerce getTreeObject() {
		return null;
	}

	@Override
	public TreeNode<Ecommerce> render(HasTreeItems parent, Ecommerce ecommerce) {
		InlineLabel label = new InlineLabel();
    	label.setText("Pedidos"); 
    	label.addStyleName("aon-icon-task");
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setTreeObject(ecommerce);
    	setWidget(label);
    	parent.addItem(this);
    	return this;
	}

	public Ecommerce getEcommerce(){
		return ecommerce;
	}
	public void setEcommercer(Ecommerce ecommerce){
		this.ecommerce = ecommerce;
		
	}

	private Domain getDomain() {
		return new Domain().setId(JsTemplates.getCurrentDomain()).setName(JsTemplates.getCurrentDomainName());
	}
}
