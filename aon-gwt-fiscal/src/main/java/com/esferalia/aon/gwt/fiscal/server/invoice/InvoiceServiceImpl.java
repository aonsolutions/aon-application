package com.esferalia.aon.gwt.fiscal.server.invoice;


import java.util.HashMap;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.INVOICE;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;

@WebServlet(name = "Aon MS Invoice Service Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/AonInvoiceService" })
public class InvoiceServiceImpl extends AonStatelessRemoteServiceServlet implements InvoiceService {

	private static final long serialVersionUID = -1495086199296794184L;

	// 										[CONFIGURATION]
	@Override
	public AonConfiguration getAonConfiguration(Occam occam) throws AonCoreException {
		return AON.getConfiguration(occam);
	}

	//										[INVOICE]
	@Override
	public Invoice save(Occam occam, Invoice invoice) throws AonCoreException {
		return INVOICE.save(occam, invoice);
	}
	@Override
	public Invoice delete(Occam occam, Integer invoiceId) throws AonCoreException {
		return INVOICE.delete(occam, invoiceId);
	}
	// 										[COMMUNICATION]
	@Override
	public HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> communicationHistory(Occam occam, Integer invoiceId) throws AonCoreException {
		return InvoiceCommunicator.history(occam, invoiceId);
	}
}
