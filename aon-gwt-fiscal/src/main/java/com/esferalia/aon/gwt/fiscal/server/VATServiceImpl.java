package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.invoice.VATService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.impl.jooq.dao.VATFormatter;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "VAT Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/VAT" })
public class VATServiceImpl extends AonStatelessRemoteServiceServlet implements VATService {

	private static final long serialVersionUID = 7560328798889381364L;

	@Override
	public AonConfiguration getAonConfiguration(String domainName, String user, int domain) {
		return AON.getConfiguration(domainName, domain,user, null);
	}

	@Override
	public LinkedList<VatContext> getVatContext(String domainName, String user, int domain, AccountingReportParams params) throws AonCoreException {
		return FISCAL.getVatContext(domainName, domain, user, params)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Override
	public String getVatContextReport(String domainName, String user, int domain, AccountingReportParams params) throws AonCoreException {
		return VATFormatter.formatInvoices("LISTADO IVA", FiscalUtils.toString(params), 
			FISCAL.getVatContext(domainName, domain, user, params)
				.collect(Collectors.toCollection(LinkedList::new)));
	}

	@Override
	public LinkedList<VatSummaryContext> getVatSummaryContext(String domainName, String user, int domain, AccountingReportParams params) throws AonCoreException {
		return FISCAL.getVatSummaryContext(domainName, domain, user, params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
}
