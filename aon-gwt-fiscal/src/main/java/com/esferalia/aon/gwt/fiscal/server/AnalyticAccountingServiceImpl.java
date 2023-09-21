package com.esferalia.aon.gwt.fiscal.server;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.AnalyticAccountingService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Analytic Accounting Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/AnalyticAccounting" })
public class AnalyticAccountingServiceImpl extends AonStatelessRemoteServiceServlet implements AnalyticAccountingService {

	private static final long serialVersionUID = 1359271172007862242L;

	@Override
	public AccountingAnalyticalReport saveConfiguration(String domainName, String user, int domain, AccountingReportParams params, Analytical analytical)
			throws AonCoreException {
		return ACCOUNTING.saveAnalyticConfiguration(domainName, user, domain,params,analytical);
	}
	@Override
	public AccountingAnalyticalReport getAccountAnalyticalReport(String domainName, String user, int domain,
			AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountAnalyticalReport(domainName, user, domain,params);
	}

}
