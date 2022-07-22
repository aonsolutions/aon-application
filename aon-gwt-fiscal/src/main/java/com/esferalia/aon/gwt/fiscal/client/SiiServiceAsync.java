package com.esferalia.aon.gwt.fiscal.client;

import java.util.List;

import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SiiServiceAsync {

	void getSiiConfiguration(String domainName, int domainId, String user, AsyncCallback<SiiConfiguration> callback);
	void getInvoices(String domainName, int domainId, String user, InvoiceParams params, AsyncCallback<List<Invoice>> callback);
	void altaLroe140(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<ICResponse> callback);
	void bajaLroe140(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	void altaLroe240(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<ICResponse> callback);
	void bajaLroe240(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	void altaSii(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	void bajaSii(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams,
			AsyncCallback<String> callback);
	
	void refresh140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback);
	void refresh240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams, AsyncCallback<Boolean> callback);

}
