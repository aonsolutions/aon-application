package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VATServiceAsync {

	// --------------------------------------------------------- CONFIGURATION
	void getAonConfiguration(String domainName, String user, int domain, AsyncCallback<AonConfiguration> callback);

	// --------------------------------------------------------------- VAT
	void getVatSummaryContext(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<LinkedList<VatSummaryContext>> callback);
	void getVatContext(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<LinkedList<VatContext>> callback);
	void getVatContextReport(String domainName, String user, int domain, AccountingReportParams params, AsyncCallback<String> callback);

}
