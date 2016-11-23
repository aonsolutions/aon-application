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


public class CommonServiceAsyncDecorator implements CommonServiceAsync {

	private CommonServiceAsync serviceAsync;

	public CommonServiceAsyncDecorator(CommonServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// --------------------------------------------------------- CONFIGURATION
	@Override
	public void getAonConfiguration(String currentDomainName, int currentDomain, Date atDate,
			AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(currentDomainName, currentDomain, atDate,   
				new AsyncCallbackWrapper<AonConfiguration>(callback));
	}
	@Override
	public void getAonConfiguration(String currentDomainName, int currentDomain,
			AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(currentDomainName, currentDomain,   
				new AsyncCallbackWrapper<AonConfiguration>(callback));
	}

	// -------------------------------------------------------------- SECURITY
	@Override
	public void getCurrentUser(String domainName, int domain,
			AsyncCallback<User> callback) {
		AON.start();
		serviceAsync.getCurrentUser(domainName, domain,  
				new AsyncCallbackWrapper<User>(callback));
	}

	// -------------------------------------------------------------- PARAMS
	@Override
	public void getParentEnterprises(String domainName, int domain,
			String query,AsyncCallback<LinkedList<Enterprise>> callback) {
		AON.start();
		serviceAsync.getParentEnterprises(domainName, domain, query,  
				new AsyncCallbackWrapper<LinkedList<Enterprise>>(callback));
	}

	@Override
	public void getEnterprise(String domainName, int domain, int id,
			AsyncCallback<Enterprise> callback) {
		AON.start();
		serviceAsync.getEnterprise(domainName, domain, id,  
				new AsyncCallbackWrapper<Enterprise>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, int enterprise,
			AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		serviceAsync.getCompanyBanks(domainName, domain, enterprise,  
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
	@Override
	public void getCompanyBanks(String domainName, int domain,
			AsyncCallback<LinkedList<CompanyBank>> callback){
		AON.start();
		serviceAsync.getCompanyBanks(domainName, domain,   
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}

	// -------------------------------------------------------------- ACCOUNT
	@Override
	public void getAccount(String domainName, int domain, Integer id,
			AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.getAccount(domainName, domain, id,   
				new AsyncCallbackWrapper<Account>(callback));
	}
	
	@Override
	public void getAccount(String domainName, int domain, String code,
			AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.getAccount(domainName, domain, code,   
				new AsyncCallbackWrapper<Account>(callback));
	}

	@Override
	public void getAccounts(String domainName, int domain, String query,
			AsyncCallback<LinkedList<Account>> callback) {
		AON.start();
		serviceAsync.getAccounts(domainName, domain, query,   
				new AsyncCallbackWrapper<LinkedList<Account>>(callback));
	}

	@Override
	public void save(String domainName, int domain, Account account, AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.save(domainName, domain, account,   
				new AsyncCallbackWrapper<Account>(callback));
	}
	@Override
	public void getAccountNextCode(String domainName, int domain, String prefix,
			AsyncCallback<String> callback) {
		AON.start();
		serviceAsync.getAccountNextCode(domainName, domain, prefix,   
				new AsyncCallbackWrapper<String>(callback));
	}

	// -------------------------------------------------------------- CREDITOR
	@Override
	public void getBasicCreditors(String domainName, int domain, String query,
			AsyncCallback<LinkedList<Creditor>> callback) {
		AON.start();
		serviceAsync.getBasicCreditors(domainName, domain, query,   
				new AsyncCallbackWrapper<LinkedList<Creditor>>(callback));
	}

	// -------------------------------------------------------------- REGISTRY
	@Override
	public void getAccountingRegistries(String domainName, int domain, String query,
			AsyncCallback<LinkedList<AccountingRegistry>> callback) {
		AON.start();
		serviceAsync.getAccountingRegistries(domainName, domain, query,   
				new AsyncCallbackWrapper<LinkedList<AccountingRegistry>>(callback));
	}

	@Override
	public void insert(String domainName, int domain, AccountingRegistry reg,
			AsyncCallback<AccountingRegistry> callback) {
		AON.start();
		serviceAsync.insert(domainName, domain, reg,   
				new AsyncCallbackWrapper<AccountingRegistry>(callback));
		
	}



}
