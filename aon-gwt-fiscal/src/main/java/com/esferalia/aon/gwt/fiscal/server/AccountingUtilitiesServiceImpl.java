package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.AccountingUtilitiesService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Accounting Utilities Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/AccountingUtilities" })
public class AccountingUtilitiesServiceImpl extends AonRemoteServiceServlet implements AccountingUtilitiesService {

	private static final long serialVersionUID = -3045020929753519103L;

	@Override
	public Domain getDomain(String domainName, String user, int domain) throws AonCoreException {
		return AON.getDomain(domainName, domain, user);
	}

	@Override
	public LinkedList<Domain> getChildDomains(String domainName, String user, int domain) throws AonCoreException {
		return AON.getDomainList(domainName, domain, user, p -> p.getParentProperty().eq(domain));
	}

	@Override
	public AccUtilitiesResult checkParentLinker(String domainName, String user, Domain domain, Account account)
			throws AonCoreException {
		return ACCOUNTING.checkParentLinker(domainName, user, domain, account);
	}

	@Override
	public AccUtilitiesResult runParentLinker(String domainName, String user, Domain domain, Account account)
			throws AonCoreException {
		return ACCOUNTING.runParentLinker(domainName, user, domain, account);
	}

	@Override
	public AccUtilitiesResult noLowLevelAccounts(String domainName, String user, Domain domain) {
		return ACCOUNTING.noLowLevelAccounts(domainName, user, domain);
	}

	@Override
	public AccUtilitiesResult emptyEntries(String domainName, String user, Domain domain) {
		return ACCOUNTING.emptyEntries(domainName, user, domain);
	}

	@Override
	public AccUtilitiesResult unbalancedEntries(String domainName, String user, Domain domain) {
		return ACCOUNTING.unbalancedEntries(domainName, user, domain);
	}

	@Override
	public AccUtilitiesResult getAccountLinks(String domainName, String user, Integer domain, AccUtilitiesParams params) {
		return ACCOUNTING.getAccountLinks(domainName, user, domain, params);
	}

	@Override
	public String changeAccountDescription(String domainName, String user, Integer domain, Integer accountId,
			String newDescription) throws AonCoreException {
		return ACCOUNTING.changeAccountDescription(domainName, user, domain, accountId, newDescription);
	}

	@Override
	public Account createAndLinkAccount(String domainName, String user, Integer domain,
			AccountingRegistryType registryType, Integer registryId) throws AonCoreException {
		return ACCOUNTING.createAndLinkAccount(domainName, user, domain, registryType, registryId);
	}

	@Override
	public AccUtilitiesResult getJournalRegenerationInfo(String domainName, String user, Integer domain) throws AonCoreException {
		return ACCOUNTING.getJournalRegenerationInfo(domainName,user,domain);
	}

	@Override
	public AccUtilitiesResult regenerateJournal(String domainName, String user, Integer domain, Integer accuountPeriod) throws AonCoreException {
		return ACCOUNTING.regenerateJournal(domainName,user,domain,accuountPeriod);
	}

}
