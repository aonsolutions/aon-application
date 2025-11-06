package com.esferalia.aon.gwt.fiscal.server.invoice.fee;


import java.util.LinkedList;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.invoice.fee.InvoiceFeeService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.Pair;

import jakarta.servlet.annotation.WebServlet;

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
	// ***************************************** [SEARCH]
	// **************************************************
	@Override
	public LinkedList<Item> getProductsSuggestion(Occam occam, Integer searchDomain, String productQuery) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// **************************************************
	// ************************************** [INVOICING]
	// **************************************************
	@Override
	public LinkedList<Invoice> getInvoices(Occam occam, FeeBillingParams params) {
		return AON.feeInvoicing(occam, params);
	}

}
