package com.esferalia.aon.gwt.fiscal.server;

import java.util.Date;
import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Account Entry Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/AccountEntry" })
public class AccountEntryServiceImpl extends AonStatelessRemoteServiceServlet implements AccountEntryService {

	private static final long serialVersionUID = 8791955004212947200L;

	@Override
	public LinkedList<AccountEntry> getAccountEntries(String domainName, int domain, String user, final AccountEntryParams params, int offset, int limit) throws AonCoreException {
		return ACCOUNTING.getAccountEntries(domainName, domain, user, params, offset, limit);
	}

	@Override
	public AccountEntry getAccountEntry(String domainName, int domain, String user, int id) throws AonCoreException {
		return ACCOUNTING.getAccountEntry(domainName, domain, user, id);
	}

	@Override
	public AccountEntry save(String domainName, int domain, String user, AccountEntry ae) throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, user, ae);
	}

	@Override
	public LinkedList<SalaryEntry> getSalaryEntries(String domainName, int domain, String user, Date from, Date to) {
		return ACCOUNTING.getSalaryEntries(domainName, domain, user, from, to);
	}

	@Override
	public String getSalaryFormatted(String domainName, int domain, String user, Date from, Date to) {
		return ACCOUNTING.getSalaryFormatted(domainName, domain, user, from, to);
	}

	@Override
	public void deleteAccountEntry(String domainName, int domain, String user, Integer id) {
		ACCOUNTING.deleteAccountEntry(domainName, domain, user, id);
	}

	@Override
	public AccountingInvoice initializeInvoice(String domainName, int domain, String user, AccountingRegistry registry, Integer activity, Date issueDate) throws AonCoreException {
		return ACCOUNTING.initializeInvoice(domainName, domain, user, registry, activity, issueDate);
	}

	@Override
	public AccountingInvoice getAccountingInvoice(String domainName, int domain, String user, Integer accountEntry) throws AonCoreException {
		return ACCOUNTING.getAccountingInvoice(domainName, domain, user, accountEntry);
	}

	@Override
	public AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, String user, Integer invoiceId) throws AonCoreException {
		return ACCOUNTING.getAccountingInvoiceFromInvoice(domainName, domain, user, invoiceId);
	}

	@Override
	public LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(String domainName, int domain, String user, String query) throws AonCoreException {
		return ACCOUNTING.getPendingImportAccountingInvoices(domainName, domain, user, query);
	}

	@Override
	public AccountingInvoice getRegistryLastAccountingInvoice(String domainName, int domain, String user, Integer registryId) {
		return ACCOUNTING.getRegistryLastAccountingInvoice(domainName, domain, user, registryId);
	}

	@Override
	public AccountingInvoice rectifyInvoice(String domainName, int domain, String user, Integer invoiceId, InvoiceRectificationData data) throws AonCoreException {
		return ACCOUNTING.rectifyInvoice(domainName, domain, user, invoiceId, data);
	}

	@Override
	public AccountingInvoice save(String domainName, int domain, String user, AccountingInvoice invoice) throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, user, invoice);
	}

	@Override
	public FinanceEntry save(String domainName, int domain, String user, FinanceEntry financeEntry) throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, user, financeEntry);
	}

	@Override
	public FinanceEntry getFinanceEntry(String domainName, int domain, String user, Integer accountEntry) {
		return ACCOUNTING.getFinanceEntry(domainName, domain, user, accountEntry);
	};

}
