package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FinanceServiceAsync {

	// --------------------------------------------------------------- INVOICE SERIES
	void getInvoiceSeries(String domainName, int domainId, Date from, Date to, boolean taxDate,AsyncCallback<LinkedList<InvoiceSeries>> callback );

	void getInvoiceNextNumber(String domainName, Integer domainId, Byte[] types, String series,
			AsyncCallback<Integer> callback);
	

}
