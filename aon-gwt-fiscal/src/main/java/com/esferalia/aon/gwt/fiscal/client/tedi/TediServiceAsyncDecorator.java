package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

import es.translogia.tedi.ewok.TediInvoice;

public class TediServiceAsyncDecorator implements TediServiceAsync {

	private TediServiceAsync fsa;

	public TediServiceAsyncDecorator(TediServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getAonConfiguration(String domainName, String user, int domain,
			AsyncCallback<AonConfiguration> callback) {
		AON.start();
		fsa.getAonConfiguration(domainName, user, domain, new AsyncCallbackWrapper<AonConfiguration>(callback));
	}
	
	@Override
	public void getVerifiedInvoices(String domainName, String user, int domain,
			AsyncCallback<LinkedList<TediResult>> callback) {
		AON.start();
		fsa.getVerifiedInvoices(domainName, user, domain, new AsyncCallbackWrapper<LinkedList<TediResult>>(callback));
	}

	@Override
	public void getInvoice(String domainName, String user, int domain, String uuid,
			AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.getInvoice(domainName, user, domain, uuid, new AsyncCallbackWrapper<TediResult>(callback));
	}

	@Override
	public void putInvoice(String domainName, String user, int domain, TediInvoice invoice,
			AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.putInvoice(domainName, user, domain, invoice, new AsyncCallbackWrapper<TediResult>(callback));
	}

	@Override
	public void putInvoices(String domainName, String user, int domain, LinkedList<TediResult> results,
			AsyncCallback<LinkedList<TediResult>> callback) {
		AON.start();
		fsa.putInvoices(domainName, user, domain, results, new AsyncCallbackWrapper<LinkedList<TediResult>>(callback));
	}
	
	@Override
	public void validateInvoice(String domainName, String user, int domain, TediResult result,
			AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.validateInvoice(domainName, user, domain, result, new AsyncCallbackWrapper<TediResult>(callback));
	}
	
	@Override
	public void getInvoiceAttachURL(String domainName, String user, int domain, String uuid, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInvoiceAttachURL(domainName, user, domain, uuid, new AsyncCallbackWrapper<String>(callback));
	}
}
