package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InvoiceConsoleServiceAsync {

	void getInvoices(Occam occam, InvoiceConsoleParams params, AsyncCallback<LinkedList<InvoiceConsole>> callback);
	void getInvoice(Occam occam, Integer domain, Integer invoiceId, AsyncCallback<Invoice> callback);

}
