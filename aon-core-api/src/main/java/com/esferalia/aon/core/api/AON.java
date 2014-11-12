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
import com.esferalia.aon.core.impl.jooq.AccountingImpl;

public class AON {


	private static IAccounting getAccounting() {
		return new AccountingImpl();
	}
	
	// ********************************************
	// ****************************** ACCOUNTING **
	// ********************************************
	
	// ------------------------------------ ACCOUNT
	public static Account fetchAccount(AONContext ctx,Integer accountId) {
		return getAccounting().fetchAccount(ctx, accountId);
	}

	// ----------------------------- ACCOUNT PERIOD
	public static AccountPeriod fetchPeriod(AONContext ctx,Date date) {
		return getAccounting().fetchPeriod(ctx, date);
	}
	public static AccountPeriod fetchPeriod(AONContext ctx,Integer id) {
		return getAccounting().fetchPeriod(ctx, id);
	}
	public static AccountPeriod fetchPeriod(AONContext ctx,Condition condition) {
		return getAccounting().fetchPeriod(ctx, condition);
	}
	public static void insert(AONContext ctx,AccountPeriod ap) {
		getAccounting().insert(ctx, ap);
	}
	public static void update(AONContext ctx,AccountPeriod ap) {
		getAccounting().update(ctx, ap);
	}
	public static void delete(AONContext ctx,AccountPeriod ap) {
		getAccounting().delete(ctx, ap);
	}
	

	// ------------------------------ ACCOUNT ENTRY
	public static AccountEntry fetchOneAccountEntry(AONContext ctx,Condition condition) {
		return getAccounting().fetchOneAccountEntry(ctx,condition);
	}
	public static Seq<AccountEntry> fetchAccountEntry(AONContext ctx,Condition condition, int offset, int numberOfRows) {
		return getAccounting().fetchAccountEntry(ctx,condition,offset,numberOfRows);
	}
	public static Seq<AccountEntry> fetchAccountEntry(AONContext ctx,Condition condition, int offset, int numberOfRows, Function<ResultSet, AccountEntry> function) {
		return getAccounting().fetchAccountEntry(ctx,condition,offset,numberOfRows,function);
	}
	public static String fetchAccountEntryCSV(AONContext ctx,Condition condition, int offset, int numberOfRows) {
		return getAccounting().fetchAccountEntryCSV(ctx,condition,offset,numberOfRows);
	}
	public static boolean existsAnyEntry(AONContext ctx,Integer period, AccountEntryType accountEntryType) {
		return getAccounting().existsAnyEntry(ctx, period,accountEntryType);
	}
	public static void insert(AONContext ctx,AccountEntry ae) {
		getAccounting().insert(ctx, ae);
	}
	public static void update(AONContext ctx,AccountEntry ae) {
		getAccounting().update(ctx, ae);
	}
	public static void delete(AONContext ctx,AccountEntry ae) {
		getAccounting().delete(ctx, ae);
	}
	public static AccountEntry insertSalaryEntry(AONContext ctx,SalaryAccountEntry sae) {
		return getAccounting().insertSalaryEntry(ctx, sae);
	}

}
