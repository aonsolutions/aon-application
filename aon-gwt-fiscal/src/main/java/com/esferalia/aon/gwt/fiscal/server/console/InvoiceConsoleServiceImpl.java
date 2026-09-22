package com.esferalia.aon.gwt.fiscal.server.console;


import java.util.LinkedList;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleService;
import com.esferalia.aon.occam.api.INVOICECONSOLE;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Aon MS Invoice Console Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/InvoiceConsole" })
public class InvoiceConsoleServiceImpl extends AonStatelessRemoteServiceServlet implements InvoiceConsoleService {

	private static final long serialVersionUID = -1495086199296794184L;

	@Override
	public LinkedList<InvoiceConsole> getInvoices(Occam occam, InvoiceConsoleParams params) throws AonCoreException {
		return new LinkedList<>( INVOICECONSOLE.getInvoiceConsoles( occam, params) );
	}

	@Override
	public Invoice getInvoice(Occam occam, Integer domain, Integer invoiceId) throws AonCoreException {
		return INVOICECONSOLE
			.getInvoice(occam, domain,   invoiceId);
	}
	
	@Override
	public AccountingInvoice getAccountingInvoice(Occam occam, Integer domain, Integer invoiceId) throws AonCoreException {
		return INVOICECONSOLE.getOrInitializeAccountingInvoiceFromInvoice(occam, domain, invoiceId);
	}
	
	@Override
	public InvoiceConsoleAnalysis analyze(Occam occam, InvoiceConsoleParams params) throws AonCoreException {
		return INVOICECONSOLE.analyze(occam, params);
	}

}
