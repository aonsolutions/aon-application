package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.finance.utilities.FinanceUtilitiesService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Finance Utilities Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/FinanceUtilities" })
public class FinanceUtilitiesServiceImpl extends AonRemoteServiceServlet implements FinanceUtilitiesService {

	private static final long serialVersionUID = 1L;

	@Override
	public Domain getDomain(String domainName, String user, int domain) throws AonCoreException {
		return AON.getDomain(domainName, domain, user);
	}

	@Override
	public LinkedList<Domain> getChildDomains(String domainName, String user, int domain) throws AonCoreException {
		return AON.getDomainList(domainName, domain, user, p -> p.getParentProperty().eq(domain));
	}

	@Override
	public FinanceUtilitiesResult missingFinanceInvoices(String domainName, String user, Domain domain,FinanceUtilitiesParams params)
			throws AonCoreException {
		return AON.missingFinanceInvoices(domainName, user, domain.getId(),params);
	}

	@Override
	public Invoice missingFinanceInvoicesFix(String domainName, String user, Integer domain,
			Integer invoice) throws AonCoreException {
		return AON.missingFinanceInvoicesFix(domainName, user, domain,invoice);
	}
	@Override
	public FinanceUtilitiesResult financeInvoiceIntegrity(String domainName, String user, Domain domain)
			throws AonCoreException {
		return AON.financeInvoiceIntegrity(domainName, user, domain);
	}
	@Override
	public Finance financeInvoiceIntegrityFix(String domainName, String user, Integer domain, Finance finance)
			throws AonCoreException {
		return AON.financeInvoiceIntegrityFix(domainName, user, domain, finance);
	}
}
