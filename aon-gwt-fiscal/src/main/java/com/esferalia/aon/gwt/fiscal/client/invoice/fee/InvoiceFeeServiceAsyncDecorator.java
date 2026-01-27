package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InvoiceFeeServiceAsyncDecorator implements InvoiceFeeServiceAsync {

	private InvoiceFeeServiceAsync serviceAsync;

	public InvoiceFeeServiceAsyncDecorator(InvoiceFeeServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	@Override
	public void getAonConfiguration(Occam occam, AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(occam, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// ******************************************** [FEE]
	// **************************************************
	@Override
	public void getFeeYearRange(Occam occam, Integer domainId, AsyncCallback<Pair<Integer, Integer>> callback) {
		AON.start();
		serviceAsync.getFeeYearRange(occam, domainId, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ************************************** [INVOICING]
	// **************************************************
	@Override
	public void getInvoices(Occam occam, FeeBillingParams params, AsyncCallback<InvoiceProcessOutput> callback) {
		AON.start();
		serviceAsync.getInvoices(occam, params, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void saveInvoice(Occam occam, Invoice invoice, AsyncCallback<Invoice> callback) {
		AON.start();
		serviceAsync.saveInvoice(occam, invoice, new AsyncCallbackWrapper<>(callback));
	}
				
			
}
