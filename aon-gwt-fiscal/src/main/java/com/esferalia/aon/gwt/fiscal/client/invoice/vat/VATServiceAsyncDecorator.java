package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class VATServiceAsyncDecorator implements VATServiceAsync {

	private VATServiceAsync fsa;

	public VATServiceAsyncDecorator(VATServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getAonConfiguration(Occam occam,AsyncCallback<AonConfiguration> callback) {
		AON.start();
		fsa.getAonConfiguration(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getVatContext(Occam occam, AccountingReportParams params, AsyncCallback<LinkedList<VatContext>> callback) {
		AON.start();
		fsa.getVatContext(occam, params, new AsyncCallbackWrapper<>(callback));
	}
	
//	@Override
//	public void getVatContextReport(Occam occam, AccountingReportParams params, AsyncCallback<String> callback) {
//		AON.start();
//		fsa.getVatContextReport(occam, params, new AsyncCallbackWrapper<>(callback));
//		
//	}
	
	@Override
	public void getVatSummaryContext(Occam occam, AccountingReportParams params, AsyncCallback<LinkedList<VatSummaryContext>> callback) {
		AON.start();
		fsa.getVatSummaryContext(occam, params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
	
	

}
