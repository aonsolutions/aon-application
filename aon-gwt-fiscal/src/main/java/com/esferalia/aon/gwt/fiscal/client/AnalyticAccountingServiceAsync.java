package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalyticAccountingServiceAsync {

	void saveConfiguration(String domainName, String user, int domain, AccountingReportParams params,
			Analytical analytical, AsyncCallback<AccountingAnalyticalReport> asyncCallback);
	
	void getAccountAnalyticalReport(String domainName, String user, int domain
			,AccountingReportParams params
			,AsyncCallback<AccountingAnalyticalReport> callback) throws AonCoreException;

}
