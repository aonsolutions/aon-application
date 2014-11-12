package com.esferalia.aon.core.api;

import java.sql.ResultSet;
import java.util.Date;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.lambda.Seq;

import com.esferalia.aon.core.api.model.Account;
import com.esferalia.aon.core.api.model.AccountEntry;
import com.esferalia.aon.core.api.model.AccountPeriod;
import com.esferalia.aon.core.api.model.SalaryAccountEntry;
import com.esferalia.aon.core.api.model.type.AccountEntryType;

public interface IAccounting {

	// 				   		  ACCOUNT
	public Account fetchAccount(AONContext ctx,Integer accountId);		

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
	public AccountEntry insertSalaryEntry(AONContext ctx,SalaryAccountEntry sae);
	
}
