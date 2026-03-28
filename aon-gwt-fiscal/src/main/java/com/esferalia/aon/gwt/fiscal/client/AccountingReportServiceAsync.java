package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccountingReportServiceAsync {

	// ACCOUNT PERIOD
	void getDomainPeriods(Occam occam, AsyncCallback<LinkedList<AccountPeriod>> callback);

	// ACCOUNT STATEMENT
	void getAccountStatement(Occam occam, AccountingReportParams params, AsyncCallback<AccountStatementReport> callback) throws AonCoreException;

	void getAccountBalance(String domainName, int domain, String user, AccountingReportParams params, AsyncCallback<LinkedList<AccountStatement>> callback) throws AonCoreException;

	// ACCOUNT OPERATING STATEMENT
	void getAccountOperatingReport(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<AccountOperatingReport> callback) throws AonCoreException;

	// ACCOUNT TRIAL BALANCE
	void getAccountTrialBalanceReport(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<AccountTrialBalanceReport> callback) throws AonCoreException;

	// ACCOUNT BALANCE
	void getAccountBalanceReport(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<AccountBalanceReport> callback) throws AonCoreException;

}
