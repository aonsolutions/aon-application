package com.esferalia.aon.gwt.fiscal.client;

import java.util.List;

import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InvoiceCommunicationServiceAsync {

	void getSiiConfiguration(String domainName, int domainId, String user, AsyncCallback<SiiConfiguration> callback);
	void getTbaiConfiguration(String domainName, int domainId, String user, AsyncCallback<TbaiConfiguration> callback);

	void getInvestAssets(String domainName, int domainId, String user, AsyncCallback<List<InvestAsset>> callback);
	void assignInvestAsset2Invoice(String domainName, int domainId, String user, String investAsset, Invoice invoice, AsyncCallback<Void> callback);

	void getInvoices(String domainName, int domainId, String user, InvoiceParams params, AsyncCallback<List<Invoice>> callback);
	void altaLroe(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<ICResponse> callback);
	void bajaLroe140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	void bajaLroe240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	void altaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	void bajaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	
	void refresh140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback);
	void refresh240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback);

	void cancel(String domainName, int domainId, String user, InvoiceCommunicationType type, Invoice invoice, AEATParams aeatParams, AsyncCallback<String> callback);
	
	void getInvoiceCommunicationTrackingList(String domainName, int domainId, String login, Integer invoice, AsyncCallback<List<InvoiceCommunicationTracking>> callback);
	void getRequestUrl(String domainName, int domainId, String login, Integer dataResponse, AsyncCallback<String> callback);
	void getResponseUrl(String domainName, int domainId, String login, Integer dataResponse, AsyncCallback<String> callback);

	void addDocumentInvoice(String domainName, int domainId, String user, Invoice invoice, AsyncCallback<Void> callback);
}
