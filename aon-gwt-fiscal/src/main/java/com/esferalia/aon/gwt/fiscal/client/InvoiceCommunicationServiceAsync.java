package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InvoiceCommunicationServiceAsync {

	void getConfiguration(String domainName, int domainId, String user, AsyncCallback<InvoiceCommunicationConfiguration> callback);

	void getInvestAssets(String domainName, int domainId, String user, AsyncCallback<LinkedList<InvestAsset>> callback);
	void assignInvestAsset2Invoice(String domainName, int domainId, String user, String investAsset, Invoice invoice, AsyncCallback<Void> callback);

	void getInvoices(Occam occam, InvoiceCommunicationParams params, AsyncCallback<LinkedList<Invoice>> callback);
	void altaLroe(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<ICResponse> callback);
	void bajaLroe140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	void bajaLroe240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	void altaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<ICResponse> callback);
	void bajaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	
	void refresh140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback);
	void refresh240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback);

	void cancel(String domainName, int domainId, String user, InvoiceCommunicationType type, Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback);
	
	void getInvoiceCommunicationTrackings(String domainName, int domainId, String login, Integer invoice, AsyncCallback<LinkedList<InvoiceCommunicationTracking>> callback);
	void getRequestUrl(String domainName, int domainId, String login, Integer dataResponse, AsyncCallback<String> callback);
	void getResponseUrl(String domainName, int domainId, String login, Integer dataResponse, AsyncCallback<String> callback);

	void addDocumentInvoice(String domainName, int domainId, String user, Invoice invoice, AsyncCallback<Void> callback);
	
	void prepareNewSii(String domainName, int domainId, String user, Integer year, AsyncCallback<Void> callback);
}
