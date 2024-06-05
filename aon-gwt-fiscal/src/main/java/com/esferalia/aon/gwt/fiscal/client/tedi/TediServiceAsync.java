package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TediServiceAsync {

	void parseInvoice(String domainName, String user, int domain, String fileName, String content, AsyncCallback<TediResult> callback);
	void validateInvoice(String domainName, String user, int domain, TediResult result, AsyncCallback<TediResult> callback);
	
}
