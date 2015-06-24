package com.esferalia.aon.gwt.connect.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConnectServiceAsync {

	void importZippedMod2002013(String domainName, int domain,
			AsyncCallback<Void> callback);

}
