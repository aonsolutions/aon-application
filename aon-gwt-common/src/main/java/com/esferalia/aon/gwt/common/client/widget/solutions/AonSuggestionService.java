package com.esferalia.aon.gwt.common.client.widget.solutions;


import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AonSuggestions")
public interface AonSuggestionService extends RemoteService {
	
	// [INVOICING GROUP]
	LinkedList<InvoicingGroup> getInvoicingGroups(Occam occam, Integer domainId, String query) throws AonCoreException;
	
	// [CUSTOMER]
	LinkedList<Customer> getCustomers(Occam occam, Integer domainId, String query) throws AonCoreException;
	
	// [ITEM]
	LinkedList<Item> getItems(Occam occam, Integer domainId, String query) throws AonCoreException;

}
