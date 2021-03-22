package com.esferalia.aon.gwt.fiscal.client.accounting.period;


import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccountingPeriodServiceAsync {

	void getPeriods(String domainName, int domain, String user, AsyncCallback<LinkedList<AccountPeriod>> callback);
	void save(String domainName, int domain, String user, AccountPeriod accountPeriod, AsyncCallback<AccountPeriod> callback);
	void delete(String domainName, int domain, String user, AccountPeriod accountPeriod, AsyncCallback<Void> callback);

}
