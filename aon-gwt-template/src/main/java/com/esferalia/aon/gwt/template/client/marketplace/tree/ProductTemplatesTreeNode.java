package com.esferalia.aon.gwt.template.client.marketplace.tree;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.template.client.JsTemplates;
import com.esferalia.aon.gwt.template.client.marketplace.Marketplace;
import com.esferalia.aon.gwt.template.client.marketplace.ProductTemplates;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class ProductTemplatesTreeNode extends TreeNode<Integer>{
	
	Marketplace m;
	
	@Override
	public void select(Marketplace marketplace) {
		m= marketplace;
		marketplace.getImpl().getProductTemplatesList(getDomain(), marketplace.getUser() , new AsyncCallback<List<EcommerceProduct>>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(List<EcommerceProduct> result) {
				ProductTemplates pt = new ProductTemplates(m.getAonData(), result);
				m.setContent(pt);
			}
		});
	}

	@Override
	public Integer getTreeObject() {
		return getTreeObject();
	}

	@Override
	public TreeNode<Integer> render(HasTreeItems parent, Integer domainId) {
		InlineLabel label = new InlineLabel();
    	label.setText("Plantillas de Productos"); 
    	label.addStyleName("aon-icon-excel");
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setTreeObject(domainId);
    	setWidget(label);
    	parent.addItem(this);
    	return this;
	}

	private Domain getDomain() {
		return new Domain().setId(JsTemplates.getCurrentDomain()).setName(JsTemplates.getCurrentDomainName());
	}
}
