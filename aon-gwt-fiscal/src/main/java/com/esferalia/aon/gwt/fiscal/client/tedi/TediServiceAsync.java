package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

import es.translogia.tedi.ewok.TediInvoice;

public interface TediServiceAsync {

	void getAonConfiguration(String domainName, String user, int domain, AsyncCallback<AonConfiguration> callback);
	void getVerifiedInvoices(String domainName, String user, int domain, boolean snapshot, AsyncCallback<LinkedList<TediResult>> callback);
	void getInvoice(String domainName, String user, int domain, boolean snapshot, String uuid, String tediStatus, AsyncCallback<TediResult> callback);
	void putInvoice(String domainName, String user, int domain, boolean snapshot, TediInvoice invoice, AsyncCallback<TediResult> callback);
	void putInvoices(String domainName, String user, int domain, boolean snapshot, LinkedList<TediResult> results, AsyncCallback<LinkedList<TediResult>> callback);
	void validateInvoice(String domainName, String user, int domain, boolean snapshot, TediResult result, AsyncCallback<TediResult> callback);
	void getInvoiceAttachURL(String domainName, String user, int domain, boolean snapshot, String uuid, AsyncCallback<String> callback);
}
