package com.esferalia.aon.gwt.fiscal.server;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.occam.api.ACCOUNTING;
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

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Account Entry Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/AccountEntry" })
public class AccountEntryServiceImpl extends AonStatelessRemoteServiceServlet implements AccountEntryService {

	private static final long serialVersionUID = 8791955004212947200L;

	@Override
	public AccountEntry getAccountEntry(Occam occam, int id) throws AonCoreException {
		return ACCOUNTING.getAccountEntry(occam, id);
	}
	
	@Override
	public AccountingInvoice initializeInvoice(Occam occam, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData) {
		return ACCOUNTING.initializeInvoice(occam, registry, ai, preserveData);
	}

	@Override
	public AccountingInvoice initializeInvoice(Occam occam, AccountingRegistry registry, Integer activity, Date issueDate) throws AonCoreException {
		return ACCOUNTING.initializeInvoice(occam, registry, activity, issueDate);
	}
	
	@Override
	public FinanceEntry save(Occam occam, FinanceEntry financeEntry) throws AonCoreException {
		return ACCOUNTING.save(occam, financeEntry);
	}

	@Override
	public AccountEntry save(Occam occam, AccountEntry ae) throws AonCoreException {
		return ACCOUNTING.save(occam, ae);
	}

	@Override
	public void deleteAccountEntry(Occam occam, Integer id) {
		ACCOUNTING.deleteAccountEntry(occam, id);
	}

	@Override
	public FinanceEntry getFinanceEntry(Occam occam, Integer accountEntry) {
		return ACCOUNTING.getFinanceEntry(occam, accountEntry);
	}

	@Override
	public AccountingInvoice rectifyInvoice(Occam occam, Integer invoiceId, InvoiceRectificationData data) throws AonCoreException {
		return ACCOUNTING.rectifyInvoice(occam, invoiceId, data);
	}

	@Override
	public AccountingInvoice getAccountingInvoice(Occam occam, Integer accountEntry) throws AonCoreException {
		return ACCOUNTING.getAccountingInvoice(occam, accountEntry);
	}
	
	@Override
	public AccountingInvoice getAccountingInvoiceFromInvoice(Occam occam, Integer invoiceId) throws AonCoreException {
		return ACCOUNTING.getAccountingInvoiceFromInvoice(occam, invoiceId);
	}

	@Override
	public AccountingInvoice getRegistryLastAccountingInvoice(Occam occam, Integer registryId) {
		return ACCOUNTING.getRegistryLastAccountingInvoice(occam, registryId);
	}

	@Override
	public AccountEntry getAccountEntry(Occam occam, AonConfiguration config, Invoice invoice) throws AonCoreException {
		return ACCOUNTING.getAccountEntry(occam, config, invoice);
	}
	
	@Override
	public LinkedList<SalaryEntry> getSalaryEntries(Occam occam, Date from, Date to) {
		return ACCOUNTING.getSalaryEntries(occam, from, to);
	}

	@Override
	public String getSalaryFormatted(Occam occam, Date from, Date to) {
		return ACCOUNTING.getSalaryFormatted(occam, from, to);
	}
	
	// *************************************	
	// *************************************	
	// *************************************	
	// *************************************	

	@Override
	public LinkedList<AccountEntry> getAccountEntries(String domainName, int domain, String user, final AccountEntryParams params, int offset, int limit) throws AonCoreException {
		return ACCOUNTING.getAccountEntries(domainName, domain, user, params, offset, limit);
	}

//	@Override
//	public AccountEntry getAccountEntry(String domainName, int domain, String user, int id) throws AonCoreException {
//		return ACCOUNTING.getAccountEntry(domainName, domain, user, id);
//	}

	@Override
	public AccountingInvoice removeInvoiceAttach(Occam occam, Integer invoiceId) throws AonCoreException {
		return ACCOUNTING.removeInvoiceAttach(occam, invoiceId);
	}

	@Override
	public AccountingInvoice addInvoiceAttach(Occam occam, AccountingInvoice ai) throws AonCoreException {
		return ACCOUNTING.addInvoiceAttach(occam, ai);
	}

	@Override
	public AccountingInvoice getAccountingInvoice(String domainName, int domain, String user, Integer accountEntry) throws AonCoreException {
		return ACCOUNTING.getAccountingInvoice(domainName, domain, user, accountEntry);
	}

	@Override
	public LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(Occam occam, String query) throws AonCoreException {
		return ACCOUNTING.getPendingImportAccountingInvoices(occam, query);
	}

	@Override
	public AccountingInvoice save(Occam occam, AccountingInvoice invoice) throws AonCoreException {
		return ACCOUNTING.save(occam, invoice);
	}

	@Override
	public IAccountEntryWrapper updateSpecial(String domainName, int domain, String user, AccountEntryUpdate operation, IAccountEntryWrapper wrapper) throws AonCoreException {
		return ACCOUNTING.updateSpecial(domainName, domain, user, operation, wrapper);
	}

	@Override
	public LinkedList<AccountEntryUpdate> getAvailableAccountEntryUpdates(String domainName, int domain, String user, IAccountEntryWrapper wrapper) throws AonCoreException {
		return ACCOUNTING.getAvailableAccountEntryUpdates(domainName, domain, user, wrapper);
	}

}
