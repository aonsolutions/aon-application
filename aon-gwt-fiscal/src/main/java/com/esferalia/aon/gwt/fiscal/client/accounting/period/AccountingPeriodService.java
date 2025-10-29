package com.esferalia.aon.gwt.fiscal.client.accounting.period;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AccountingPeriod")
public interface AccountingPeriodService extends RemoteService {

	LinkedList<AccountPeriod> getPeriods(Occam occam) throws AonCoreException;
	AccountPeriod save(Occam occam, AccountPeriod accountPeriod) throws AonCoreException;
	void delete(Occam occam, AccountPeriod accountPeriod) throws AonCoreException;

	
}
