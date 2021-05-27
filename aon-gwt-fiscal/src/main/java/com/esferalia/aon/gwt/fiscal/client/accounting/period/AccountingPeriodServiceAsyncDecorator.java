package com.esferalia.aon.gwt.fiscal.client.accounting.period;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AccountingPeriodServiceAsyncDecorator implements AccountingPeriodServiceAsync {

	private AccountingPeriodServiceAsync serviceAsync;

	public AccountingPeriodServiceAsyncDecorator(AccountingPeriodServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}


	@Override
	public void getPeriods(String domainName, int domain, String user, AsyncCallback<LinkedList<AccountPeriod>> callback) {
		AON.start();
		serviceAsync.getPeriods(domainName, domain, user, new AsyncCallbackWrapper<LinkedList<AccountPeriod>>(callback));
	}
	@Override
	public void save(String domainName, int domain, String user, AccountPeriod accountPeriod, AsyncCallback<AccountPeriod> callback) {
		AON.start();
		serviceAsync.save(domainName, domain, user, accountPeriod, new AsyncCallbackWrapper<AccountPeriod>(callback));
	}
	@Override
	public void delete(String domainName, int domain, String user, AccountPeriod accountPeriod, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.delete(domainName, domain, user, accountPeriod, new AsyncCallbackWrapper<Void>(callback));
	}
}
