package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAccounting;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountEntryWrapper;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Filter.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO.AccountEntryOrder;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingUtilitiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalaryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalaryFormatter;
import com.esferalia.aon.occam.server.accounting.AccountEntryUtils;
import com.esferalia.aon.occam.server.finance.FinanceUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AccountingImpl implements IAccounting {

	// --------- REGISTRY -------------------------------------------------
	@Override
	public Stream<AccountingRegistry> getAccountingRegistries(AONContext ctx, AccountingRegistryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getAccountingRegistries(ctx, filter));
	}
	@Override
	public AccountingRegistry insert(AONContext ctx, AccountingRegistry reg) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insert(ctx, reg));
	}
	
	// --------- ACCOUNT -------------------------------------------------
	@Override
	public Account getAccount(AONContext ctx, Integer accountId) {
		return AccountDAO.get(ctx, accountId);
	}
	@Override
	public Account getAccount(AONContext ctx, String code) {
		return AccountDAO.get(ctx, code);
	}
	public Stream<Account> getAccounts(AONContext ctx,AccountFilter filter) {
		return AccountDAO.getAccounts(ctx, filter);
	}
	@Override
	public Account save(AONContext ctx, Account account) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountDAO.save(ctx, account));
	}
	@Override
	public String getAccountNextCode(AONContext ctx, String prefix) {
		return AccountDAO.getNextAccountCode(ctx, prefix);
	}

	// --------- ACCOUNT PERIOD ------------------------------------------
	@Override
	public LinkedList<AccountPeriod> getDomainPeriods(AONContext ctx) {
		return AccountPeriodDAO.getDomainPeriods(ctx)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	@Override
	public AccountPeriod fetchPeriod(AONContext ctx, Date date) {
		return AccountPeriodDAO.fetchOne(ctx, date);
	}

	@Override
	public AccountPeriod fetchPeriod(AONContext ctx, Integer id) {
		return AccountPeriodDAO.fetchOne(ctx, id);
	}

	@Override
	public AccountPeriod fetchPeriodByYear(AONContext ctx, int year) {
		return AccountPeriodDAO.fetchOneByYear(ctx, year);
	}

	@Override
	public void insert(AONContext ctx, AccountPeriod ap) {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodDAO.insert(ctx, ap);
		} );		
	}

	@Override
	public void update(AONContext ctx, AccountPeriod ap) {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodDAO.update(ctx, ap);
		} );		
	}

	@Override
	public void delete(AONContext ctx, AccountPeriod ap) {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodDAO.delete(ctx, ap);
		} );		
	}
	
	// --------- ACCOUNT ENTRY -------------------------------------------
	@Override
	public Stream<AccountEntry> getAccountEntries(AONContext ctx,
			AccountEntryParams params, int offset, int numberOfRows) {
		return params.hasDetailProperties()
			? AccountEntryDAO.fetchByLines(ctx,
					p -> AccountEntryUtils.getFilterByLines(p, params) 
					,offset,numberOfRows,
					AccountEntryOrder.safeEnum(params.getOrder())
					)
			: AccountEntryDAO.fetch(ctx,
				p -> AccountEntryUtils.getFilter(p, params) 
				,offset,numberOfRows,AccountEntryOrder.safeEnum(params.getOrder()))
			;
	}

	@Override
	public Stream<AccountEntry> getAccountEntries(AONContext ctx,
			AccountEntryFilter filter, int offset, int numberOfRows) {
		return AccountEntryDAO.fetch(ctx, filter,offset,numberOfRows);
	}
	
	@Override
	public AccountEntry getAccountEntry(AONContext ctx, Integer id) {
		return AccountEntryDAO.getAccountEntry(ctx, id);
	}

	@Override
	public boolean existsAnyEntry(AONContext ctx, Integer period,
			AccountEntryType accountEntryType) {
		return AccountEntryDAO.existsAnyEntry(ctx, period,accountEntryType);
	}

	@Override
	public Integer save(AONContext ctx, AccountEntry ae) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountEntryDAO.save(ctx, ae )
		 );		
	}

	@Override
	public void delete(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> {
			AccountEntryDAO.delete(ctx, id);
		} );		
	}
	
	@Override
	public AccountingInvoice getAccountingInvoice(AONContext ctx, Integer accountEntry) {
		return AccountingInvoiceDAO.getAccountingInvoice(ctx, accountEntry);
	}
	@Override
	public AccountingInvoice getAccountingInvoiceFromInvoice(AONContext ctx, Integer invoiceId) {
		return AccountingInvoiceDAO.getAccountingInvoiceFromInvoice(ctx, invoiceId);
	}
	@Override
	public AccountingInvoice getRegistryLastAccountingInvoice(AONContext ctx, Integer registryId) {
		return AccountingInvoiceDAO.duplicateLastAccountingInvoice(ctx, registryId);
	}

	@Override
	public AccountingInvoice initializeInvoice(AONContext ctx, AccountingRegistry registry, Integer activity, Date issueDate) {
		if (issueDate == null) {
			throw new AonCoreException("No se puede inicializar una factura sin fecha");
		}
		if (registry == null) {
			throw new AonCoreException("No se pudo encontrar al titular de factura \"" + registry + "\"");
		}
		if (registry.getType() == null) {
			throw new AonCoreException("No se puede inicializar una factura sin tipo");
		}
		return AccountingInvoiceDAO.initializeInvoice(ctx, registry.getType().getInvoiceType(), registry.getId(), activity, issueDate);
	}
	
	
	

	@Override
	public AccountingInvoice save(final AONContext ctx, AccountingInvoice invoice) {
		final Date atDate = (invoice.getInvoice() == null? null : invoice.getInvoice().getIssueDate());
		return ctx.getDslContext().transactionResult(
			configuration -> AccountingInvoiceDAO.save(ctx
				, ConfigurationDAO.getConfiguration(ctx, atDate)
				, invoice)
		 );		
	}

	@Override
	public AccountingInvoice rectifyInvoice(AONContext ctx, Integer invoiceId, InvoiceRectificationData data) {
		return ctx.getDslContext().transactionResult(
			configuration -> AccountingInvoiceDAO.rectifyInvoice(ctx, invoiceId, data) 
		 );
	}

	
	
	@Override
	public IAccountEntryWrapper getAccountEntryWrapper(AONContext ctx, Integer accountEntry) {
		AccountEntry entry = getAccountEntry(ctx, accountEntry);
		if (entry != null) {
			if (entry.isInvoice()) {
				return getAccountingInvoice(ctx, accountEntry);
			}
			return  new AccountEntryWrapper(entry);
		}
		return null;
	}

	@Override
	public LinkedList<SalaryEntry> getSalaryEntries(AONContext ctx, Date from, Date to) {
		LinkedList<AccountEntry> entries = AccountEntryDAO.fetch(ctx,  
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().between(from, to))
					.and(p.getEntryTypeProperty().eq((byte) AccountEntryType.SALARY.ordinal()))
				, 0, 1000)
			.collect(Collectors.toCollection(LinkedList::new));
		LinkedList<SalaryEntry> salaries = SalaryDAO.getSalaryEntries(ctx, from, to)
				.collect(Collectors.toCollection(LinkedList::new));
		for (SalaryEntry salary : salaries ) {
			Date salaryDate = salary.getAccountEntry().getEntryDate();
			for (AccountEntry entry : entries) {
				Date entryDate = entry.getEntryDate();
				if (AonDateUtils.isSameDay(salaryDate, entryDate)) {
					salary.setAccountEntry(entry);
					break;
				}
			}
		}
		return salaries;
	}

	@Override
	public String getSalaryFormatted(AONContext ctx, Date from, Date to) {
		LinkedList<SalaryEntry> salaries = SalaryDAO.getSalaryEntries(ctx, from, to, false)
				.collect(Collectors.toCollection(LinkedList::new));
		return SalaryFormatter.formatSalariesForAccount("N\u00F3minas", salaries);
	}
	
	// 					      BALANCE
	public LinkedHashMap<String, AccountBalance>
		getAccountBalances(AONContext ctx,AccMiningParameters params) throws AonCoreException {
		return AccountEntryDAO.fetchBalance(ctx, params);		
	}
	// 					      STATEMENT
	@Override
	public Stream<AccountStatement> getAccountStatement(AONContext ctx, AccountStatementParams params)
			throws AonCoreException {
		return AccountStatementDAO.statement(ctx, params);
	}

	@Override
	public Stream<AccountStatement> getAccountBalance(AONContext ctx, AccountStatementParams params)
			throws AonCoreException {
		return AccountStatementDAO.balance(ctx, params);		
	}
	@Override
	public Stream<Finance> getAccountFinances(AONContext ctx, FinanceParams params, int offset, int limit) {
		return FinanceDAO.accountFetch(ctx, 
					p -> FinanceUtils.getPendingFilter(p, params) 
					,offset,limit)
				;
	}
	@Override
	public FinanceEntry save(AONContext ctx, FinanceEntry financeEntry) {
		return ctx.getDslContext().transactionResult(
			configuration -> FinanceDAO.save(ctx, financeEntry)
		 );		
	}
	@Override
	public FinanceEntry getFinanceEntry(AONContext ctx, Integer accountEntry) {
		return FinanceDAO.getFinanceEntry(ctx, accountEntry);
	}
	
	// Accounting Utilities
	@Override
	public AccUtilitiesResult checkParentLinker(AONContext ctx, Account account) {
		return AccountingUtilitiesDAO.checkParentLinker(ctx, account);
	}
	@Override
	public AccUtilitiesResult runParentLinker(AONContext ctx, Account account) {
		return ctx.getDslContext().transactionResult(
			configuration -> AccountingUtilitiesDAO.runParentLinker(ctx, account)
		 );		
	}
	@Override
	public AccUtilitiesResult emptyEntries(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.emptyEntries(ctx)
			 );		
	}
	@Override
	public AccUtilitiesResult unbalancedEntries(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.unbalancedEntries(ctx)
			 );		
	}
		
}
