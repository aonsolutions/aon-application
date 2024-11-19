package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RawdocServiceAsync {

	// --------------------------------------------------------------- INVOICE SERIES
	void getRawdocs(String domainName, int domain, String user, RawdocParams params, int offset, int limit,AsyncCallback<LinkedList<Rawdoc>> callback);
	void getDomainData(String domainName, int domain, String user, int searchDomain, AsyncCallback<LinkedList<RawdocDomainData>> callback);
	void parse(String domainName, int domain, String user, Integer id, AsyncCallback<TediResult> asyncCallback);
	void delete(String domainName, int domain, String user, Integer rawdocId, AsyncCallback<Void> callback);
	void toDraft(String domainName, int domain, String user, Integer rawdocId, AsyncCallback<Void> callback);
	void toRejected(String domainName, int domain, String user, Integer rawdocId, String reason, AsyncCallback<Void> callback);
	void toInbox(String domainName, int domain, String user, Integer rawdocId, AsyncCallback<Void> callback);

	void getAccountingInvoice(String domainName, int domain, String user, String invoice,AsyncCallback<AccountingInvoice> callback);
	void processInvoiceFile(String domainName, int domain, String user, String jsonStr, Invoice invoice,AsyncCallback<Boolean> callback);

	void getS3Url(Rawdoc rawdoc, AsyncCallback<String> callback);

}
