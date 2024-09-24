package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccountEntryServiceAsync {

	void getAccountEntry(Occam occam, int id, AsyncCallback<AccountEntry> callback);
	void initializeInvoice(Occam occam, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData, AsyncCallback<AccountingInvoice> callback);
	void initializeInvoice(Occam occam, AccountingRegistry registry, Integer activity, Date issueDate, AsyncCallback<AccountingInvoice> callback);
	void getPendingImportAccountingInvoices(Occam occam, String query, AsyncCallback<LinkedList<AccountingInvoice>> asyncCallback);
	void save(Occam occam, AccountingInvoice invoice, AsyncCallback<AccountingInvoice> callback);
	void save(Occam occam, FinanceEntry financeEntry, AsyncCallback<FinanceEntry> asyncCallback);
	void save(Occam occam, AccountEntry ae, AsyncCallback<AccountEntry> callback);
	void deleteAccountEntry(Occam occam, Integer id, AsyncCallback<Void> callback);
	void getFinanceEntry(Occam occam, Integer accountEntry, AsyncCallback<FinanceEntry> asyncCallback);
	void rectifyInvoice(Occam occam, Integer invoiceId, InvoiceRectificationData data, AsyncCallback<AccountingInvoice> asyncCallback);
	void getAccountingInvoice(Occam occam, Integer accountEntry, AsyncCallback<AccountingInvoice> callback);
	void getAccountingInvoiceFromInvoice(Occam occam, Integer invoiceId, AsyncCallback<AccountingInvoice> callback);
	void getRegistryLastAccountingInvoice(Occam occam, Integer registryId, AsyncCallback<AccountingInvoice> asyncCallback);
	
	void getAccountEntry(Occam occam, Invoice invoice, AsyncCallback<AccountEntry> callback);

	void addInvoiceAttach(Occam occam, AccountingInvoice ai, AsyncCallback<AccountingInvoice> callback);
	void removeInvoiceAttach(Occam occam, Integer invoiceId, AsyncCallback<AccountingInvoice> callback);
	
	void getSalaryEntries(Occam occam, Date from, Date to, AsyncCallback<LinkedList<SalaryEntry>> callback);
	void getSalaryFormatted(Occam occam, Date from, Date to, AsyncCallback<String> callback);
	//	-----------------------------------
	
	void getAccountEntries(String domainName, int domain, String user, AccountEntryParams params, int offset, int limit, AsyncCallback<LinkedList<AccountEntry>> callback);
//	void getAccountEntry(String domainName, int domain, String user, int id, AsyncCallback<AccountEntry> callback);
	void getAccountingInvoice(String domainName, int domain, String user, Integer accountEntry, AsyncCallback<AccountingInvoice> callback);
	void updateSpecial(String domainName, int domain, String user, AccountEntryUpdate operation, IAccountEntryWrapper wrapper, AsyncCallback<IAccountEntryWrapper> callback);
	void getAvailableAccountEntryUpdates(String domainName, int domain, String user, IAccountEntryWrapper wrapper, AsyncCallback<LinkedList<AccountEntryUpdate>> callback);

}
