package com.esferalia.aon.gwt.stat.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
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
	public void getYearInvoiceTypeData(String domainName,Integer domainId,StatParams params,
			AsyncCallback<StatData<Integer, String, Double>> callback) {
		AON.start();
		fsa.getYearInvoiceTypeData(domainName,domainId,params
			, new AsyncCallbackWrapper<StatData<Integer, String, Double>>(callback));
	}

	@Override
	public void getMonthInvoiceTypeData(String domainName,Integer domainId, StatParams params,
			AsyncCallback<StatData<Integer, String, Double>> callback) {
		AON.start();
		fsa.getMonthInvoiceTypeData(domainName,domainId,params
			, new AsyncCallbackWrapper<StatData<Integer, String, Double>>(callback));
	}
	
	@Override
	public void getDayInvoiceTypeData(String domainName,Integer domainId, StatParams params,
			AsyncCallback<StatData<Integer, String, Double>> callback) {
		AON.start();
		fsa.getDayInvoiceTypeData(domainName,domainId,params
			, new AsyncCallbackWrapper<StatData<Integer, String, Double>>(callback));
	}

}
