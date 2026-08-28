package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InvoiceConsoleAsyncDecorator implements InvoiceConsoleServiceAsync {

	private InvoiceConsoleServiceAsync serviceAsync;

	public InvoiceConsoleAsyncDecorator(InvoiceConsoleServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	@Override
	public void getInvoices(Occam occam, InvoiceConsoleParams params, AsyncCallback<LinkedList<InvoiceConsole>> callback) {
		AON.start();
		serviceAsync.getInvoices(occam, params, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInvoice(Occam occam, Integer domain, Integer invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		serviceAsync.getInvoice(occam, domain, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getAccountingInvoice(Occam occam, Integer domain, Integer invoiceId, AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		serviceAsync.getAccountingInvoice(occam, domain, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void analyze(Occam occam, InvoiceConsoleParams params, AsyncCallback<InvoiceConsoleAnalysis> callback) {
		AON.start();
		serviceAsync.analyze(occam, params, new AsyncCallbackWrapper<>(callback));
	}

}
