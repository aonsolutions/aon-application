package com.esferalia.aon.gwt.common.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.CreditorStatus;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Common Servlet", urlPatterns = { "/aon_gwt_fiscal/Common " })
public class CommonServiceImpl extends AonRemoteServiceServlet implements CommonService {

	// -------------------------------------------------------------- SECURITY
	@Override
	public User getCurrentUser(String domainName, int domain)
			throws AonSQLException {
		return AON.getUser(domainName,domain,AonServletUtils.getLoggedUser()); 		
	}
	// -------------------------------------------------------------- ENTERPRISE
	@Override
	public LinkedList<Enterprise> getParentEnterprises(String domainName, int domain,
			String query) throws AonSQLException {
		return AON.getParentEnterprises(domainName, domain,AonServletUtils.getLoggedUser(), query);		
	}

	@Override
	public Enterprise getEnterprise(String domainName, int domain, int id)
			throws AonSQLException {
		return AON.getEnterprise(domainName, domain,AonServletUtils.getLoggedUser(), id);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName,
			int domain, int enterprise) throws AonSQLException {
		return AON.getCompanyBanks(domainName, domain,AonServletUtils.getLoggedUser(), enterprise);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName,
			int domain) throws AonSQLException {
		return AON.getCompanyBanks(domainName, domain,AonServletUtils.getLoggedUser());
	}

	@Override
	public LinkedList<Account> getAccounts(String domainName, int domain,
			String query) throws AonSQLException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
		 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
			:(query);
		return AON.getAccounts(domainName, domain,AonServletUtils.getLoggedUser(),
				p ->  p.getActiveProperty().eq((byte) 1)
					.and(p.getCodeProperty().like(q)
					 .or(p.getDescriptionProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				).collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public Account getAccount(String domainName, int domain,String code) throws AonSQLException {
		return AON.getAccount(domainName, domain,AonServletUtils.getLoggedUser(), code);
	}
	
	// -------------------------------------------------------------- CREDITOR
	@Override
	public LinkedList<Creditor> getBasicCreditors(String domainName, int domain, String query) throws AonSQLException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getBasicCreditors(domainName, domain,AonServletUtils.getLoggedUser(),
				p ->  p.getActiveProperty().eq( CreditorStatus.ACTIVE.value())
					.and(p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				).collect(Collectors.toCollection(LinkedList::new));
	}

}
