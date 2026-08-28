package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InvoiceConsoleServiceAsync {

	void getInvoices(Occam occam, InvoiceConsoleParams params, AsyncCallback<LinkedList<InvoiceConsole>> callback);
	void getInvoice(Occam occam, Integer domain, Integer invoiceId, AsyncCallback<Invoice> callback);
	void getAccountingInvoice(Occam occam, Integer domain, Integer invoiceId, AsyncCallback<AccountingInvoice> callback);
	void analyze(Occam occam, InvoiceConsoleParams params, AsyncCallback<InvoiceConsoleAnalysis> callback);
}
