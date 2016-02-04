package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.Product;
import com.esferalia.aon.gwt.template.shared.RegistryAttachTag;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
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
	
	void getProductList(Domain domain, String login, Integer category, AsyncCallback<List<Product>> callback);
	
	void getProductList(Domain domain, String login, Integer category, Boolean active, AsyncCallback<List<Product>> callback);
	
	void searchProductByName(String searchStr, Vector<Product> list, AsyncCallback<Vector<Product>> callback);
	
	void getAttachTemplateTagList(Domain domain, String login, List<Integer> pTagList, AsyncCallback<List<RegistryAttachTag>> callback);
	
	void obtainEcommerceProductTemplates(Domain domain, String sellerId, AsyncCallback<List<Attach>> callback);
	
	void obtainEcommerceProductAttach(Domain domain, Product product, String templateName, AsyncCallback<Attach> callback);

	void obtainEcommerceProductValues(Domain domain, Attach attach, Product product, AsyncCallback<EcommerceProduct> callback);
	
	void obtainEcommerceProductValues(Domain domain, Product product, String templateName, AsyncCallback<EcommerceProduct> callback);
	
	void acceptEcommerceProductValues(Domain domain, String login, String templateName, EcommerceProduct eProduct, Attach iattach, AsyncCallback<Boolean> callback);

	void addMarketplaceTag(Domain domain, String name, AsyncCallback<Tag> callback);

	void removeMarketplaceTag(Domain domain, Tag tag, AsyncCallback<Void> callback);

	void updateMarketplaceTag(Domain domain, Tag tag, AsyncCallback<Tag> callback);
	
}
