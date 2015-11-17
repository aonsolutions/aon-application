package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_marketplace")
public interface IMarketplace extends RemoteService{

	List<EcommerceProduct> getProductTemplatesList(Integer domainId);
	
	List<Order> getAmazonOrdersList(Integer domainId, String login);
	
	public Vector<EcommerceProduct> searchNameTemplate(String searchStr, Vector<EcommerceProduct> templates);

	public Vector<EcommerceProduct> searchTypeTemplate(String searchStr, Vector<EcommerceProduct> templates);

	public String getDateStr(Date date);

	public void deleteTemplate(String description);
}
