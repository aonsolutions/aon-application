package com.esferalia.aon.gwt.common.client;


import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommonServiceAsync {

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
	void getAccount(String domainName, int domain, String code,
			AsyncCallback<Account> callback);

	void getAccounts(String domainName, int domain, String query,
			AsyncCallback<LinkedList<Account>> callback);

	// -------------------------------------------------------------- CREDITOR
	void getBasicCreditors(String domainName, int domain, String query, 
			AsyncCallback<LinkedList<Creditor>> callback);


	
}
