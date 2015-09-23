package com.esferalia.aon.gwt.common.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "Common Servlet", urlPatterns = { "/aon_gwt_fiscal/Common " })
public class CommonServiceImpl extends AonRemoteServiceServlet implements CommonService {

	@Override
	public ArrayList<Enterprise> getParentEnterprises(String domainName, int domain,
			String query) throws AonSQLException {
		return AON.getParentEnterprises(domainName, domain, query);		
	}

	@Override
	public Enterprise getEnterprise(String domainName, int domain, int id)
			throws AonSQLException {
		return AON.getEnterprise(domainName, domain, id);
	}

	@Override
	public ArrayList<CompanyBank> getCompanyBanks(String domainName,
			int domain, int enterprise) throws AonSQLException {
		return AON.getCompanyBanks(domainName, domain, enterprise);
	}

	@Override
	public ArrayList<CompanyBank> getCompanyBanks(String domainName,
			int domain) throws AonSQLException {
		return AON.getCompanyBanks(domainName, domain);
	}

	@Override
	public LinkedList<Account> getAccounts(String domainName, int domain,
			String query) throws AonSQLException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
		 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
			:(query);
		LinkedList<Account> list = new LinkedList<Account>();
		list.addAll( AON.getAccounts(domainName, domain,
				p ->  p.getActiveProperty().eq((byte) 1)
					.and(p.getCodeProperty().like(q)
					 .or(p.getDescriptionProperty().like(q)))
				).collect(Collectors.toList()));
		return list;
	}

	@Override
	public Account getAccount(String domainName, int domain,String code) throws AonSQLException {
		return AON.getAccount(domainName, domain, code);
	}

}
