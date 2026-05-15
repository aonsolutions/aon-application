package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RawdocServiceAsync {

	// --------------------------------------------------------------- INVOICE SERIES
	void getRawdocs(Occam occam, RawdocParams params, int offset, int limit,AsyncCallback<LinkedList<Rawdoc>> callback);
	void delete(Occam occam, Integer rawdocId, AsyncCallback<Void> callback);
	void saveToAccounting(Occam occam, LinkedHashSet<Integer> rawdocIds, AsyncCallback<LinkedList<String>> callback);
	
	void toTrash(Occam occam, Integer rawdocId, AsyncCallback<Rawdoc> callback);
	void toRejected(Occam occam, Integer rawdocId, String reason, String email, AsyncCallback<Rawdoc> callback);
	void toInbox(Occam occam, Integer rawdocId, AsyncCallback<Rawdoc> callback);

	void getS3Url(Rawdoc rawdoc, AsyncCallback<String> callback);
	
	void parse(Occam occam, Integer id, AsyncCallback<TediResult> asyncCallback);
	
	// --------------------------------------------------------------- USER EMAIL
	void getUserEmail(Occam occam, String userLogin, AsyncCallback<String> callback);
	
	// --------------------------------------------------------------- PENDING 
//	void getAccountingInvoice(String domainName, int domain, String user, String invoice,AsyncCallback<AccountingInvoice> callback);
//	void processInvoiceFile(String domainName, int domain, String user, String jsonStr, Invoice invoice,AsyncCallback<Boolean> callback);
	
	

}
