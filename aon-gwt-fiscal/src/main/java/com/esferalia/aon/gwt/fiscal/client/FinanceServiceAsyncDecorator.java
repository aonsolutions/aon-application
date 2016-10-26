package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FinanceServiceAsyncDecorator implements FinanceServiceAsync {

	private FinanceServiceAsync fsa;

	public FinanceServiceAsyncDecorator(FinanceServiceAsync financeServiceAsync) {
		this.fsa = financeServiceAsync;
	}

	// --------------------------------------------------------------- INVOICE
	// SERIES
	@Override
	public void getInvoiceSeries(String domainName, int domainId, Date from, Date to, boolean taxDate,
			AsyncCallback<LinkedList<InvoiceSeries>> callback) {
		AON.start();
		fsa.getInvoiceSeries(domainName,domainId, from, to, taxDate, new AsyncCallbackWrapper<LinkedList<InvoiceSeries>>(callback));
	}

	@Override
	public void getInvoiceNextNumber(String domainName, Integer domainId, Byte[] types, String series,
			AsyncCallback<Integer> callback) {
		AON.start();
		fsa.getInvoiceNextNumber(domainName,domainId, types, series, new AsyncCallbackWrapper<Integer>(callback));
	}

}
