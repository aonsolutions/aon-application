package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FinanceServiceAsync {

	// --------------------------------------------------------------- INVOICE SERIES
	void getInvoiceSeries(String domainName, int domainId, String  user, Date from, Date to, boolean taxDate,AsyncCallback<LinkedList<InvoiceSeries>> callback );
	void getInvoiceNextNumber(String domainName, Integer domainId, String  user, Byte[] types, String series, AsyncCallback<Integer> callback);
	
	// --------------------------------------------------------------- REGISTRY BANKS
	void getCompanyBanks(String domainName, int domainId, String user, AsyncCallback<LinkedList<RegistryBank>> callback);
	void getRegistryBanks(String domainName, int domainId, String user, Integer registry,AsyncCallback<LinkedList<RegistryBank>> callback);

	// --------------------------------------------------------------- FINANCE
	void getFinancesForInvoice(String domainName, int domainId, String user, Invoice invoice, AsyncCallback<LinkedList<Finance>> callback);
	void getFinanceTracking(String domainName, int domainId, String user, Integer finance,AsyncCallback<LinkedList<FinanceTracking>> callback);
	void settleFinance(String domainName, int domainId, String user, Integer finance, AsyncCallback<Finance> callback);
	void undoFinance(String domainName, int domainId, String user, Integer finance, AsyncCallback<Finance> callback);
	void payFinance(String domainName, int domainId, String user, FinanceTracking finance, AsyncCallback<FinanceTracking> callback);
	void returnFinance(String domainName, int domainId, String user, FinanceTracking finance,AsyncCallback<FinanceTracking> callback);
	

}
