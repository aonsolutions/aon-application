package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AccountingUtilitiesServiceAsyncDecorator implements AccountingUtilitiesServiceAsync {

	private AccountingUtilitiesServiceAsync fsa;

	public AccountingUtilitiesServiceAsyncDecorator(AccountingUtilitiesServiceAsync serviceAsync) throws AonCoreException{
		this.fsa = serviceAsync;
	}

	@Override
	public void getDomain(String domainName, String user, int domain, AsyncCallback<Domain> callback) throws AonCoreException{
		AON.start();
		fsa.getDomain(domainName, user, domain, new AsyncCallbackWrapper<Domain>(callback));
	}

	@Override
	public void getChildDomains(String domainName, String user, int domain,AsyncCallback<LinkedList<Domain>> callback) throws AonCoreException{
		AON.start();
		fsa.getChildDomains(domainName, user, domain, new AsyncCallbackWrapper<LinkedList<Domain>>(callback));
	}

	@Override
	public void checkParentLinker(String domainName, String user, Domain domain, Account account,
			AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException {
		AON.start();
		fsa.checkParentLinker(domainName, user, domain, account, new AsyncCallbackWrapper<AccUtilitiesResult>(callback));
	}

	@Override
	public void runParentLinker(String domainName, String user, Domain domain, Account account,
			AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException {
		AON.start();
		fsa.runParentLinker(domainName, user, domain, account, new AsyncCallbackWrapper<AccUtilitiesResult>(callback));
	}

	// Chequeo de cuentas contables sin niveles inferiores.
	@Override
	public void noLowLevelAccounts(String domainName, String user, Domain domain,
			AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException {
		AON.start();
		fsa.noLowLevelAccounts(domainName, user, domain, new AsyncCallbackWrapper<AccUtilitiesResult>(callback));
	}

	@Override
	public void emptyEntries(String domainName, String user, Domain domain,
			AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException {
		AON.start();
		fsa.emptyEntries(domainName, user, domain, new AsyncCallbackWrapper<AccUtilitiesResult>(callback));
	}

	@Override
	public void unbalancedEntries(String domainName, String user, Domain domain,
			AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException {
		AON.start();
		fsa.unbalancedEntries(domainName, user, domain, new AsyncCallbackWrapper<AccUtilitiesResult>(callback));
	}

	@Override
	public void getAccountLinks(String domainName, String user, Integer domain, AccUtilitiesParams params, AsyncCallback<AccUtilitiesResult> callback) {
		AON.start();
		fsa.getAccountLinks(domainName, user, domain, params, new AsyncCallbackWrapper<AccUtilitiesResult>(callback));
	}

	@Override
	public void changeAccountDescription(String domainName, String user, Integer domain, Integer accountId,
			String newDescription, AsyncCallback<String> callback) throws AonCoreException {
		AON.start();
		fsa.changeAccountDescription(domainName, user, domain, accountId, newDescription, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void createAndLinkAccount(String domainName, String user, Integer domain, AccountingRegistryType registryType,
			Integer registryId, AsyncCallback<Account> callback) throws AonCoreException {
		AON.start();
		fsa.createAndLinkAccount(domainName, user, domain, registryType, registryId, new AsyncCallbackWrapper<Account>(callback));
	}

	// Regeneracion del numero de diario
	@Override
	public void getJournalRegenerationInfo(String domainName, String user, Integer domain, AsyncCallback<AccUtilitiesResult> callback) {
		AON.start();
		fsa.getJournalRegenerationInfo(domainName, user, domain, new AsyncCallbackWrapper<AccUtilitiesResult>(callback));
	}

	@Override
	public void regenerateJournal(String domainName, String user, Integer domain, Integer accuountPeriod, AsyncCallback<AccUtilitiesResult> callback) {
		AON.start();
		fsa.regenerateJournal(domainName, user, domain,accuountPeriod, new AsyncCallbackWrapper<AccUtilitiesResult>(callback));
	}



}
