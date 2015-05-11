package com.esferalia.aon.occam.api;

import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.lambda.Seq;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.error.AonCoreException;

public interface IAccounting {

	// 				   		  ACCOUNT
	public Account fetchAccount(AONContext ctx,Integer accountId);		
	public Account fetchAccount(AONContext ctx,String code);

	// 				   ACCOUNT PERIOD
	public AccountPeriod fetchPeriod(AONContext ctx,Date date);
	public AccountPeriod fetchPeriod(AONContext ctx,Integer id);
	public AccountPeriod fetchPeriod(AONContext ctx,Condition condition);
	public void insert(AONContext ctx,AccountPeriod ap);
	public void update(AONContext ctx,AccountPeriod ap);
	public void delete(AONContext ctx,AccountPeriod ap);

	// 					ACCOUNT ENTRY
	public AccountEntry fetchOneAccountEntry(AONContext ctx,Condition condition);
	public Seq<AccountEntry> fetchAccountEntry(AONContext ctx,Condition condition, int offset, int numberOfRows);
	public Seq<AccountEntry> fetchAccountEntry(AONContext ctx,Condition condition, int offset, int numberOfRows, Function<ResultSet, AccountEntry> function);
	public String fetchAccountEntryCSV(AONContext ctx,Condition condition, int offset, int numberOfRows);
	public boolean existsAnyEntry(AONContext ctx,Integer period, AccountEntryType accountEntryType);
	public void insert(AONContext ctx,AccountEntry ae);
	public void update(AONContext ctx,AccountEntry ae);
	public void delete(AONContext ctx,AccountEntry accountEntry);
	public AccountEntry insertSalaryEntry(AONContext ctx,SalaryAccountEntry sae) throws AonCoreException;

	// 					      BALANCE
	public LinkedHashMap<String, AccountBalance> 
		getAccountBalances(AONContext ctx,AccMiningParameters params) throws AonCoreException;

}
