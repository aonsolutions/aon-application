package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AccountingUtilities")
public interface AccountingUtilitiesService extends RemoteService {
	
	Domain getDomain(String domainName,String user, int domain) throws AonCoreException;
	LinkedList<Domain> getChildDomains(String domainName,String user, int domain) throws AonCoreException;

	// ParentLinker Module
	AccUtilitiesResult checkParentLinker(String domainName, String user, Domain domain, Account account) throws AonCoreException;
	AccUtilitiesResult runParentLinker(String domainName, String user, Domain domain, Account account) throws AonCoreException;

	// Apuntes sin lineas
	AccUtilitiesResult emptyEntries(String domainName, String user, Domain domain) throws AonCoreException;
	
	// Apuntes descuadrados
	AccUtilitiesResult unbalancedEntries(String domainName, String user, Domain domain) throws AonCoreException;
	
	// Chequeo de cuentas asignadas a otras entidades
	AccUtilitiesResult getAccountLinks(String domainName, String user, Integer domain, AccUtilitiesParams params) throws AonCoreException;
	String changeAccountDescription(String domainName, String user, Integer domain, Integer accountId, String newDescription) throws AonCoreException;
	Account createAndLinkAccount(String domainName, String user, Integer domain, AccountingRegistryType registryType,Integer registryId) throws AonCoreException;

}
