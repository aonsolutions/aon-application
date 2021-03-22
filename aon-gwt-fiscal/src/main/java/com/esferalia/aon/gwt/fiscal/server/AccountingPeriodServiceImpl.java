package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.accounting.period.AccountingPeriodService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Accounting Period Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/AccountingPeriod" })
public class AccountingPeriodServiceImpl extends AonStatelessRemoteServiceServlet implements AccountingPeriodService {

	private static final long serialVersionUID = 8791955004212947200L;

	@Override
	public LinkedList<AccountPeriod> getPeriods(String domainName, int domain, String user) throws AonCoreException {
		return ACCOUNTING.getDomainPeriods(domainName, domain, user);
	}

	@Override
	public AccountPeriod save(String domainName, int domain, String user, AccountPeriod accountPeriod) throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, user,accountPeriod);
	}

	@Override
	public void delete(String domainName, int domain, String user, AccountPeriod accountPeriod) throws AonCoreException {
		ACCOUNTING.deleteAccountPeriod(domainName, domain, user,accountPeriod);
	}

}
