package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("roms/AccountingReport")
public interface AccountingReportService extends RemoteService {

	// -------------------------------- ------------------------------- ACCOUNT
	// PERIOD
	LinkedList<AccountPeriod> getDomainPeriods(String domainName, int domain, String user) throws AonCoreException;

	// --------------------------------------------------------------- ACCOUNT
	// STATEMENT
	AccountStatementReport getAccountStatement(String domainName, String user, int domain, AccountingReportParams params) throws AonCoreException;

	LinkedList<AccountStatement> getAccountBalance(String domainName, int domain, String user, AccountingReportParams params) throws AonCoreException;

	// --------------------------------------------------------------- ACCOUNT
	// OPERATING STATEMENT
	AccountOperatingReport getAccountOperatingReport(String domainName, String user, int domain, AccountingReportParams params) throws AonCoreException;

	// --------------------------------------------------------------- ACCOUNT TRIAL
	// BALANCE
	AccountTrialBalanceReport getAccountTrialBalanceReport(String domainName, String user, int domain, AccountingReportParams params) throws AonCoreException;

	// --------------------------------------------------------------- ACCOUNT
	// BALANCE
	AccountBalanceReport getAccountBalanceReport(String domainName, String user, int domain, AccountingReportParams params) throws AonCoreException;


}
