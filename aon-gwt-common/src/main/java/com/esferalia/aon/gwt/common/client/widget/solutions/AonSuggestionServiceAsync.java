package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AonSuggestionServiceAsync {
	
	// [CERTIFICATE]
	void getCertificates(Occam occam, AsyncCallback<LinkedList<Certificate>> callback) throws AonCoreException;
	
	// [INVOICE REGISTRY]
	void getInvoiceRegistries(Occam occam, Integer domainId, String query,AsyncCallback<LinkedList<InvoiceRegistry>> callback) throws AonCoreException;
	
	// [INVOICING GROUP]
	void getInvoicingGroups(Occam occam, Integer domainId, String query, AsyncCallback<LinkedList<InvoicingGroup>> callback) throws AonCoreException;
	
	// [CUSTOMER]
	void getCustomers(Occam occam, Integer domainId, String query, AsyncCallback<LinkedList<Customer>> callback) throws AonCoreException;

	// [ITEMS]
	void getItems(Occam occam, Integer domainId, String query, AsyncCallback<LinkedList<Item>> callback) throws AonCoreException;

	// [SERIES]
	void getSeries(Occam occam, Integer domain, String query, AsyncCallback<LinkedList<Series>> callback);


}
