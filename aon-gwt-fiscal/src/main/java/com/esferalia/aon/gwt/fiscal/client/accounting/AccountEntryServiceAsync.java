package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccountEntryServiceAsync {

	void getAccountEntries(String domainName, int domain, String user, AccountEntryParams params, int offset, int limit, AsyncCallback<LinkedList<AccountEntry>> callback);

	void getAccountEntry(String domainName, int domain, String user, int id, AsyncCallback<AccountEntry> callback);

	void save(String domainName, int domain, String user, AccountEntry ae, AsyncCallback<AccountEntry> callback);

	void deleteAccountEntry(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback);

	void initializeInvoice(String domainName, int domain, String user, AccountingRegistry registry, Integer activity, Date issueDate, AsyncCallback<AccountingInvoice> callback);
	
	void initializeInvoice(String domainName, int domain, String user, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData, AsyncCallback<AccountingInvoice> callback);

	void getAccountingInvoice(String domainName, int domain, String user, Integer accountEntry, AsyncCallback<AccountingInvoice> callback);

	void getAccountingInvoiceFromInvoice(String domainName, int domain, String user, Integer invoiceId, AsyncCallback<AccountingInvoice> callback);

	void getPendingImportAccountingInvoices(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<AccountingInvoice>> asyncCallback);

	void save(String domainName, int domain, String user, AccountingInvoice invoice, AsyncCallback<AccountingInvoice> callback);

	void getRegistryLastAccountingInvoice(String domainName, int domain, String user, Integer registryId, AsyncCallback<AccountingInvoice> asyncCallback);

	void rectifyInvoice(String domainName, int domain, String user, Integer id, InvoiceRectificationData data, AsyncCallback<AccountingInvoice> asyncCallback);

	void getSalaryEntries(String domainName, int domain, String user, Date from, Date to, AsyncCallback<LinkedList<SalaryEntry>> callback);

	void getSalaryFormatted(String domainName, int domain, String user, Date from, Date to, AsyncCallback<String> callback);

	void getFinanceEntry(String domainName, int domain, String user, Integer accountEntry, AsyncCallback<FinanceEntry> asyncCallback);

	void save(String domainName, int domain, String user, FinanceEntry financeEntry, AsyncCallback<FinanceEntry> asyncCallback);
}
