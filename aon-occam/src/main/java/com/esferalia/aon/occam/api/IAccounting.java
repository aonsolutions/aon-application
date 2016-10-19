package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.error.AonCoreException;

public interface IAccounting {
	//		  	REGISTRY
	public Stream<AccountingRegistry> getAccountingRegistries(AONContext ctx, AccountingRegistryFilter filter);
	// 			ACCOUNT
	public Account getAccount(AONContext ctx,Integer accountId);		
	public Account getAccount(AONContext ctx,String code);
	public Stream<Account> getAccounts(AONContext ctx,AccountFilter filter);
	public Account insert(AONContext ctx, Account account);

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
	public AccountEntry getAccountEntry(AONContext ctx,SalaryAccountEntry sae) throws AonCoreException;
	public LinkedList<AccountEntry> previewSalaryEntries(String domainName, int domain,String user,
			Date from, Date to, String concept, Integer registryBank);
	public List<Integer> insertSalaryEntries(String domainName, int domain,String user,
			Date from, Date to, String concept, Integer registryBank);
	public AccountingInvoice initializeInvoice(AONContext ctx, AccountEntry entry, AccountingRegistry registry);
	public AccountingInvoice getAccountingInvoice(AONContext ctx, Integer accountEntry);
	public AccountingInvoice save(AONContext ctx, AccountingInvoice invoice);
	
	// 			BALANCE
	public LinkedHashMap<String, AccountBalance> 
		getAccountBalances(AONContext ctx,AccMiningParameters params) throws AonCoreException;

	// 			STATEMENT
	public Stream<AccountStatement> getAccountBalance(AONContext ctx, AccountStatementParams params) 
			throws AonCoreException;
	public Stream<AccountStatement> getAccountStatement(AONContext ctx, AccountStatementParams params)
			throws AonCoreException;
	
}
