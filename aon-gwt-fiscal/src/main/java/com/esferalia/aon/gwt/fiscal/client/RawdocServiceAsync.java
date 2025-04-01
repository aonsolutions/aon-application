package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RawdocServiceAsync {

	// --------------------------------------------------------------- INVOICE SERIES
	void getRawdocs(Occam occam, RawdocParams params, int offset, int limit,AsyncCallback<LinkedList<Rawdoc>> callback);
	void delete(Occam occam, Integer rawdocId, AsyncCallback<Void> callback);
	
	void toDraft(Occam occam, Integer rawdocId, AsyncCallback<Rawdoc> callback);
	void toRejected(Occam occam, Integer rawdocId, String reason, AsyncCallback<Rawdoc> callback);
	void toInbox(Occam occam, Integer rawdocId, AsyncCallback<Rawdoc> callback);
	
	// --------------------------------------------------------------- PENDING 
	void getAccountingInvoice(String domainName, int domain, String user, String invoice,AsyncCallback<AccountingInvoice> callback);
	void processInvoiceFile(String domainName, int domain, String user, String jsonStr, Invoice invoice,AsyncCallback<Boolean> callback);
	void parse(String domainName, int domain, String user, Integer id, AsyncCallback<TediResult> asyncCallback);
	void parse(Occam occam, Integer id, AsyncCallback<TediResult> asyncCallback);
	void getS3Url(Rawdoc rawdoc, AsyncCallback<String> callback);

}
