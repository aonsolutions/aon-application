package com.esferalia.aon.gwt.common.server;

import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;

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


}
