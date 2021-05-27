package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AccountingReportServiceAsyncDecorator implements AccountingReportServiceAsync {

	private AccountingReportServiceAsync fsa;

	public AccountingReportServiceAsyncDecorator(AccountingReportServiceAsync serviceAsync) {
		this.fsa = serviceAsync;
	}

	// ACCOUNT PERIOD
	@Override
	public void getDomainPeriods(String domainName, int domain, String user, AsyncCallback<LinkedList<AccountPeriod>> callback) {
		AON.start();
		fsa.getDomainPeriods(domainName, domain, user, new AsyncCallbackWrapper<LinkedList<AccountPeriod>>(callback));
	}

	// ACCOUNT STATEMENT
	@Override
	public void getAccountStatement(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<AccountStatementReport> callback) {
		AON.start();
		fsa.getAccountStatement(domainName, user, domain, params, new AsyncCallbackWrapper<AccountStatementReport>(callback));
	}

	// ACCOUNT TRIAL BALANCE
	@Override
	public void getAccountTrialBalanceReport(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<AccountTrialBalanceReport> callback) {
		AON.start();
		fsa.getAccountTrialBalanceReport(domainName, user, domain, params, new AsyncCallbackWrapper<AccountTrialBalanceReport>(callback));
	}

	// ACCOUNT TRIAL BALANCE
	@Override
	public void getAccountBalanceReport(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<AccountBalanceReport> callback) {
		AON.start();
		fsa.getAccountBalanceReport(domainName, user, domain, params, new AsyncCallbackWrapper<AccountBalanceReport>(callback));
	}

	@Override
	public void getAccountBalance(String domainName, int domain, String user, AccountingReportParams params, AsyncCallback<LinkedList<AccountStatement>> callback) {
		AON.start();
		fsa.getAccountBalance(domainName, domain, user, params, new AsyncCallbackWrapper<LinkedList<AccountStatement>>(callback));
	}

	@Override
	public void getAccountOperatingReport(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<AccountOperatingReport> callback) {
		AON.start();
		fsa.getAccountOperatingReport(domainName, user, domain, params, new AsyncCallbackWrapper<AccountOperatingReport>(callback));

	}

	// --------------------------------------------------------------- IRPF
	@Override
	public void getIrpfBreakdownSummary(String domainName, String user, int domain, IRPFParams params, AsyncCallback<LinkedList<IrpfBreakdown>> callback) {
		AON.start();
		fsa.getIrpfBreakdownSummary(domainName, user, domain, params, new AsyncCallbackWrapper<LinkedList<IrpfBreakdown>>(callback));
	}

	@Override
	public void getIrpfBreakdown(String domainName, String user, int domain, IRPFParams params, AsyncCallback<LinkedList<IrpfBreakdown>> callback) {
		AON.start();
		fsa.getIrpfBreakdown(domainName, user, domain, params, new AsyncCallbackWrapper<LinkedList<IrpfBreakdown>>(callback));
	}
}
