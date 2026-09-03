package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DomainInvoiceStat;
import com.esferalia.aon.occam.api.model.DomainInvoiceStatParams;
import com.esferalia.aon.occam.api.model.Filter.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.Filter.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountingAmortization;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetailFlat;
import com.esferalia.aon.occam.api.model.accounting.AmortizationInvoice;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.error.AonCoreException;

public interface IAccounting {
	
	// **************************************************
	// **************************************** [ACCOUNT]
	// **************************************************
	public Account getAccount(AONContext ctx,Integer accountId);		
	public Account getAccount(AONContext ctx,String code);
	public Stream<Account> getAccounts(AONContext ctx,AccountFilter filter);
	public Stream<Account> getAviablesAccountsForBank(AONContext ctx,AccountFilter filter);
	public Stream<Account> getAccounts(AONContext ctx,AccountFilter filter, int offset, int limit);
	public Account save(AONContext ctx, Account account);
	public Account delete(AONContext ctx, Account account);
	public String getAccountNextCode(AONContext ctx, String prefix);
	public Stream<Account> getAccounts(AONContext ctx, AccountParams params);
	public List<Account> getAccountsList(AONContext ctx, AccountParams params);
	public List<Account> getSuggestedAccounts(AONContext ctx, Integer registry, InvoiceType type);
	
	public Account createAccountsForBank(CloseableAONContext ctx, Integer domain, String alias,  String suffixCode);
	
	
	// **************************************************
	// ********************************* [ACCOUNT PERIOD]
	// **************************************************
	public LinkedList<AccountPeriod> getDomainPeriods(AONContext ctx);
	public AccountPeriod ensurePeriod(AONContext ctx, Integer domain, Date date);
	public AccountPeriod getPeriod(AONContext ctx,Date date);
	public AccountPeriod getPeriod(AONContext ctx,Integer id);
	public AccountPeriod getPeriodByYear(AONContext ctx,int year);
	public AccountPeriod save(AONContext ctx,AccountPeriod ap);
	public void delete(AONContext ctx,AccountPeriod ap);

	//		  	REGISTRY
	public AccountingRegistry initialize(AONContext ctx, AccountingRegistry ar);
	public Stream<AccountingRegistry> getAccountingRegistries(AONContext ctx, AccountingRegistryFilter filter);
	public AccountingRegistry insert(AONContext ctx, AccountingRegistry reg);
	public AccountingRegistry update(AONContext ctx, AccountingRegistry reg);

	// 			ACCOUNT ENTRY
	public AccountEntry getAccountEntry(AONContext ctx,Integer id);
	public Stream<AccountEntry> getAccountEntries(AONContext ctx,AccountEntryParams params
			, int offset, int numberOfRows);
	public Stream<AccountEntry> getAccountEntries(AONContext ctx,AccountEntryFilter filter
			, int offset, int numberOfRows);
	public Stream<FlatAccountEntryDetail> getFlatAccountEntries(AONContext ctx, AccountEntryParams params, int offset, int limit, IDAOCallback callback);
	public boolean existsAnyEntry(AONContext ctx,Integer period, AccountEntryType accountEntryType);
	public Integer save(AONContext ctx,AccountEntry ae);
	public void delete(AONContext ctx,Integer id);
	public AccountingInvoice getAccountingInvoice(AONContext ctx, Integer accountEntry);
	public AccountingInvoice getAccountingInvoiceFromInvoice(AONContext ctx, Integer invoiceId);
	public AccountingInvoice getOrInitializeAccountingInvoiceFromInvoice(AONContext ctx, Integer invoiceId);
	public LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(AONContext ctx, String query);
	public LinkedList<AccountingInvoice> getRegistryNotRectifiedAccountingInvoices(AONContext ctx, Integer registry, String query);
	public AccountingInvoice save(AONContext ctx, AccountingInvoice invoice);
	public IAccountEntryWrapper getAccountEntryWrapper(AONContext ctx, Integer accountEntry);
	public AccountingInvoice initializeInvoice(AONContext ctx, AccountingRegistry registry, Integer activity, Date issueDate);
	public AccountingInvoice initializeInvoice(AONContext ctx, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData);
	public AccountingInvoice removeInvoiceAttach(AONContext ctx, Integer invoiceId);
	public AccountingInvoice addInvoiceAttach(AONContext ctx, AccountingInvoice ai);
	public AccountingInvoice getRegistryLastAccountingInvoice(AONContext ctx, Integer registryId);
	public AccountingInvoice rectifyInvoice(AONContext ctx, Integer invoiceId, InvoiceRectificationData data);
	public IAccountEntryWrapper  updateSpecial(AONContext ctx, AccountEntryUpdate operation, IAccountEntryWrapper wrapper);
	public LinkedList<AccountEntryUpdate> getAvailableAccountEntryUpdates(AONContext ctx, IAccountEntryWrapper wrapper);
	
