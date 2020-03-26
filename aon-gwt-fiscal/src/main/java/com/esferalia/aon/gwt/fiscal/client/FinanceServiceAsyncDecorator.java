package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
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
	public void getInvoiceSeries(String domainName, int domainId, String  user, Date from, Date to, boolean taxDate,
			AsyncCallback<LinkedList<InvoiceSeries>> callback) {
		AON.start();
		fsa.getInvoiceSeries(domainName,domainId,user, from, to, taxDate, new AsyncCallbackWrapper<LinkedList<InvoiceSeries>>(callback));
	}

	@Override
	public void getInvoiceNextNumber(String domainName, Integer domainId, String  user, Byte[] types, String series,
			AsyncCallback<Integer> callback) {
		AON.start();
		fsa.getInvoiceNextNumber(domainName,domainId,user, types, series, new AsyncCallbackWrapper<Integer>(callback));
	}

	// --------------------------------------------------------------- FINANCE
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
	public void undoFinance(String domainName, int domainId, String user, Integer finance, AsyncCallback<Finance> callback) {
		AON.start();
		fsa.undoFinance(domainName, domainId, user, finance, new AsyncCallbackWrapper<Finance>(callback));
	}

	@Override
	public void payFinance(String domainName, int domainId, String user, Finance finance,
			AsyncCallback<Finance> callback) {
		AON.start();
		fsa.payFinance(domainName, domainId, user, finance, new AsyncCallbackWrapper<Finance>(callback));
	}

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

	
}
