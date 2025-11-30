package com.esferalia.aon.gwt.fiscal.server.invoice.fee;


import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.invoice.fee.InvoiceFeeService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.INVOICE;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.Pair;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;

@WebServlet(name = "Aon MS Invoice Fee Console Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/InvoiceFee" })
public class InvoiceFeeServiceImpl extends AonStatelessRemoteServiceServlet implements InvoiceFeeService {

	private static final long serialVersionUID = -1495086199296794184L;

	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	@Override
	public AonConfiguration getAonConfiguration(Occam occam) throws AonCoreException {
		return AON.getConfiguration(occam);
	}

	// **************************************************
	// ******************************************** [FEE]
	// **************************************************
	@Override
	public Pair<Integer, Integer> getFeeYearRange(Occam occam, Integer domainId) {
		return AON.getFeeYearRange(occam, domainId)
			.orElse(null);
	}

	// **************************************************
	// ************************************** [INVOICING]
	// **************************************************
	@Override
	public InvoiceProcessOutput getInvoices(Occam occam, FeeBillingParams params) {
		return InvoiceCommunicator.feeInvoicing(occam, params);
	}

	@Override
	public Invoice saveInvoice(Occam occam, Invoice invoice) {
		return INVOICE.save(occam, invoice);
	}

}
