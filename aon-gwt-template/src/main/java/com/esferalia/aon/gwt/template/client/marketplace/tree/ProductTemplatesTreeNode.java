package com.esferalia.aon.gwt.template.client.marketplace.tree;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.template.client.marketplace.Marketplace;
import com.esferalia.aon.gwt.template.client.marketplace.ProductTemplates;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class ProductTemplatesTreeNode extends TreeNode<Integer>{
	
	Marketplace m;
	Integer domainId;
	
	@Override
	public void select(Marketplace marketplace) {
		m= marketplace;
		marketplace.getImpl().getProductTemplatesList(getDomainId(), new AsyncCallback<List<EcommerceProduct>>() {
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(List<EcommerceProduct> result) {
				ProductTemplates pt = new ProductTemplates(result, getDomainId());
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
    	label.addStyleName("aon-icon-registradores");
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setTreeObject(domainId);
    	setDomainId(domainId);
    	setWidget(label);
    	parent.addItem(this);
    	return this;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	
	

}
