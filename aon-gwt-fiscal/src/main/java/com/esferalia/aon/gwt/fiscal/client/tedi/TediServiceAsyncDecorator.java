package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TediServiceAsyncDecorator implements TediServiceAsync {

	private TediServiceAsync fsa;

	public TediServiceAsyncDecorator(TediServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}
	
	@Override
	public void parseInvoice(Occam occam, String fileName, String content, AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.parseInvoice(occam, fileName , content, new AsyncCallbackWrapper<TediResult>(callback));		
	}

	@Override
	public void validateInvoice(Occam occam, TediResult result, AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.validateInvoice(occam, result, new AsyncCallbackWrapper<TediResult>(callback));
	}
}
