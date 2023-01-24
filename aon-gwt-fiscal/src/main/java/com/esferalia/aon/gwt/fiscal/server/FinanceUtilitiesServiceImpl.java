package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.finance.utilities.FinanceUtilitiesService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FINANCE;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Finance Utilities Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/FinanceUtilities" })
public class FinanceUtilitiesServiceImpl extends AonStatelessRemoteServiceServlet implements FinanceUtilitiesService {

	private static final long serialVersionUID = 1L;

	@Override
	public Domain getDomain(Occam occam) throws AonCoreException {
		return AON.getDomain(occam, occam.getDomain());
	}

	@Override
	public LinkedList<Domain> getChildDomains(Occam occam) throws AonCoreException {
		return AON.getDomainList(occam, p -> p.getParentProperty().eq(occam.getDomain()));
	}

	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}
	
	// Chequeo de facturas sin vencimientos
	@Override
	public FinanceUtilitiesResult missingFinanceInvoices(Occam occam, Domain domain,FinanceUtilitiesParams params) throws AonCoreException {
		return FINANCE.missingFinanceInvoices(occam, domain,params);
	}
	@Override
	public Invoice missingFinanceInvoicesFix(Occam occam, Integer invoice) throws AonCoreException {
		return FINANCE.missingFinanceInvoicesFix(occam,invoice);
	}
	
	// Chequeo de integridad de vencimientos en facturas 
	@Override
	public FinanceUtilitiesResult financeInvoiceIntegrity(Occam occam, Domain domain) throws AonCoreException {
		return FINANCE.financeInvoiceIntegrity(occam, domain);
	}
	@Override
	public Finance financeInvoiceIntegrityFix(Occam occam, Finance finance) throws AonCoreException {
		return FINANCE.financeInvoiceIntegrityFix(occam, finance);
	}

	// Chequeo de tipos de retenci\u00F3n en facturas.
	@Override
	public void updateWithholdingType(Occam occam, Integer invoiceId, WithholdingType newType) throws AonCoreException {
		FINANCE.updateWithholdingType(occam, invoiceId, newType);
		
	}
}
