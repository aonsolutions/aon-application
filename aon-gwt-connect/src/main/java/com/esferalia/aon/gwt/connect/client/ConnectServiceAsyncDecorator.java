package com.esferalia.aon.gwt.connect.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ConnectServiceAsyncDecorator implements ConnectServiceAsync {

	private ConnectServiceAsync csa;

	public ConnectServiceAsyncDecorator(ConnectServiceAsync connectServiceAsync) {
		this.csa = connectServiceAsync;
	}

	@Override
	public void importZippedMod2002013(String domainName, int domain,
			AsyncCallback<Void> callback) {
		AON.start();
		csa.importZippedMod2002013(domainName, domain, 
				new AsyncCallbackWrapper<Void>(callback));
	}



}
