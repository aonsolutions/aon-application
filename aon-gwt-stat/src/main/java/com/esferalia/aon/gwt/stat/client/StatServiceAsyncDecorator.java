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
	public void createStatParams(String domainName, String user, int domain, AsyncCallback<StatParams> callback) {
		AON.start();
		fsa.createStatParams(domainName, user, domain
			, new AsyncCallbackWrapper<StatParams>(callback));
	}

	@Override
	public void getStatData(String domainName, String user,Integer domainId,StatParams params,
			AsyncCallback<StatData<String, String, Double>> callback) {
		AON.start();
		fsa.getStatData(domainName,user,domainId,params
			, new AsyncCallbackWrapper<StatData<String, String, Double>>(callback));
	}

	@Override
	public void getInvoicesReport(String domainName, String user, int domain, StatParams params, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInvoicesReport(domainName,user,domain,params
			, new AsyncCallbackWrapper<String>(callback));
		
	}

}
