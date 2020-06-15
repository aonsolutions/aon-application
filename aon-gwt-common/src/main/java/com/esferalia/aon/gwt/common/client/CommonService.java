package com.esferalia.aon.gwt.common.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Common")
public interface CommonService extends RemoteService {

	// --------------------------------------------------------- CONFIGURATION
	AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain, String user) throws AonCoreException;
	AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain, String user, Date atDate) throws AonCoreException;
	
	// -------------------------------------------------------------- SECURITY
	User getCurrentUser(String domainName, int domain, String currentUser) throws AonCoreException;
	
	// -------------------------------------------------------------- ENTERPRISE
	LinkedList<Enterprise> getParentEnterprises(String domainName, int domain, String user,String query) throws AonCoreException;
	Enterprise getEnterprise(String domainName, int domain, String user, int id) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain, String user) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain, String user,int enterprise) throws AonCoreException;
	
	// -------------------------------------------------------------- ACCOUNT
	Account getAccount(String domainName,int domain, String user,Integer id) throws AonCoreException;
	Account getAccount(String domainName,int domain, String user,String code) throws AonCoreException;
	LinkedList<Account> getAccounts(String domainName,int domain, String user,String query) throws AonCoreException;
	Account save(String domainName, int domain, String user, Account account) throws AonCoreException;
	Account delete(String domainName, int domain, String user, Account account) throws AonCoreException;
	String getAccountNextCode(String domainName, int domain, String user, String prefix);
	
	// -------------------------------------------------------------- CREDITOR
	LinkedList<Creditor> getBasicCreditors(String domainName,int domain, String user,String query) throws AonCoreException;
	
	// -------------------------------------------------------------- REGISTRY
	AccountingRegistry getAccountingRegistry(String domainName, int domain, String user, AccountingRegistry ar) throws AonCoreException;
	LinkedList<AccountingRegistry> getAccountingRegistries(String domainName,int domain, String user,Integer id) throws AonCoreException;
	LinkedList<AccountingRegistry> getAccountingRegistries(String domainName,int domain, String user,String query) throws AonCoreException;
	LinkedList<AccountingRegistry> getAccountingRegistries(String domainName,int domain, String user,AccountingRegistryParams params) throws AonCoreException;
	LinkedList<InvoiceRegistry> getInvoiceRegistries(String domainName,int domain, String user,String query) throws AonCoreException;
	LinkedList<Product> getInvoiceProducts(String domainName,int domain, String user,String query) throws AonCoreException;
	AccountingRegistry insert(String domainName,int domain, String user,AccountingRegistry reg) throws AonCoreException;
	AccountingRegistry update(String domainName,int domain, String user,AccountingRegistry reg) throws AonCoreException;


	

}
