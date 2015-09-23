package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.error.AonCoreException;

public interface IAccounting {

	// 				   		  ACCOUNT
	public Account getAccount(AONContext ctx,Integer accountId);		
	public Account getAccount(AONContext ctx,String code);
	public Stream<Account> getAccounts(AONContext ctx,AccountFilter filter);

	// 				   ACCOUNT PERIOD
	public AccountPeriod fetchPeriod(AONContext ctx,Date date);
	public AccountPeriod fetchPeriod(AONContext ctx,Integer id);
	public AccountPeriod fetchPeriod(AONContext ctx,Condition condition);
	public void insert(AONContext ctx,AccountPeriod ap);
	public void update(AONContext ctx,AccountPeriod ap);
	public void delete(AONContext ctx,AccountPeriod ap);

	// 					ACCOUNT ENTRY
	public Stream<AccountEntry> getAccountEntries(AONContext ctx,AccountEntryFilter filter, int offset, int numberOfRows);
	public boolean existsAnyEntry(AONContext ctx,Integer period, AccountEntryType accountEntryType);
	public Integer insert(AONContext ctx,AccountEntry ae);
	public void update(AONContext ctx,AccountEntry ae);
	public void delete(AONContext ctx,Integer id);
	public AccountEntry getAccountEntry(AONContext ctx,SalaryAccountEntry sae) throws AonCoreException;
	public List<Integer> insertSalaryEntries(String domainName, int domain,
			Date from, Date to, String concept, Integer registryBank);

	// 					      BALANCE
	public LinkedHashMap<String, AccountBalance> 
		getAccountBalances(AONContext ctx,AccMiningParameters params) throws AonCoreException;

}
