package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Aon MS Fiscal Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Fiscal" })
public class FiscalMSServiceServiceImpl extends AonStatelessRemoteServiceServlet implements FiscalMSService {

	private static final long serialVersionUID = 4908377540728384390L;

	// -------------------------------------------------------------- COMPANY
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName, String user, int domain) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain, user);
	}

	// -------------------------------------------------------------- CREDITOR
	@Override
	public LinkedList<Creditor> getBasicCreditors(String domainName, String user, int domain, String query) throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getBasicCreditors(domainName, domain,user,
				p ->  p.getActiveProperty().eq( RegistryStatus.ACTIVE.value())
					.and(p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				).collect(Collectors.toCollection(LinkedList::new));
	}
}
