package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AonSuggestionServiceAsync {
	
	// [INVOICING GROUP]
	void getInvoicingGroups(Occam occam, Integer domainId, String query, AsyncCallback<LinkedList<InvoicingGroup>> callback) throws AonCoreException;
	
	// [CUSTOMER]
	void getCustomers(Occam occam, Integer domainId, String query, AsyncCallback<LinkedList<Customer>> callback) throws AonCoreException;

	// [ITEMS]
	void getItems(Occam occam, Integer domainId, String query, AsyncCallback<LinkedList<Item>> callback) throws AonCoreException;
	
}
