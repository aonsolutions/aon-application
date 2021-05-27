package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AnalyticAccountingServiceAsyncDecorator implements AnalyticAccountingServiceAsync {

	private AnalyticAccountingServiceAsync fsa;

	public AnalyticAccountingServiceAsyncDecorator(AnalyticAccountingServiceAsync serviceAsync) {
		this.fsa = serviceAsync;
	}
	
	@Override
	public void saveConfiguration(String domainName, String user, int domain, AccountingReportParams params,Analytical analytical,
			AsyncCallback<AccountingAnalyticalReport> callback) {
		AON.start();
		fsa.saveConfiguration(domainName, user, domain, params, analytical,new AsyncCallbackWrapper<AccountingAnalyticalReport>(callback));
	}

	@Override
	public void getAccountAnalyticalReport(String domainName, String user, int domain, AccountingReportParams params,
			AsyncCallback<AccountingAnalyticalReport> callback) {
		AON.start();
		fsa.getAccountAnalyticalReport(
				domainName,
				user,
				domain,
				params,
				new AsyncCallbackWrapper<AccountingAnalyticalReport>(callback));
	}

}
