package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RawdocServiceAsyncDecorator implements RawdocServiceAsync {

	private RawdocServiceAsync fsa;

	public RawdocServiceAsyncDecorator(RawdocServiceAsync rawdocServiceAsync) {
		this.fsa = rawdocServiceAsync;
	}
	
	// --------------------------------------------------------------- FINANCE
	@Override
	public void getRawdocs(String domainName, int domain, String user, RawdocParams params, int offset, int limit,
			AsyncCallback<LinkedList<Rawdoc>> callback) {
		AON.start();
		fsa.getRawdocs(domainName, domain, user, params, offset, limit, new AsyncCallbackWrapper<LinkedList<Rawdoc>>(callback));
	}

	@Override
	public void getDomainData(String domainName, int domain, String user, int searchDomain, AsyncCallback<LinkedList<RawdocDomainData>> callback) {
		AON.start();
		fsa.getDomainData(domainName, domain, user, searchDomain, new AsyncCallbackWrapper<LinkedList<RawdocDomainData>>(callback));
	}

	@Override
	public void parse(String domainName, int domain, String user, Integer rawdocId, AsyncCallback<TediResult> callback) {
		AON.start();
		fsa.parse(domainName, domain, user, rawdocId, new AsyncCallbackWrapper<TediResult>(callback));
	}
	
	@Override
	public void delete(String domainName, int domain, String user, Integer rawdocId, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, domain, user, rawdocId, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void toDraft(String domainName, int domain, String user, Integer rawdocId, AsyncCallback<Void> callback) {
		AON.start();
		fsa.toDraft(domainName, domain, user, rawdocId, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void toRejected(String domainName, int domain, String user, Integer rawdocId, String reason, AsyncCallback<Void> callback) {
		AON.start();
		fsa.toRejected(domainName, domain, user, rawdocId, reason, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void toInbox(String domainName, int domain, String user, Integer rawdocId, AsyncCallback<Void> callback) {
		AON.start();
		fsa.toInbox(domainName, domain, user, rawdocId, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void getAccountingInvoice(String domainName, int domain, String user, String invoice, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getAccountingInvoice(domainName, domain, user, invoice, new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}
	
	@Override
	public void processInvoiceFile(String domainName, int domain, String user, String jsonStr, Invoice invoice, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.processInvoiceFile(domainName, domain, user, jsonStr, invoice, new AsyncCallbackWrapper<Boolean>(callback));
	}
	
	@Override
	public void getS3Url(Rawdoc rawdoc, AsyncCallback<String> callback) {
		AON.start();
		fsa.getS3Url(rawdoc, new AsyncCallbackWrapper<String>(callback));
	}
	
}
