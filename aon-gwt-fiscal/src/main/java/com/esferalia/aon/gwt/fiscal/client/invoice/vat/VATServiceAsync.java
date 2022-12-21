package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VATServiceAsync {

	void getAonConfiguration(Occam occam, AsyncCallback<AonConfiguration> callback);
	void getVatSummaryContext(Occam occam, AccountingReportParams params, AsyncCallback<LinkedList<VatSummaryContext>> callback);
	void getVatContext(Occam occam, AccountingReportParams params, AsyncCallback<LinkedList<VatContext>> callback);
//	void getVatContextReport(Occam occam, AccountingReportParams params, AsyncCallback<String> callback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback);

}