	public LinkedList<SalaryEntry> getSalaryEntries(AONContext ctx, Date from, Date to);
	public String getSalaryFormatted(AONContext ctx, Date from, Date to);
	public List<Account> generateLowerLevels(AONContext ctx, Account account, int minLevel);
	
	// 			BALANCE
	public LinkedHashMap<String, AccountBalance> getAccountBalances(AONContext ctx,AccMiningParameters params) throws AonCoreException;
	public LinkedHashMap<String, AccountBalance> getAccountBalances(AONContext ctx,AccMiningParameters params, boolean pyg) throws AonCoreException;

	// 			STATEMENT
	public Stream<FlatAccountEntryDetail> getLedger(AONContext ctx, AccountingReportParams params, int offset, int limit, IDAOCallback callback);
	public Stream<AccountStatement> getAccountBalance(AONContext ctx, AccountingReportParams params) 
			throws AonCoreException;
	public Stream<AccountStatement> getAccountStatement(AONContext ctx, AccountingReportParams params)
			throws AonCoreException;
	public AccountOperatingReport getAccountOperatingReport(AONContext ctx, AccountingReportParams params) throws AonCoreException;
	public AccountTrialBalanceReport getTrialBalanceReport(AONContext ctx, AccountingReportParams params) throws AonCoreException;
	public AccountBalanceReport getBalanceReport(AONContext ctx, AccountingReportParams params);
	
	
	public Stream<Finance> getAccountFinances(AONContext ctx, FinanceParams params,
			int offset, int limit);
	public FinanceEntry save(AONContext ctx, FinanceEntry financeEntry);
	public FinanceEntry getFinanceEntry(AONContext ctx, Integer accountEntry);
	
	// Accounting Utilities 
	public AccUtilitiesResult checkParentLinker(AONContext ctx, Account account);
	public AccUtilitiesResult runParentLinker(AONContext ctx, Account account);
	public AccUtilitiesResult accountIntegrity(AONContext ctx);
	public AccUtilitiesResult accountIntegrityFix(AONContext ctx, Account account);
	public AccUtilitiesResult domainIntegrity(AONContext ctx);
	public AccUtilitiesResult domainIntegrityFix(AONContext ctx, Account account);
	public AccUtilitiesResult noLowLevelAccounts(AONContext ctx);
	public AccUtilitiesResult emptyEntries(AONContext ctx);
	public AccUtilitiesResult unbalancedEntries(AONContext ctx);
	public AccUtilitiesResult outOfDateEntries(AONContext ctx);
	public AccUtilitiesResult moveOutOfDateEntries(AONContext ctx, AccUtilitiesResult findResult);
	public AccUtilitiesResult wrongRecordedInvoices(AONContext ctx);
	public AccUtilitiesResult removeWrongRecordedInvoice(AONContext ctx, Integer accountEntryId);
	public AccUtilitiesResult removeWrongCheckedInvoice(AONContext ctx, Integer invoice);
	public AccUtilitiesResult getAccountLinks(AONContext ctx, AccUtilitiesParams params);
	public String changeAccountDescription(AONContext ctx, Integer accountId, String newDescription);
	public Account createAndLinkAccount(AONContext ctx, AccountingRegistryType registryType, Integer registryId);
	public AccUtilitiesResult getJournalRegenerationInfo(AONContext ctx);
	public AccUtilitiesResult regenerateJournal(AONContext ctx, Integer accuountPeriod);
	public AccUtilitiesResult getInputVatRegenerationInfo(AONContext ctx);
	public AccUtilitiesResult regenerateInputVat(AONContext ctx, Integer year);
	public boolean isUndeductibleInvoice(AONContext ctx, Integer id);
	public AccUtilitiesResult removeEntries(AONContext ctx, AccountEntryParams params);
	public AccUtilitiesResult invoiceIntegrity(AONContext ctx);
	public AccUtilitiesResult invoiceIntegrityFix(AONContext ctx,Integer invoiceId);
	public AccUtilitiesResult searchAccountChange(AONContext ctx, AccUtilitiesAccountChangeParams params);
	public AccUtilitiesResult fixAccountChange(AONContext ctx, AccUtilitiesAccountChangeParams params, AccUtilitiesAccountChangeItem accountChanges);
	
