package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InvoiceServiceAsyncDecorator implements InvoiceServiceAsync {

	private InvoiceServiceAsync serviceAsync;

	public InvoiceServiceAsyncDecorator(InvoiceServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// 										[CONFIGURATION]
	@Override
	public void getAonConfiguration(Occam occam, AsyncCallback<AonConfiguration> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAonConfiguration(occam, new AsyncCallbackWrapper<>(callback));
	}

	//										[INVOICE]
	@Override
	public void save(Occam occam, Invoice invoice, AsyncCallback<Invoice> callback) throws AonCoreException {
		AON.start();
		serviceAsync.save(occam, invoice, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void delete(Occam occam, Integer invoiceId, AsyncCallback<Invoice> callback) throws AonCoreException {
		AON.start();
		serviceAsync.delete(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
	
	//										[COMMUNICATION
	@Override
	public void communicationHistory(Occam occam, Integer invoiceId, AsyncCallback<HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.communicationHistory(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
	
}
