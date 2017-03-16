package com.esferalia.aon.gwt.common.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Common")
public interface CommonService extends RemoteService {

	// --------------------------------------------------------- CONFIGURATION
	AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain) throws AonCoreException;
	AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain, Date atDate) throws AonCoreException;
	
	// -------------------------------------------------------------- SECURITY
	User getCurrentUser(String domainName, int domain) throws AonCoreException;
	
	// -------------------------------------------------------------- ENTERPRISE
	LinkedList<Enterprise> getParentEnterprises(String domainName, int domain,
			String query) throws AonCoreException;
	Enterprise getEnterprise(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain,int enterprise) throws AonCoreException;
	
	// -------------------------------------------------------------- ACCOUNT
	Account getAccount(String domainName,int domain,Integer id) throws AonCoreException;
	Account getAccount(String domainName,int domain,String code) throws AonCoreException;
	LinkedList<Account> getAccounts(String domainName,int domain,String query) throws AonCoreException;
	Account save(String domainName, int domain, Account account) throws AonCoreException;
	String getAccountNextCode(String domainName, int domain, String prefix);
	
	// -------------------------------------------------------------- CREDITOR
	LinkedList<Creditor> getBasicCreditors(String domainName,int domain,String query) throws AonCoreException;
	
	// -------------------------------------------------------------- REGISTRY
	LinkedList<AccountingRegistry> getAccountingRegistries(String domainName,int domain,Integer id) throws AonCoreException;
	LinkedList<AccountingRegistry> getAccountingRegistries(String domainName,int domain,String query) throws AonCoreException;
	LinkedList<AccountingRegistry> getAccountingRegistries(String domainName,int domain,AccountingRegistryParams params) throws AonCoreException;
	AccountingRegistry insert(String domainName,int domain,AccountingRegistry reg) throws AonCoreException;
	

	

}
