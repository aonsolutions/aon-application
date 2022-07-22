package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AccountEntryServiceAsyncDecorator implements AccountEntryServiceAsync {

	private AccountEntryServiceAsync fsa;

	public AccountEntryServiceAsyncDecorator(AccountEntryServiceAsync serviceAsync) {
		this.fsa = serviceAsync;
	}
	
	@Override
	public void getAccountEntry(Occam occam, int id, AsyncCallback<AccountEntry> callback) {
		AON.start();
		fsa.getAccountEntry(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	
	@Override
	public void getAccountEntries(String domainName, int domain, String user, AccountEntryParams params, int offset, int limit, AsyncCallback<LinkedList<AccountEntry>> callback) {
		AON.start();
		fsa.getAccountEntries(domainName, domain, user, params, offset, limit, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAccountEntry(String domainName, int domain, String user, int id, AsyncCallback<AccountEntry> callback) {
		AON.start();
		fsa.getAccountEntry(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(String domainName, int domain, String user, AccountEntry ae, AsyncCallback<AccountEntry> callback) {
		AON.start();
		fsa.save(domainName, domain, user, ae, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteAccountEntry(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteAccountEntry(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initializeInvoice(String domainName, int domain, String user, AccountingRegistry registry, Integer activity, Date issueDate, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.initializeInvoice(domainName, domain, user, registry, activity, issueDate, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void initializeInvoice(String domainName, int domain, String user, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.initializeInvoice(domainName, domain, user, registry, ai, preserveData, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void removeInvoiceAttach(String domainName, int domain, String user, Integer invoiceId, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.removeInvoiceAttach(domainName, domain, user, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void addInvoiceAttach(String domainName, int domain, String user, AccountingInvoice ai, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.addInvoiceAttach(domainName, domain, user, ai, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAccountingInvoice(String domainName, int domain, String user, Integer accountEntry, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getAccountingInvoice(domainName, domain, user, accountEntry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAccountingInvoiceFromInvoice(String domainName, int domain, String user, Integer invoiceId, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getAccountingInvoiceFromInvoice(domainName, domain, user, invoiceId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getPendingImportAccountingInvoices(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<AccountingInvoice>> callback) {
		AON.start();
		fsa.getPendingImportAccountingInvoices(domainName, domain, user, query, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(String domainName, int domain, String user, AccountingInvoice invoice, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.save(domainName, domain, user, invoice, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryLastAccountingInvoice(String domainName, int domain, String user, Integer registryId, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getRegistryLastAccountingInvoice(domainName, domain, user, registryId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void rectifyInvoice(String domainName, int domain, String user, Integer invoiceId, InvoiceRectificationData data, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.rectifyInvoice(domainName, domain, user, invoiceId, data, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSalaryEntries(String domainName, int domain, String user, Date from, Date to, AsyncCallback<LinkedList<SalaryEntry>> callback) {
		AON.start();
		fsa.getSalaryEntries(domainName, domain, user, from, to, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSalaryFormatted(String domainName, int domain, String user, Date from, Date to, AsyncCallback<String> callback) {
		AON.start();
		fsa.getSalaryFormatted(domainName, domain, user, from, to, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getFinanceEntry(String domainName, int domain, String user, Integer accountEntry, AsyncCallback<FinanceEntry> callback) {
		AON.start();
		fsa.getFinanceEntry(domainName, domain, user, accountEntry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(String domainName, int domain, String user, FinanceEntry financeEntry, AsyncCallback<FinanceEntry> asyncCallback) {
		AON.start();
		fsa.save(domainName, domain, user, financeEntry, new AsyncCallbackWrapper<>(asyncCallback));
	}

	@Override
	public void updateSpecial(String domainName, int domain, String user, AccountEntryUpdate operation, IAccountEntryWrapper wrapper, AsyncCallback<IAccountEntryWrapper> callback) {
		AON.start();
		fsa.updateSpecial(domainName, domain, user, operation, wrapper, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAvailableAccountEntryUpdates(String domainName, int domain, String user, IAccountEntryWrapper wrapper, AsyncCallback<LinkedList<AccountEntryUpdate>> callback) {
		AON.start();
		fsa.getAvailableAccountEntryUpdates(domainName, domain, user, wrapper, new AsyncCallbackWrapper<>(callback));
	}
}
