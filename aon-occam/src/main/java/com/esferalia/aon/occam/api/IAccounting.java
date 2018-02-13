package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.error.AonCoreException;

public interface IAccounting {
	//		  	REGISTRY
	public Stream<AccountingRegistry> getAccountingRegistries(AONContext ctx, AccountingRegistryFilter filter);
	public AccountingRegistry insert(AONContext ctx, AccountingRegistry reg);
	
	// 			ACCOUNT
	public Account getAccount(AONContext ctx,Integer accountId);		
	public Account getAccount(AONContext ctx,String code);
	public Stream<Account> getAccounts(AONContext ctx,AccountFilter filter);
	public Account save(AONContext ctx, Account account);
	public String getAccountNextCode(AONContext ctx, String prefix);

	// 			ACCOUNT PERIOD
	public LinkedList<AccountPeriod> getDomainPeriods(AONContext ctx);
	public AccountPeriod fetchPeriod(AONContext ctx,Date date);
	public AccountPeriod fetchPeriod(AONContext ctx,Integer id);
	public AccountPeriod fetchPeriodByYear(AONContext ctx,int year);
	public void insert(AONContext ctx,AccountPeriod ap);
	public void update(AONContext ctx,AccountPeriod ap);
	public void delete(AONContext ctx,AccountPeriod ap);

	// 			ACCOUNT ENTRY
	public AccountEntry getAccountEntry(AONContext ctx,Integer id);
	public Stream<AccountEntry> getAccountEntries(AONContext ctx,AccountEntryParams params
			, int offset, int numberOfRows);
	public Stream<AccountEntry> getAccountEntries(AONContext ctx,AccountEntryFilter filter
			, int offset, int numberOfRows);
	public boolean existsAnyEntry(AONContext ctx,Integer period, AccountEntryType accountEntryType);
	public Integer save(AONContext ctx,AccountEntry ae);
	public void delete(AONContext ctx,Integer id);
	public AccountingInvoice getAccountingInvoice(AONContext ctx, Integer accountEntry);
	public AccountingInvoice getAccountingInvoiceFromInvoice(AONContext ctx, Integer invoiceId);
	public AccountingInvoice save(AONContext ctx, AccountingInvoice invoice);
	public IAccountEntryWrapper getAccountEntryWrapper(AONContext ctx, Integer accountEntry);
	public AccountingInvoice initializeInvoice(AONContext ctx, AccountingRegistry registry, Integer activity, Date issueDate);
	public AccountingInvoice getRegistryLastAccountingInvoice(AONContext ctx, Integer registryId);
	public AccountingInvoice rectifyInvoice(AONContext ctx, Integer invoiceId, InvoiceRectificationData data);
	public LinkedList<SalaryEntry> getSalaryEntries(AONContext ctx, Date from, Date to);
	public String getSalaryFormatted(AONContext ctx, Date from, Date to);
	
	// 			BALANCE
	public LinkedHashMap<String, AccountBalance> 
		getAccountBalances(AONContext ctx,AccMiningParameters params) throws AonCoreException;

	// 			STATEMENT
	public Stream<AccountStatement> getAccountBalance(AONContext ctx, AccountStatementParams params) 
			throws AonCoreException;
	public Stream<AccountStatement> getAccountStatement(AONContext ctx, AccountStatementParams params)
			throws AonCoreException;
	
	public Stream<Finance> getAccountFinances(AONContext ctx, FinanceParams params,
			int offset, int limit);
	public FinanceEntry save(AONContext ctx, FinanceEntry financeEntry);
	public FinanceEntry getFinanceEntry(AONContext ctx, Integer accountEntry);
	
	
}