	// ANALYTIC ACCOUNTING	
	public AccountingAnalyticalReport getAccountAnalyticalReport(AONContext ctx, AccountingReportParams params) throws AonCoreException;
	public AccountingAnalyticalReport getAccountAnalyticalReport(AONContext ctx, AccountingReportParams params, Analytical analytical) throws AonCoreException;
	public Analytical saveAnalyticConfiguration(AONContext ctx, Analytical analytical);
	
	// REPORT
	public Stream<OperationBreakdown> getOperationBreakdown(AONContext ctx, int domain, OperationParams params);
	
	// AMORTIZATION TYPE
	public List<AmortizationType> getAmortizationTypeList(CloseableAONContext ctx, AmortizationTypeParams params) throws AonCoreException;
	public void deleteAmortizationTypes(CloseableAONContext ctx, List<Integer> deleteIds) throws AonCoreException;
	public void saveAmortizationType(CloseableAONContext ctx, AmortizationType amortizationType) throws AonCoreException;

	// **************************************** [ACCOUNTING EXPENSES]
	public Stream<AccountingExpense> getAccountingExpenses(CloseableAONContext ctx, int domain, String query, int offset, int limit, IDAOCallback cbk) throws AonCoreException;
	public AccountingExpense saveAccountingExpense(AONContext ctx, AccountingExpense expense) throws AonCoreException;
	public void deleteAccountingExpense(AONContext ctx, AccountEntry ae) throws AonCoreException;

	// **************************************** [ACCOUNTING INCOMES]
	public Stream<AccountingIncome> getAccountingIncomes(CloseableAONContext ctx, int domain, String query, int offset, int limit, IDAOCallback cbk) throws AonCoreException;
	public AccountingIncome saveAccountingIncome(AONContext ctx, AccountingIncome income) throws AonCoreException;
	public void deleteAccountingIncome(AONContext ctx, AccountEntry ae) throws AonCoreException;

	// **************************************** [AMORTIZATION]
	public Optional<Amortization> getAmortization(AONContext ctx, Integer domain, Integer id) throws AonCoreException;
	public LinkedList<Amortization> getAmortizations(AONContext ctx, AmortizationParams params) throws AonCoreException;
	public LinkedList<AmortizationDetailFlat> getFlatAmortizations(AONContext ctx, AmortizationParams params) throws AonCoreException;
	public Amortization saveAmortization(AONContext ctx, Amortization am) throws AonCoreException;
	public Amortization saveFiscalAllocation(AONContext ctx, AmortizationDetail detail) throws AonCoreException;
	public void deleteAmortization(AONContext ctx, Amortization am) throws AonCoreException;
	public Amortization calculateAmortization(AONContext ctx, Amortization am) throws AonCoreException;
	public Amortization saleAmortization(AONContext ctx, Amortization am) throws AonCoreException;
	public AmortizationDetail recordAmortizationAllocation(AONContext ctx, Amortization am, AmortizationDetail detail) throws AonCoreException;
	public AmortizationDetail unrecordAmortizationAllocation(AONContext ctx, AmortizationDetail detail) throws AonCoreException;
	public AmortizationDetail blockAmortizationDetail(AONContext ctx, AmortizationDetail detail) throws AonCoreException;
	public AmortizationDetail unblockAmortizationDetail(AONContext ctx, AmortizationDetail detail) throws AonCoreException;
	public LinkedList<AmortizationInvoice> getAmortizationInvoices(AONContext ctx, Integer domain, Integer amortizationId);
	public void linkAmortizationInvoices(AONContext ctx, Integer domain, Integer amotizationId, Integer[] invoiceIds);
	public void unlinkAmortizationInvoice(AONContext ctx, Integer domain, Integer amortizationId, Integer invoiceId);
	public Invoice changeInvestment(CloseableAONContext ctx, Integer domain, Integer invoiceId);
	public Stream<AccountingAmortization> getAccountingAmortizations(AONContext ctx, Integer domain, AmortizationParams params);
	public void recordAmortizationDetails(AONContext ctx, Integer domain, Integer[] ids);
	public void unrecordAmortizationDetails(AONContext ctx, Integer domain, Integer[] ids);
	
	// ********************************** [INVOICES CONUNTERS]
	public Stream<DomainInvoiceStat> getDomainInvoiceStats(AONContext ctx, DomainInvoiceStatParams params);
	
}
