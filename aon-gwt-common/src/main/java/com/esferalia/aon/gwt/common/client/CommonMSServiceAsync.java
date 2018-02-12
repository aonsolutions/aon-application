package com.esferalia.aon.gwt.common.client;


import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommonMSServiceAsync {

	// --------------------------------------------------------- CONFIGURATION
	void getAonConfiguration(String domainName, String user, int domain, AsyncCallback<AonConfiguration> asyncCallback);
}
