package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FinanceServiceAsync {

	// --------------------------------------------------------------- INVOICE SERIES
	void getInvoiceNextNumber(String domainName, Integer domainId, String  user, Byte[] types, String series, AsyncCallback<Integer> callback);
	void getInvoiceNextNumber(Occam occam, Byte[] types, String series, AsyncCallback<Integer> callback);
	
	// --------------------------------------------------------------- REGISTRY BANKS
	void getCompanyBanks(String domainName, int domainId, String user, AsyncCallback<LinkedList<RegistryBank>> callback);
	void getRegistryBanks(String domainName, int domainId, String user, Integer registry,AsyncCallback<LinkedList<RegistryBank>> callback);

	void getFinances(String domainName, int domain, String user, FinanceParams params, int offset, int limit,AsyncCallback<LinkedList<Finance>> callback);
	void getFinancesForInvoice(String domainName, int domainId, String user, Invoice invoice, AsyncCallback<LinkedList<Finance>> callback);
	void getFinanceTracking(String domainName, int domainId, String user, Integer finance,AsyncCallback<LinkedList<FinanceTracking>> callback);
	void settleFinance(String domainName, int domainId, String user, Integer finance, AsyncCallback<Finance> callback);
	void unSettleFinance(String domainName, int domainId, String user, Integer finance, AsyncCallback<Finance> callback);
	void undoFinance(String domainName, int domainId, String user, Integer finance, AsyncCallback<Finance> callback);
	void payFinance(String domainName, int domainId, String user, FinanceTracking finance, AsyncCallback<FinanceTracking> callback);
	void returnFinance(String domainName, int domainId, String user, FinanceTracking finance,AsyncCallback<FinanceTracking> callback);
	void deleteFinance(String domainName, int domain, String user, Integer financeId, AsyncCallback<Void> asyncCallback);
	
	void getAccountFinances(String domainName, int domain, String user, FinanceParams params, int offset, int limit, AsyncCallback<LinkedList<Finance>> callback);
	
	// --------------------------------------------------------------- VENCIMIENTO NOMINAS
	void createSettleSalaries(String domainName, int domain, String user, Date date, AsyncCallback<Void> asyncCallback);
	void createSepaFile(String domainName, int domain, String user, Integer fbatchId, AsyncCallback<Integer> asyncCallback);

	void getFBatches(String domainName, int domain, String user, FBatchParams params, int offset, int limit,AsyncCallback<LinkedList<FBatch>> callback);
	void getFBatch(String domainName, int domain, String user, Integer fbatchId, AsyncCallback<FBatch> callback);
	void deleteFBatches(String domainName, int domain, String user, LinkedList<Integer> fBatchIds, AsyncCallback<Void> callback);
	void createUpdateFBatch(String domainName, int domain, String user, FBatch fBatch, AsyncCallback<FBatch> callback);
	void deleteSepaFile(String domainName, int domain, String user, Integer rattachId, AsyncCallback<Void> asyncCallback);
	void recordFBatch(Occam occam, Integer fbatchId, Date paymentDate, AsyncCallback<FBatch> callback);
	void unrecordFBatch(Occam occam, Integer fbatchId, AsyncCallback<FBatch> callback);
	void getFBatchAccountEntry(Occam occam, Integer id, AsyncCallback<AccountEntry> callback);

}

