package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface IMarketplaceAsync {

	void getProductTemplatesList(Integer domainId,
			AsyncCallback<List<EcommerceProduct>> callback);

	void searchNameTemplate(String searchStr,
			Vector<EcommerceProduct> templates,
			AsyncCallback<Vector<EcommerceProduct>> callback);

	void searchTypeTemplate(String searchStr,
			Vector<EcommerceProduct> templates,
			AsyncCallback<Vector<EcommerceProduct>> callback);
	
}
