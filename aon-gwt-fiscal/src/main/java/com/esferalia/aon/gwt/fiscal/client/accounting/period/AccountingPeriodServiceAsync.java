package com.esferalia.aon.gwt.fiscal.client.accounting.period;


import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccountingPeriodServiceAsync {

	void getPeriods(Occam occam, AsyncCallback<LinkedList<AccountPeriod>> callback);
	void save(Occam occam, AccountPeriod accountPeriod, AsyncCallback<AccountPeriod> callback);
	void delete(Occam occam, AccountPeriod accountPeriod, AsyncCallback<Void> callback);

}
