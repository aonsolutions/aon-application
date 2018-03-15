package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AccountingUtilities")
public interface AccountingUtilitiesService extends RemoteService {
	
	Domain getDomain(String domainName,String user, int domain) throws AonCoreException;
	LinkedList<Domain> getChildDomains(String domainName,String user, int domain) throws AonCoreException;
	AccUtilitiesResult checkParentLinker(String domainName, String user, Domain domain, Account account) throws AonCoreException;
	AccUtilitiesResult runParentLinker(String domainName, String user, Domain domain, Account account) throws AonCoreException;
	AccUtilitiesResult emptyEntries(String domainName, String user, Domain domain);
	AccUtilitiesResult unbalancedEntries(String domainName, String user, Domain domain);

}
