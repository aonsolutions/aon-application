package com.esferalia.aon.gwt.stat.client;

import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StatServiceAsync {
	
	void createStatParams(String domainName, int domain,
			AsyncCallback<StatParams> callback);

	void getStatData(String domainName, Integer domainId, StatParams params,
			AsyncCallback<StatData<String, String, Double>> callback);

	void getInvoicesReport(String domainName, int domain, StatParams params, AsyncCallback<String> callback);

}
