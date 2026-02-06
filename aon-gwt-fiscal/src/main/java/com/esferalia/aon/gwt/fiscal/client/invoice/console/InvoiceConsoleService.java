package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/InvoiceConsole")
public interface InvoiceConsoleService extends RemoteService {

	LinkedList<InvoiceConsole> getInvoices(Occam occam, InvoiceConsoleParams params) throws AonCoreException;
	Invoice getInvoice(Occam occam, Integer domain, Integer invoiceId) throws AonCoreException;	
	InvoiceConsoleAnalysis analyze(Occam occam, InvoiceConsoleParams params) throws AonCoreException;
	AccountEntry record(Occam occam, Invoice inv) throws AonCoreException;
}
