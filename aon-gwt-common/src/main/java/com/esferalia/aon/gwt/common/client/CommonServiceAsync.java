package com.esferalia.aon.gwt.common.client;


import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommonServiceAsync {

	// --------------------------------------------------------- CONFIGURATION
	void getAonConfiguration(String currentDomainName, int currentDomain, String user,
			AsyncCallback<AonConfiguration> asyncCallback);
	void getAonConfiguration(String currentDomainName, int currentDomain, String user, Date atDate,
			AsyncCallback<AonConfiguration> callback);

	// -------------------------------------------------------------- SECURITY
	void getCurrentUser(String domainName, int domain, String user,
			AsyncCallback<User> callback);

	// ---------------------------------- ENTERPRISE
	void getParentEnterprises(String domainName, int domain, String user, String query,AsyncCallback<LinkedList<Enterprise>> callback);
	void getEnterprise(String domainName, int domain, String user, int id,AsyncCallback<Enterprise> callback);
	void getCompanyBanks(String domainName, int domain, String user, int enterprise,AsyncCallback<LinkedList<CompanyBank>> callback);
	void getCompanyBanks(String domainName, int domain, String user,AsyncCallback<LinkedList<CompanyBank>> callback);

	// ---------------------------------- ACCOUNT
	void getAccount(String domainName, int domain, String user, String code,AsyncCallback<Account> callback);
	void getAccount(String domainName, int domain, String user, Integer id,AsyncCallback<Account> callback);
	void getAccounts(String domainName, int domain, String user, String query,AsyncCallback<LinkedList<Account>> callback);
	void save(String domainName, int domain, String user, Account account,AsyncCallback<Account> callback);
	void delete(String domainName, int domain, String user, Account account,AsyncCallback<Account> callback);
	void getAccountNextCode(String domainName, int domain, String user, String prefix, AsyncCallback<String> asyncCallback);

	// -------------------------------------------------------------- CREDITOR
	void getBasicCreditors(String domainName, int domain, String user, String query, 
			AsyncCallback<LinkedList<Creditor>> callback);

	// -------------------------------------------------------------- 
	void getInvoiceRegistries(String domainName, int domain, String user, String query,
			AsyncCallback<LinkedList<InvoiceRegistry>> asyncCallback);
	void getInvoiceProducts(String domainName, int domain, String user, String query,
			AsyncCallback<LinkedList<Product>> asyncCallback);

}
