package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Accounting Report Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/AccountingReport" })
public class AccountingReportServiceImpl extends AonStatelessRemoteServiceServlet implements AccountingReportService {

	private static final long serialVersionUID = -3171037725671037932L;

	@Override
	public LinkedList<AccountPeriod> getDomainPeriods(String domainName, int domain, String user) throws AonCoreException {
		return ACCOUNTING.getDomainPeriods(domainName, domain, user);
	}
	
	@Override
	public AccountStatementReport getAccountStatement(String domainName, String user,
			int domain, AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountStatement(domainName,domain,user,params);
	}
	
	@Override
	public AccountTrialBalanceReport getAccountTrialBalanceReport(String domainName, String user, int domain,
			AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountTrialBalance(domainName,domain,user,params);
	}
	
	@Override
	public AccountBalanceReport getAccountBalanceReport(String domainName, String user, int domain,
			AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountBalanceReport(domainName,domain,user,params);
	}

	@Override
	public LinkedList<AccountStatement> getAccountBalance(String domainName,
			int domain, String user, AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountBalance(domainName,domain, user,params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Override
	public AccountOperatingReport getAccountOperatingReport(String domainName, String user, int domain,
			AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountOperatingReport(domainName, user, domain,params);
	}

	
	// --------------------------------------------------------------- IRPF
	@Override
	public LinkedList<IrpfBreakdown> getIrpfBreakdownSummary(String domainName, String user, int domain,
			IRPFParams params) throws AonCoreException {
		return FISCAL.getIrpfBreakdownSummary(domainName, user, domain, params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	@Override
	public LinkedList<IrpfBreakdown> getIrpfBreakdown(String domainName, String user, int domain,
			IRPFParams params) throws AonCoreException {
		return FISCAL.getIrpfBreakdown(domainName, user, domain, params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
}
