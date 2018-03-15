package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
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


}
