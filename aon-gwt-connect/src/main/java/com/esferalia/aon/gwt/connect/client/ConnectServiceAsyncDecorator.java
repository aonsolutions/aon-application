package com.esferalia.aon.gwt.connect.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ConnectServiceAsyncDecorator implements ConnectServiceAsync {

	private ConnectServiceAsync csa;

	public ConnectServiceAsyncDecorator(ConnectServiceAsync connectServiceAsync) {
		this.csa = connectServiceAsync;
	}

	@Override
	public void importZippedMod2002013(String domainName, int domain, String data,
			AsyncCallback<List<String>> callback) {
		AON.start();
		csa.importZippedMod2002013(domainName, domain, data,
				new AsyncCallbackWrapper<List<String>>(callback));

	}

}
