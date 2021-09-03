package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CheckItServiceAsync {

	void getConfiguration(String currentDomainName, int currentDomain, String user, AsyncCallback<CheckItConfiguration> callback);
	

}
