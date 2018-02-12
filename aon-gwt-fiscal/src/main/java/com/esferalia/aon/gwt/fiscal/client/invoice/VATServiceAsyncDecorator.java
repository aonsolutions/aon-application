package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class VATServiceAsyncDecorator implements VATServiceAsync {

	private VATServiceAsync fsa;

	public VATServiceAsyncDecorator(VATServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getAonConfiguration(String domainName, String user, int domain,
			AsyncCallback<AonConfiguration> callback) {
		AON.start();
		fsa.getAonConfiguration(domainName, user, domain, new AsyncCallbackWrapper<AonConfiguration>(callback));
	}

	@Override
	public void getVatContext(String domainName, String user, int domain, VatParams params,
			AsyncCallback<LinkedList<VatContext>> callback) {
		AON.start();
		fsa.getVatContext(domainName, user, domain, params, new AsyncCallbackWrapper<LinkedList<VatContext>>(callback));
	}
	
	@Override
	public void getVatContextReport(String domainName, String user, int domain, VatParams params,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getVatContextReport(domainName, user, domain, params, new AsyncCallbackWrapper<String>(callback));
		
	}
	@Override
	public void getVatSummaryContext(String domainName, String user, int domain, VatParams params,
			AsyncCallback<LinkedList<VatSummaryContext>> callback) {
		fsa.getVatSummaryContext(domainName, user, domain, params, new AsyncCallbackWrapper<LinkedList<VatSummaryContext>>(callback));
	}

}
