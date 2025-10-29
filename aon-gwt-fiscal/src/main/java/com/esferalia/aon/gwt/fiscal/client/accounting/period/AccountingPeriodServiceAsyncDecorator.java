package com.esferalia.aon.gwt.fiscal.client.accounting.period;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AccountingPeriodServiceAsyncDecorator implements AccountingPeriodServiceAsync {

	private AccountingPeriodServiceAsync serviceAsync;

	public AccountingPeriodServiceAsyncDecorator(AccountingPeriodServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}


	@Override
	public void getPeriods(Occam occam, AsyncCallback<LinkedList<AccountPeriod>> callback) {
		AON.start();
		serviceAsync.getPeriods(occam, new AsyncCallbackWrapper<LinkedList<AccountPeriod>>(callback));
	}
	@Override
	public void save(Occam occam, AccountPeriod accountPeriod, AsyncCallback<AccountPeriod> callback) {
		AON.start();
		serviceAsync.save(occam, accountPeriod, new AsyncCallbackWrapper<AccountPeriod>(callback));
	}
	@Override
	public void delete(Occam occam, AccountPeriod accountPeriod, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.delete(occam, accountPeriod, new AsyncCallbackWrapper<Void>(callback));
	}
}
