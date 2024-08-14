package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FinanceServiceAsyncDecorator implements FinanceServiceAsync {

	private FinanceServiceAsync fsa;

	public FinanceServiceAsyncDecorator(FinanceServiceAsync financeServiceAsync) {
		this.fsa = financeServiceAsync;
	}

	// --------------------------------------------------------------- INVOICE SERIES
	@Override
	public void getInvoiceNextNumber(String domainName, Integer domainId, String  user, Byte[] types, String series, AsyncCallback<Integer> callback) {
		AON.start();
		fsa.getInvoiceNextNumber(domainName,domainId,user, types, series, new AsyncCallbackWrapper<Integer>(callback));
	}
	
	@Override
	public void getInvoiceNextNumber(Occam occam, Byte[] types, String series, AsyncCallback<Integer> callback) {
		AON.start();
		fsa.getInvoiceNextNumber(occam, types, series, new AsyncCallbackWrapper<Integer>(callback));
	}

	// --------------------------------------------------------------- REGISTRY BANKS
	@Override
	public void getCompanyBanks(String domainName, int domainId, String user,
			AsyncCallback<LinkedList<RegistryBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(domainName, domainId, user, new AsyncCallbackWrapper<LinkedList<RegistryBank>>(callback));
	}

	@Override
	public void getRegistryBanks(String domainName, int domainId, String user, Integer registry,
			AsyncCallback<LinkedList<RegistryBank>> callback) {
		AON.start();
		fsa.getRegistryBanks(domainName, domainId, user, registry, new AsyncCallbackWrapper<LinkedList<RegistryBank>>(callback));
	}
	
	// --------------------------------------------------------------- FINANCE
	@Override
	public void getFinances(String domainName, int domain, String user, FinanceParams params, int offset, int limit,
			AsyncCallback<LinkedList<Finance>> callback) {
		AON.start();
		fsa.getFinances(domainName, domain, user, params, offset, limit, new AsyncCallbackWrapper<LinkedList<Finance>>(callback));
	}
	
	@Override
	public void getFinancesForInvoice(String domainName, int domainId, String user, Invoice invoice, AsyncCallback<LinkedList<Finance>> callback) {
		AON.start();
		fsa.getFinancesForInvoice(domainName, domainId, user, invoice, new AsyncCallbackWrapper<LinkedList<Finance>>(callback));
	}
	
	@Override
	public void getFinanceTracking(String domainName, int domainId, String user, Integer finance,
			AsyncCallback<LinkedList<FinanceTracking>> callback) {
		AON.start();
		fsa.getFinanceTracking(domainName, domainId, user, finance, new AsyncCallbackWrapper<LinkedList<FinanceTracking>>(callback));
	}

	@Override
	public void settleFinance(String domainName, int domainId, String user, Integer finance, AsyncCallback<Finance> callback) {
		AON.start();
		fsa.settleFinance(domainName, domainId, user, finance, new AsyncCallbackWrapper<Finance>(callback));
	}
	
	@Override
	public void unSettleFinance(String domainName, int domainId, String user, Integer finance, AsyncCallback<Finance> callback) {
		AON.start();
		fsa.unSettleFinance(domainName, domainId, user, finance, new AsyncCallbackWrapper<Finance>(callback));
	}

	@Override
	public void undoFinance(String domainName, int domainId, String user, Integer finance, AsyncCallback<Finance> callback) {
		AON.start();
		fsa.undoFinance(domainName, domainId, user, finance, new AsyncCallbackWrapper<Finance>(callback));
	}

	@Override
	public void payFinance(String domainName, int domainId, String user, FinanceTracking finance,
			AsyncCallback<FinanceTracking> callback) {
		AON.start();
		fsa.payFinance(domainName, domainId, user, finance, new AsyncCallbackWrapper<FinanceTracking>(callback));
	}

	@Override
	public void returnFinance(String domainName, int domainId, String user, FinanceTracking finance,
			AsyncCallback<FinanceTracking> callback) {
		AON.start();
		fsa.returnFinance(domainName, domainId, user, finance, new AsyncCallbackWrapper<FinanceTracking>(callback));
	}

	@Override
	public void deleteFinance(String domainName, int domain, String user, Integer financeId, AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteFinance(domainName, domain, user, financeId, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getAccountFinances(String domainName, int domain, String user, FinanceParams params, int offset, int limit, AsyncCallback<LinkedList<Finance>> callback) {
		AON.start();
		fsa.getAccountFinances(domainName, domain, user, params, offset, limit, new AsyncCallbackWrapper<LinkedList<Finance>>(callback));
	}

	// --------------------------------------------------------------- VENCIMIENTO NOMINAS
	
	@Override
	public void createSettleSalaries(String domainName, int domain, String user, Date date, AsyncCallback<Void> callback) {
		AON.start();
		fsa.createSettleSalaries(domainName, domain, user, date, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void createSepaFile(String domainName, int domain, String user, Integer fbatchId, AsyncCallback<Integer> callback) {
		AON.start();
		fsa.createSepaFile(domainName, domain, user, fbatchId, new AsyncCallbackWrapper<Integer>(callback));
	}

	@Override
	public void getFBatches(String domainName, int domain, String user, FBatchParams params, int offset, int limit, AsyncCallback<LinkedList<FBatch>> callback) {
		AON.start();
		fsa.getFBatches(domainName, domain, user, params, offset, limit, new AsyncCallbackWrapper<LinkedList<FBatch>>(callback));
	}
	
	@Override
	public void getFBatch(String domainName, int domain, String user, Integer fbatchId, AsyncCallback<FBatch> callback) {
		AON.start();
		fsa.getFBatch(domainName, domain, user, fbatchId, new AsyncCallbackWrapper<FBatch>(callback));
	}

	@Override
	public void deleteFBatches(String domainName, int domain, String user, LinkedList<Integer> fBatchIds, AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteFBatches(domainName, domain, user, fBatchIds, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void createUpdateFBatch(String domainName, int domain, String user, FBatch fBatch, AsyncCallback<FBatch> callback) {
		AON.start();
		fsa.createUpdateFBatch(domainName, domain, user, fBatch, new AsyncCallbackWrapper<FBatch>(callback));
	}

	@Override
	public void deleteSepaFile(String domainName, int domain, String user, Integer rattachId, AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteSepaFile(domainName, domain, user, rattachId, new AsyncCallbackWrapper<Void>(callback));
	}

	
}
