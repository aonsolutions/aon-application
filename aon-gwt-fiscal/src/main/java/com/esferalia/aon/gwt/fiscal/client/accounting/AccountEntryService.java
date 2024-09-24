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
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AccountEntry")
public interface AccountEntryService extends RemoteService {

	AccountEntry getAccountEntry(Occam occam, int id) throws AonCoreException;
	AccountingInvoice initializeInvoice(Occam occam, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData) throws AonCoreException;
	AccountingInvoice initializeInvoice(Occam occam, AccountingRegistry registry, Integer activity, Date issueDate) throws AonCoreException;
	LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(Occam occam, String query) throws AonCoreException;
	AccountingInvoice save(Occam occam, AccountingInvoice invoice) throws AonCoreException;
	FinanceEntry save(Occam occam, FinanceEntry financeEntry) throws AonCoreException;
	AccountEntry save(Occam occam, AccountEntry ae) throws AonCoreException;
	void deleteAccountEntry(Occam occam, Integer id) throws AonCoreException;
	FinanceEntry getFinanceEntry(Occam occam, Integer accountEntry) throws AonCoreException;
	AccountingInvoice rectifyInvoice(Occam occam, Integer invoiceId, InvoiceRectificationData data) throws AonCoreException;
	AccountingInvoice getAccountingInvoice(Occam occam, Integer accountEntry) throws AonCoreException;
	AccountingInvoice getAccountingInvoiceFromInvoice(Occam occam, Integer invoiceId) throws AonCoreException;
	AccountingInvoice getRegistryLastAccountingInvoice(Occam occam, Integer registryId) throws AonCoreException;

	AccountEntry getAccountEntry(Occam occam, Invoice invoice) throws AonCoreException; 	

	AccountingInvoice addInvoiceAttach(Occam occam, AccountingInvoice ai) throws AonCoreException;
	AccountingInvoice removeInvoiceAttach(Occam occam, Integer invoiceId) throws AonCoreException;
	
	LinkedList<SalaryEntry> getSalaryEntries(Occam occam, Date from, Date to) throws AonCoreException;
	String getSalaryFormatted(Occam occam, Date from, Date to) throws AonCoreException;
	//	-----------------------------------

	LinkedList<AccountEntry> getAccountEntries(String domainName, int domain, String user, AccountEntryParams params, int offset, int limit) throws AonCoreException;
//	AccountEntry getAccountEntry(String domainName, int domain, String user, int id) throws AonCoreException;
	AccountingInvoice getAccountingInvoice(String domainName, int domain, String user, Integer accountEntry) throws AonCoreException;
	IAccountEntryWrapper  updateSpecial(String domainName, int domain, String user, AccountEntryUpdate operation, IAccountEntryWrapper wrapper) throws AonCoreException;
	LinkedList<AccountEntryUpdate> getAvailableAccountEntryUpdates(String domainName, int domain, String user, IAccountEntryWrapper wrapper) throws AonCoreException;

}
