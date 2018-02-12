package com.esferalia.aon.gwt.common.client;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class CommonMSServiceAsyncDecorator implements CommonMSServiceAsync {

	private CommonMSServiceAsync serviceAsync;

	public CommonMSServiceAsyncDecorator(CommonMSServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// --------------------------------------------------------- CONFIGURATION
	@Override
	public void getAonConfiguration(String domainName, String user, int domain, AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(domainName, user, domain, new AsyncCallbackWrapper<AonConfiguration>(callback));
	}
}
