package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StatServiceAsyncDecorator implements StatServiceAsync {

	private StatServiceAsync fsa;

	public StatServiceAsyncDecorator(StatServiceAsync statsServiceAsync) {
		this.fsa = statsServiceAsync;
	}

	@Override
	public void createStatParams(String domainName, int domain, AsyncCallback<StatParams> callback) {
		AON.start();
		fsa.createStatParams(domainName, domain
			, new AsyncCallbackWrapper<StatParams>(callback));
	}

	@Override
	public void getYearInvoiceTypeData(StatParams params,
			AsyncCallback<StatData<Integer, InvoiceType, Double>> callback) {
		AON.start();
		fsa.getYearInvoiceTypeData(params
			, new AsyncCallbackWrapper<StatData<Integer, InvoiceType, Double>>(callback));
	}

	@Override
	public void getMonthInvoiceTypeData(StatParams params,
			AsyncCallback<StatData<Integer, InvoiceType, Double>> callback) {
		AON.start();
		fsa.getMonthInvoiceTypeData(params
			, new AsyncCallbackWrapper<StatData<Integer, InvoiceType, Double>>(callback));
	}

}
