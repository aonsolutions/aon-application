package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CheckItServiceAsyncDecorator implements CheckItServiceAsync {

	private CheckItServiceAsync fsa;

	public CheckItServiceAsyncDecorator(CheckItServiceAsync rawdocServiceAsync) {
		this.fsa = rawdocServiceAsync;
	}
	
	@Override
	public void getConfiguration(String domainName, int domain, String user, AsyncCallback<CheckItConfiguration> callback) {
		AON.start();
		fsa.getConfiguration(domainName, domain, user, new AsyncCallbackWrapper<CheckItConfiguration>(callback));
	}
	
}
