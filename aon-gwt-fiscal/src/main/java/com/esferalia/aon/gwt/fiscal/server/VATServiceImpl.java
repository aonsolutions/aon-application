package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.VATService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "VAT Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/VAT" })
public class VATServiceImpl extends AonStatelessRemoteServiceServlet implements VATService {

	private static final long serialVersionUID = 7560328798889381364L;

	@Override
	public AonConfiguration getAonConfiguration(Occam occam) {
		return AON.getConfiguration(occam);
	}

	@Override
	public LinkedList<VatContext> getVatContext(Occam occam, AccountingReportParams params) throws AonCoreException {
		return FISCAL.getVatContext(occam, params)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Override
	public LinkedList<VatSummaryContext> getVatSummaryContext(Occam occam, AccountingReportParams params) throws AonCoreException {
		return FISCAL.getVatSummaryContext(occam, params)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}
	
}
