package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.RegistryAttachTag;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface IMarketplaceAsync {

	void getProductTemplatesList(Domain domain, User user, AsyncCallback<List<EcommerceProduct>> callback);

	void getAmazonOrdersList(Domain domain, String login, AsyncCallback<List<Order>> callback);
	
	void searchNameTemplate(String searchStr, Vector<EcommerceProduct> templates, AsyncCallback<Vector<EcommerceProduct>> callback);

	void searchTypeTemplate(String searchStr, Vector<EcommerceProduct> templates, AsyncCallback<Vector<EcommerceProduct>> callback);

	void deleteTemplate(Domain domain, User user, String description, AsyncCallback<Void> callback);

	void addMarketplaceTag(Domain domain, User user, String name, AsyncCallback<Tag> callback);
	
	void removeMarketplaceTag(Domain domain, User user, Tag tag, AsyncCallback<Void> callback);

	void updateMarketplaceTag(Domain domain, User user, Tag tag, AsyncCallback<Tag> callback);
	
	void getMarketplaceTagList(Domain domain, User user, AsyncCallback<LinkedList<Tag>> callback);
	
	void getProductList(Domain domain, String login, Integer category, AsyncCallback<List<OldProduct>> callback);
	
	void getProductList(Domain domain, String login, Integer category, Boolean active, AsyncCallback<List<OldProduct>> callback);
	
	void getSalesProductList(Domain domain, String login, Integer category,
			Boolean active, Boolean sales, AsyncCallback<List<OldProduct>> callback);
	
	void getMarketItemList(Domain domain, String login, Integer category,
			Boolean active, Boolean sales, AsyncCallback<List<OldItem>> callback);
	
	void searchItemByProductName(String searchStr, Vector<OldItem> list, AsyncCallback<Vector<OldItem>> callback);
	
	void getAttachTemplateTagList(Domain domain, String login, List<Integer> pTagList, AsyncCallback<List<RegistryAttachTag>> callback);
	
	void obtainEcommerceProductTemplates(Domain domain, User user, String sellerId, AsyncCallback<List<Attach>> callback);
	
	void obtainEcommerceProductAttach(Domain domain, User user, OldItem item, String templateName, AsyncCallback<Attach> callback);

	void obtainEcommerceProductValues(Domain domain, User user, Attach attach, OldItem item, AsyncCallback<EcommerceProduct> callback);
	
	void obtainEcommerceProductValues(Domain domain, User user, OldItem item, String templateName, AsyncCallback<EcommerceProduct> callback);
	
	void acceptEcommerceProductValues(Domain domain, String login, OldItem item, String templateName, EcommerceProduct eProduct, Attach iattach, AsyncCallback<Boolean> callback);
	
}
