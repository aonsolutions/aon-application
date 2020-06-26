package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AnalyticAccounting")
public interface AnalyticAccountingService extends RemoteService {

	AccountingAnalyticalReport saveConfiguration(String domainName, String user, int domain, AccountingReportParams params, Analytical analytical);
	AccountingAnalyticalReport getAccountAnalyticalReport(String domainName,String user,int domain, AccountingReportParams params) throws AonCoreException;	
	
}
