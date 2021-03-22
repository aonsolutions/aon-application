package com.esferalia.aon.gwt.fiscal.client.accounting.period;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AccountingPeriod")
public interface AccountingPeriodService extends RemoteService {

	LinkedList<AccountPeriod> getPeriods(String domainName, int domain, String user) throws AonCoreException;
	AccountPeriod save(String domainName,int domain, String user, AccountPeriod accountPeriod) throws AonCoreException;
	void delete(String domainName,int domain, String user, AccountPeriod accountPeriod) throws AonCoreException;

	
}
