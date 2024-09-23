package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TediServiceAsync {

	void parseInvoice(Occam occam, String fileName, String content, AsyncCallback<TediResult> callback);
	void validateInvoice(Occam occam, TediResult result, AsyncCallback<TediResult> callback);
	
}
