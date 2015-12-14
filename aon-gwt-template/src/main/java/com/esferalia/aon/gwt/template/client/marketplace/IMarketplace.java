package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
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
	
	public LinkedList<Tag> getMarketplaceTagList(Domain domain);
}
