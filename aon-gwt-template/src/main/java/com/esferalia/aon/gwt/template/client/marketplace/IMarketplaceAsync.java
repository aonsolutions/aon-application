package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface IMarketplaceAsync {

	void getProductTemplatesList(Domain domain,
			AsyncCallback<List<EcommerceProduct>> callback);

	void searchNameTemplate(String searchStr,
			Vector<EcommerceProduct> templates,
			AsyncCallback<Vector<EcommerceProduct>> callback);

	void searchTypeTemplate(String searchStr,
			Vector<EcommerceProduct> templates,
			AsyncCallback<Vector<EcommerceProduct>> callback);

	void getAmazonOrdersList(Domain domain, String login, AsyncCallback<List<Order>> callback);

	void getDateStr(Date date, AsyncCallback<String> callback);

	void deleteTemplate(Domain domain, String description, AsyncCallback<Void> callback);

	void getMarketplaceTagList(Domain domain, AsyncCallback<LinkedList<Tag>> callback);
	
}
