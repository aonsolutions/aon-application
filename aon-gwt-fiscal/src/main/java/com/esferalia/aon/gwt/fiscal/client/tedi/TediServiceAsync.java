package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.tedi.TediCompanyResult;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

import es.translogia.tedi.ewok.TediInvoice;

public interface TediServiceAsync {

	void getAonConfiguration(String domainName, String user, int domain, AsyncCallback<AonConfiguration> callback);
	void getVerifiedInvoices(String domainName, String user, int domain, boolean snapshot, Company company, AsyncCallback<LinkedList<TediResult>> callback);
	void getInvoice(String domainName, String user, int domain, boolean snapshot, String uuid, String tediStatus, AsyncCallback<TediResult> callback);
	void acceptInvoice(String domainName, String user, int domain, boolean snapshot, TediInvoice invoice, AsyncCallback<TediResult> callback);
	void acceptInvoices(String domainName, String user, int domain, boolean snapshot, LinkedList<TediResult> results, AsyncCallback<LinkedList<TediResult>> callback);
	void rejectInvoice(String domainName, String user, int domain, boolean snapshot, TediInvoice invoice, AsyncCallback<TediResult> callback);
	void rejectInvoices(String domainName, String user, int domain, boolean tediSnapshot, LinkedList<TediResult> accepted, AsyncCallback<LinkedList<TediResult>> callback);
	void validateInvoice(String domainName, String user, int domain, boolean snapshot, TediResult result, AsyncCallback<TediResult> callback);
	void getInvoiceAttachURL(String domainName, String user, int domain, boolean snapshot, String uuid, AsyncCallback<String> callback);
	void getCompanies(String domainName, String user, int domain, boolean snapshot, boolean showNotTedi, AsyncCallback<LinkedList<TediCompanyResult>> callback);
	void tediSync(String domainName, String user, int domain, boolean snapshot, boolean showNotTedi, AsyncCallback<LinkedList<TediCompanyResult>> callback);
	void getCountInboxInvoices(String domainName, String user, int domain, boolean snapshot, Company company, AsyncCallback<Integer> callback);
	void addTediCompany(String domainName, String user, int domain, boolean snapshot, TediCompanyResult company, AsyncCallback<Void> callback);

}
