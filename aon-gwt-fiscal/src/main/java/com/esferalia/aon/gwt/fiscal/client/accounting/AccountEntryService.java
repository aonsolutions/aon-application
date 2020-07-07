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
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AccountEntry")
public interface AccountEntryService extends RemoteService {

	LinkedList<AccountEntry> getAccountEntries(String domainName, int domain, String user, AccountEntryParams params, int offset, int limit) throws AonCoreException;

	AccountEntry getAccountEntry(String domainName, int domain, String user, int id) throws AonCoreException;

	AccountEntry save(String domainName, int domain, String user, AccountEntry ae) throws AonCoreException;

	void deleteAccountEntry(String domainName, int domain, String user, Integer id) throws AonCoreException;

	AccountingInvoice getAccountingInvoice(String domainName, int domain, String user, Integer accountEntry) throws AonCoreException;

	AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, String user, Integer invoiceId) throws AonCoreException;

	LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(String domainName, int domain, String user, String query) throws AonCoreException;

	AccountingInvoice save(String domainName, int domain, String user, AccountingInvoice invoice) throws AonCoreException;

	AccountingInvoice initializeInvoice(String domainName, int domain, String user, AccountingRegistry registry, Integer activity, Date issueDate) throws AonCoreException;

	AccountingInvoice getRegistryLastAccountingInvoice(String domainName, int domain, String user, Integer registryId) throws AonCoreException;

	AccountingInvoice rectifyInvoice(String domainName, int domain, String user, Integer id, InvoiceRectificationData data) throws AonCoreException;

	LinkedList<SalaryEntry> getSalaryEntries(String domainName, int domain, String user, Date from, Date to) throws AonCoreException;

	String getSalaryFormatted(String domainName, int domain, String user, Date from, Date to) throws AonCoreException;

	FinanceEntry getFinanceEntry(String domainName, int domain, String user, Integer accountEntry) throws AonCoreException;

	FinanceEntry save(String domainName, int domain, String user, FinanceEntry financeEntry) throws AonCoreException;
	

}
