package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAccounting;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountEntryWrapper;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Filter.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.Filter.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountBalanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO.AccountEntryOrder;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingUtilitiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AnalyticalAccountingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalaryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalaryFormatter;
import com.esferalia.aon.occam.server.accounting.AccountEntryUtils;
import com.esferalia.aon.occam.server.finance.FinanceUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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
	
	@Override
	public AccountingRegistry update(AONContext ctx, AccountingRegistry reg) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.update(ctx, reg));
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
	@Override
	public Stream<Account> getAccounts(AONContext ctx, AccountParams params) {
		return AccountDAO.getAccounts(ctx, params);
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
	public Account delete(AONContext ctx, Account account) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountDAO.delete(ctx, account));
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
		return AccountPeriodDAO.getPeriod(ctx, date);
	}

	@Override
	public AccountPeriod fetchPeriod(AONContext ctx, Integer id) {
		return AccountPeriodDAO.getPeriod(ctx, id);
	}

	@Override
	public AccountPeriod fetchPeriodByYear(AONContext ctx, int year) {
		return AccountPeriodDAO.getPeriodByYear(ctx, year);
	}

	@Override
	public AccountPeriod insert(AONContext ctx, AccountPeriod ap) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AccountPeriodDAO.insert(ctx, ap)
		 );		
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
					p -> AccountEntryUtils.getFilterByLines(ctx,p, params) 
					,offset,numberOfRows,
					AccountEntryOrder.safeEnum(params.getOrder())
					)
			: AccountEntryDAO.fetch(ctx,
				p -> AccountEntryUtils.getFilterByHeader(ctx,p, params) 
				,offset,numberOfRows,AccountEntryOrder.safeEnum(params.getOrder()))
			;
	}

	@Override
	public Stream<FlatAccountEntryDetail> getFlatAccountEntries(AONContext ctx, AccountEntryParams params, int offset, int limit, IDAOCallback callback) {
		return params.hasDetailProperties()
			?AccountEntryDAO.fetchFlatByLines (ctx, p -> AccountEntryUtils.getFilterByLines(ctx,p, params),AccountEntryOrder.safeEnum(params.getOrder()),offset, limit, callback)
			:AccountEntryDAO.fetchFlatByHeader(ctx, p -> AccountEntryUtils.getFilterByHeader(ctx,p, params),AccountEntryOrder.safeEnum(params.getOrder()),offset, limit, callback)
			;
	}

	@Override
	public Stream<FlatAccountEntryDetail> getLedger(AONContext ctx, AccountingReportParams params, int offset, int limit, IDAOCallback callback) {
		return AccountStatementDAO.ledger(ctx, params, offset, limit, callback); 
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
	public boolean isUndeductibleInvoice(AONContext ctx, Integer id) {
		return AccountingInvoiceDAO.isUndeductibleInvoice(ctx, id);
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
	public AccountingInvoice initializeInvoice(AONContext ctx, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData) {
		if (ai.getInvoice().getIssueDate() == null) {
			throw new AonCoreException("No se puede inicializar una factura sin fecha");
		}
		if (registry == null) {
			throw new AonCoreException("No se pudo encontrar al titular de factura \"" + registry + "\"");
		}
		if (registry.getType() == null) {
			throw new AonCoreException("No se puede inicializar una factura sin tipo");
		}
		return AccountingInvoiceDAO.initializeInvoice(ctx, registry.getType().getInvoiceType(), registry.getId(), ai, preserveData);
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
	public LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(AONContext ctx, String query) {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?((AonStringUtils.isNumeric(query)? AonStringUtils.EMPTY:AonStringUtils.PERCENT) 
			 			+ query 
			 			+ AonStringUtils.PERCENT)
				:(query);
		
		return InvoiceDAO.getInvoiceHeaders(ctx, p ->
				p.getDomainProperty().eq(ctx.getDomainId())
				 .and(p.getRegistryDocumentProperty().like(q)
				  .or(p.getRegistryNameProperty().like(q))
				  .or(p.getReferenceCodeProperty().like(q))
				  )
				 .and(p.getTransactionProperty().eq(InvoiceTransactionType.EXTRACOMMUNITY.value())
				  .or(p.getTransactionProperty().eq(InvoiceTransactionType.CAN_CEU_MEL.value()))
				  )
			,0,50)
			.filter( inv -> AccountingInvoiceDAO.isPresentInInvoiceDUA(ctx, inv.getId()) )
			.map( inv -> AccountingInvoiceDAO.getAccountingInvoiceFromInvoice(ctx, inv.getId()) )
			.filter( ai -> ai != null )
			.filter( ai -> ai.getDuaInvoice() == null )
			.collect(Collectors.toCollection(LinkedList::new));
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
	public LinkedHashMap<String, AccountBalance>  getAccountBalances(AONContext ctx,AccMiningParameters params) throws AonCoreException {
		return AccountEntryDAO.fetchBalance(ctx, params);		
	}
	public LinkedHashMap<String, AccountBalance> getAccountBalances(AONContext ctx,AccMiningParameters params, boolean pyg) throws AonCoreException {
		return AccountEntryDAO.fetchBalance(ctx, params,pyg);		
	}
	// 					      STATEMENT
	@Override
	public Stream<AccountStatement> getAccountStatement(AONContext ctx, AccountingReportParams params)
			throws AonCoreException {
		return AccountStatementDAO.statement(ctx, params);
	}
	@Override
	public AccountOperatingReport getAccountOperatingReport(AONContext ctx, AccountingReportParams params) throws AonCoreException {
		return AccountStatementDAO.operatingReport(ctx, params);
	}
	@Override
	public AccountTrialBalanceReport getTrialBalanceReport(AONContext ctx, AccountingReportParams params) throws AonCoreException {
		return AccountStatementDAO.trialBalance(ctx, params);
	}
	
	@Override
	public AccountBalanceReport getBalanceReport(AONContext ctx, AccountingReportParams params) throws AonCoreException {
		return AccountBalanceDAO.balanceReport(ctx, params);
	}

	@Override
	public Stream<AccountStatement> getAccountBalance(AONContext ctx, AccountingReportParams params)
			throws AonCoreException {
		return AccountStatementDAO.balance(ctx, params);		
	}
	@Override
	public Stream<Finance> getAccountFinances(AONContext ctx, FinanceParams params, int offset, int limit) {
		return FinanceEntryDAO.accountFetch(ctx, 
					p -> FinanceUtils.getPendingFilter(p, params) 
					,offset,limit, FinanceDAO.FinanceOrder.safeEnum( params.getOrder() ))
				;
	}
	@Override
	public FinanceEntry save(AONContext ctx, FinanceEntry financeEntry) {
		return ctx.getDslContext().transactionResult(
			configuration -> FinanceEntryDAO.save(ctx, financeEntry)
		 );		
	}
	@Override
	public FinanceEntry getFinanceEntry(AONContext ctx, Integer accountEntry) {
		return FinanceEntryDAO.getFinanceEntry(ctx, accountEntry);
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
	public AccUtilitiesResult accountIntegrity(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.accountIntegrity(ctx)
			 );		
	}
	@Override
	public AccUtilitiesResult accountIntegrityFix(AONContext ctx, Account account) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.accountIntegrityFix(ctx, account)
			 );		
	}
	@Override
	public AccUtilitiesResult domainIntegrity(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.domainIntegrity(ctx)
			 );		
	}
	@Override
	public AccUtilitiesResult domainIntegrityFix(AONContext ctx, Account account) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.domainIntegrityFix(ctx, account)
			 );		
	}
	@Override
	public AccUtilitiesResult noLowLevelAccounts(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.noLowLevelAccounts(ctx)
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
	@Override
	public AccUtilitiesResult wrongRecordedInvoices(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.wrongRecordedInvoices(ctx)
			 );		
	}
	@Override
	public AccUtilitiesResult removeWrongRecordedInvoice(AONContext ctx, Integer accountEntryId) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.removeWrongRecordedInvoice(ctx,accountEntryId)
			 );		
	}
	@Override
	public AccUtilitiesResult removeWrongCheckedInvoice(AONContext ctx, Integer invoice) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.removeWrongCheckedInvoice(ctx,invoice)
			 );		
	}	
	@Override
	public AccUtilitiesResult getAccountLinks(AONContext ctx, AccUtilitiesParams params) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.getAccountLinks(ctx,params)
			 );		
	}
	@Override
	public String changeAccountDescription(AONContext ctx, Integer accountId, String newDescription) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.changeAccountDescription(ctx,accountId, newDescription)
			 );		
	}
	@Override
	public Account createAndLinkAccount(AONContext ctx, AccountingRegistryType registryType, Integer registryId) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.createAndLinkAccount(ctx,registryType, registryId)
			 );		
	}
	@Override
	public AccUtilitiesResult getJournalRegenerationInfo(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.getJournalRegenerationInfo(ctx)
			 );		
	}
	@Override
	public AccUtilitiesResult regenerateJournal(AONContext ctx, Integer accuountPeriod) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.regenerateJournal(ctx,accuountPeriod)
			 );		
	}
		
	@Override
	public AccUtilitiesResult getInputVatRegenerationInfo(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.getInputVatRegenerationInfo(ctx)
			 );		
	}
	@Override
	public AccUtilitiesResult regenerateInputVat(AONContext ctx, Integer year) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountingUtilitiesDAO.regenerateInputVat(ctx,year)
			 );		
	}
	
	// ANALYTIC ACCOUNTING	
	@Override
	public AccountingAnalyticalReport getAccountAnalyticalReport(AONContext ctx, AccountingReportParams params) throws AonCoreException {
		return AnalyticalAccountingDAO.analyticalReport(ctx, params);
	}
	@Override
	public AccountingAnalyticalReport getAccountAnalyticalReport(AONContext ctx, AccountingReportParams params,
			Analytical analytical) throws AonCoreException {
		return AnalyticalAccountingDAO.analyticalReport(ctx, params,analytical);
	}
	@Override
	public Analytical saveAnalyticConfiguration(AONContext ctx, Analytical analytical) {
		return ctx.getDslContext().transactionResult(
				configuration -> AnalyticalAccountingDAO.save(ctx,analytical)
		 );		
	}
}
