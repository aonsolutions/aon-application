package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AonSuggestionServiceAsyncDecorator implements AonSuggestionServiceAsync {

	private AonSuggestionServiceAsync serviceAsync;

	public AonSuggestionServiceAsyncDecorator(AonSuggestionServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// [INVOICING GROUP]
	@Override
	public void getInvoicingGroups(Occam occam, Integer domainId, String query, AsyncCallback<LinkedList<InvoicingGroup>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getInvoicingGroups(occam, domainId, query, new AsyncCallbackWrapper<>(callback));
	}
			
	// [CUSTOMER]
	@Override
	public void getCustomers(Occam occam, Integer domainId, String query, AsyncCallback<LinkedList<Customer>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomers(occam, domainId, query, new AsyncCallbackWrapper<>(callback));
	}
	
	// [ITEM]
	
	@Override
	public void getItems(Occam occam, Integer domainId, String query, AsyncCallback<LinkedList<Item>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getItems(occam, domainId, query, new AsyncCallbackWrapper<>(callback));
	}
	
	// [SERIES]
	@Override
	public void getSeries(Occam occam, Integer domain, String query, AsyncCallback<LinkedList<Series>> callback) {
		AON.start();
		serviceAsync.getSeries(occam, domain, query, new AsyncCallbackWrapper<>(callback));
	}
	
}
