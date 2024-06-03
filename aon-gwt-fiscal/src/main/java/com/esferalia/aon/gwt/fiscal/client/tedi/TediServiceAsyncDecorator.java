package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TediServiceAsyncDecorator implements TediServiceAsync {

	private TediServiceAsync fsa;

	public TediServiceAsyncDecorator(TediServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}
	
	@Override
	public void parseInvoice(String domainName, String user, int domain, String fileName, String content,
			AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.parseInvoice(domainName, user, domain, fileName , content, new AsyncCallbackWrapper<TediResult>(callback));		
	}

	@Override
	public void validateInvoice(String domainName, String user, int domain, TediResult result,
			AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.validateInvoice(domainName, user, domain, result, new AsyncCallbackWrapper<TediResult>(callback));
	}
}
