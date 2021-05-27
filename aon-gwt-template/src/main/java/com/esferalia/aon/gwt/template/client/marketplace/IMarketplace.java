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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_marketplace")
public interface IMarketplace extends RemoteService{

	List<EcommerceProduct> getProductTemplatesList(Domain domain, User user);
	
	List<Order> getAmazonOrdersList(Domain domain, String login);
	
	public Vector<EcommerceProduct> searchNameTemplate(String searchStr, Vector<EcommerceProduct> templates);

	public Vector<EcommerceProduct> searchTypeTemplate(String searchStr, Vector<EcommerceProduct> templates);

	public void deleteTemplate(Domain domain, User user, String description);
	
	public Tag addMarketplaceTag(Domain domain, User user, String name);
	
	public void removeMarketplaceTag(Domain domain, User user, Tag tag);
	
	public Tag updateMarketplaceTag(Domain domain, User user, Tag tag);
	
	public LinkedList<Tag> getMarketplaceTagList(Domain domain, User user);
	
	List<OldProduct> getProductList(Domain domain, String login, Integer category);

	List<OldProduct> getProductList(Domain domain, String login, Integer category, Boolean active);

	List<OldProduct> getSalesProductList(Domain domain, String login, Integer category, Boolean active, Boolean sales);
	
	public List<OldItem> getMarketItemList(Domain domain, String login, Integer category, Boolean active, Boolean sales);
	
	public Vector<OldItem> searchItemByProductName(String searchStr, Vector<OldItem> list);
	
	List<RegistryAttachTag> getAttachTemplateTagList(Domain domain, String login, List<Integer> pTagList);
	
	public List<Attach> obtainEcommerceProductTemplates(Domain domain, User user, String sellerId);
	
	public Attach obtainEcommerceProductAttach(Domain domain, User user, OldItem item, String templateName);

	public EcommerceProduct obtainEcommerceProductValues(Domain domain, User user, Attach attach, OldItem item);
	
	public EcommerceProduct obtainEcommerceProductValues(Domain domain, User user, OldItem item, String templateName);
	
	public Boolean acceptEcommerceProductValues(Domain domain, String login, OldItem item, String templateName, EcommerceProduct eProduct, Attach iattach);
}
