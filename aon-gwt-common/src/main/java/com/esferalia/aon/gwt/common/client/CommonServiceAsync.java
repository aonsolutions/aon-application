package com.esferalia.aon.gwt.common.client;


import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommonServiceAsync {

	// --------------------------------------------------------- CONFIGURATION
	void getAonConfiguration(String currentDomainName, int currentDomain,
			AsyncCallback<AonConfiguration> asyncCallback);
	void getAonConfiguration(String currentDomainName, int currentDomain, Date atDate,
			AsyncCallback<AonConfiguration> callback);

	// -------------------------------------------------------------- SECURITY
	void getCurrentUser(String domainName, int domain,
			AsyncCallback<User> callback);

	// ---------------------------------- ENTERPRISE
	void getParentEnterprises(String domainName, int domain, String query,
			AsyncCallback<LinkedList<Enterprise>> callback);

	void getEnterprise(String domainName, int domain, int id,
			AsyncCallback<Enterprise> callback);

	void getCompanyBanks(String domainName, int domain, int enterprise,
			AsyncCallback<LinkedList<CompanyBank>> callback);

	void getCompanyBanks(String domainName, int domain,
			AsyncCallback<LinkedList<CompanyBank>> callback);

	// ---------------------------------- ACCOUNT
	void getAccount(String domainName, int domain, String code,AsyncCallback<Account> callback);
	void getAccount(String domainName, int domain, Integer id,AsyncCallback<Account> callback);
	void getAccounts(String domainName, int domain, String query,AsyncCallback<LinkedList<Account>> callback);
	void save(String domainName, int domain, Account account,AsyncCallback<Account> callback);
	void getAccountNextCode(String domainName, int domain, String prefix, AsyncCallback<String> asyncCallback);

	// -------------------------------------------------------------- CREDITOR
	void getBasicCreditors(String domainName, int domain, String query, 
			AsyncCallback<LinkedList<Creditor>> callback);

	// -------------------------------------------------------------- REGISTRY
	void getAccountingRegistries(String domainName, int domain, Integer id,
			AsyncCallback<LinkedList<AccountingRegistry>> callback);
	void getAccountingRegistries(String domainName, int domain, String query,
			AsyncCallback<LinkedList<AccountingRegistry>> asyncCallback);
	void insert(String domainName, int domain, AccountingRegistry reg, AsyncCallback<AccountingRegistry> callback);
	



	
}
