package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.RegistryAttachTag;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_marketplace")
public interface IMarketplace extends RemoteService{

	List<EcommerceProduct> getProductTemplatesList(Domain domain);
	
	List<Order> getAmazonOrdersList(Domain domain, String login);
	
	public Vector<EcommerceProduct> searchNameTemplate(String searchStr, Vector<EcommerceProduct> templates);

	public Vector<EcommerceProduct> searchTypeTemplate(String searchStr, Vector<EcommerceProduct> templates);

	public String getDateStr(Date date);

	public void deleteTemplate(Domain domain, String description);
	
	public Tag addMarketplaceTag(Domain domain, String name);
	
	public void removeMarketplaceTag(Domain domain, Tag tag);
	
	public Tag updateMarketplaceTag(Domain domain, Tag tag);
	
	public LinkedList<Tag> getMarketplaceTagList(Domain domain);
	
	List<Product> getProductList(Domain domain, String login, Integer category);

	List<Product> getProductList(Domain domain, String login, Integer category, Boolean active);

	List<Product> getSalesProductList(Domain domain, String login, Integer category, Boolean active, Boolean sales);
	
	public List<Item> getMarketItemList(Domain domain, String login, Integer category, Boolean active, Boolean sales);
	
	public Vector<Item> searchItemByProductName(String searchStr, Vector<Item> list);
	
	List<RegistryAttachTag> getAttachTemplateTagList(Domain domain, String login, List<Integer> pTagList);
	
	public List<Attach> obtainEcommerceProductTemplates(Domain domain, String sellerId);
	
	public Attach obtainEcommerceProductAttach(Domain domain, Item item, String templateName);

	public EcommerceProduct obtainEcommerceProductValues(Domain domain, Attach attach, Item item);
	
	public EcommerceProduct obtainEcommerceProductValues(Domain domain, Item item, String templateName);
	
	public Boolean acceptEcommerceProductValues(Domain domain, String login, Item item, String templateName, EcommerceProduct eProduct, Attach iattach);
}
