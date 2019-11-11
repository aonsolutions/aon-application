package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.tedi.TediCompanyResult;
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
	public void getVerifiedInvoices(String domainName, String user, int domain, boolean snapshot, Company company,
			AsyncCallback<LinkedList<TediResult>> callback) {
		AON.start();
		fsa.getVerifiedInvoices(domainName, user, domain, snapshot, company, new AsyncCallbackWrapper<LinkedList<TediResult>>(callback));
	}

	@Override
	public void getInvoice(String domainName, String user, int domain, boolean snapshot, String uuid
			, String tediStatus, AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.getInvoice(domainName, user, domain, snapshot,uuid,tediStatus, new AsyncCallbackWrapper<TediResult>(callback));
	}

	@Override
	public void rejectInvoice(String domainName, String user, int domain, boolean snapshot, TediInvoice invoice,
			AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.rejectInvoice(domainName, user, domain, snapshot,invoice, new AsyncCallbackWrapper<TediResult>(callback));
	}

	@Override
	public void putInvoice(String domainName, String user, int domain, boolean snapshot, TediInvoice invoice,
			AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.putInvoice(domainName, user, domain, snapshot,invoice, new AsyncCallbackWrapper<TediResult>(callback));
	}

	@Override
	public void putInvoices(String domainName, String user, int domain, boolean snapshot, LinkedList<TediResult> results,
			AsyncCallback<LinkedList<TediResult>> callback) {
		AON.start();
		fsa.putInvoices(domainName, user, domain, snapshot,results, new AsyncCallbackWrapper<LinkedList<TediResult>>(callback));
	}
	
	@Override
	public void validateInvoice(String domainName, String user, int domain, boolean snapshot, TediResult result,
			AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.validateInvoice(domainName, user, domain, snapshot,result, new AsyncCallbackWrapper<TediResult>(callback));
	}
	
	@Override
	public void getInvoiceAttachURL(String domainName, String user, int domain, boolean snapshot, String uuid, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInvoiceAttachURL(domainName, user, domain,snapshot, uuid, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getCompanies(String domainName, String user, int domain, boolean snapshot,
			AsyncCallback<LinkedList<TediCompanyResult>> callback) {
		AON.start();
		fsa.getCompanies(domainName, user, domain,snapshot, new AsyncCallbackWrapper<LinkedList<TediCompanyResult>>(callback));
	}

	@Override
	public void tediSync(String domainName, String user, int domain, boolean snapshot,
			AsyncCallback<LinkedList<TediCompanyResult>> callback) {
		AON.start();
		fsa.tediSync(domainName, user, domain,snapshot, new AsyncCallbackWrapper<LinkedList<TediCompanyResult>>(callback));
	}

	@Override
	public void getCountInboxInvoices(String domainName, String user, int domain, boolean snapshot, Company company,
			AsyncCallback<Integer> callback) {
		AON.start();
		fsa.getCountInboxInvoices(domainName, user, domain,snapshot, company, new AsyncCallbackWrapper<Integer>(callback));		
	}
}
