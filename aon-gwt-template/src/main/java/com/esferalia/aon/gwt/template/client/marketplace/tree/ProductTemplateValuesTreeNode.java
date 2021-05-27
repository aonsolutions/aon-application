package com.esferalia.aon.gwt.template.client.marketplace.tree;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.template.client.JsTemplates;
import com.esferalia.aon.gwt.template.client.marketplace.Marketplace;
import com.esferalia.aon.gwt.template.client.marketplace.ProductList;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class ProductTemplateValuesTreeNode extends TreeNode<Integer>{

	private Marketplace marketplace;
	
	@Override
	public void select(Marketplace _marketplace) {
		marketplace = _marketplace;
		
		marketplace.getImpl().getMarketItemList(getDomain(),
				this.marketplace.getUser().getLogin(), null, true, true, new AsyncCallback<List<OldItem>>() {
					@Override
					public void onSuccess(List<OldItem> result) {
						ProductList list = new ProductList(marketplace.getAonData(), result);
						marketplace.setContent(list);
					}

					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
	}

	@Override
	public Integer getTreeObject() {
		return null;
	}

	@Override
	public TreeNode<Integer> render(HasTreeItems parent, Integer t) {
		InlineLabel label = new InlineLabel();
    	label.setText("Caracter\u00EDsticas de producto"); 
    	label.addStyleName("aon-icon-option");
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setTreeObject(t);
    	setWidget(label);
    	parent.addItem(this);
    	return this;
	}
	
	private Domain getDomain() {
		return new Domain().setId(JsTemplates.getCurrentDomain()).setName(JsTemplates.getCurrentDomainName());
	}

}
