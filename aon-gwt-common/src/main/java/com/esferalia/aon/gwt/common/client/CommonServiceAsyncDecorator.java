package com.esferalia.aon.gwt.common.client;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class CommonServiceAsyncDecorator implements CommonServiceAsync {

	private CommonServiceAsync serviceAsync;

	public CommonServiceAsyncDecorator(CommonServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// -------------------------------------------------------------- PARAMS
	@Override
	public void getParentEnterprises(String domainName, int domain,
			String query,AsyncCallback<ArrayList<Enterprise>> callback) {
		AON.start();
		serviceAsync.getParentEnterprises(domainName, domain, query,  
				new AsyncCallbackWrapper<ArrayList<Enterprise>>(callback));
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
			AsyncCallback<ArrayList<CompanyBank>> callback) {
		AON.start();
		serviceAsync.getCompanyBanks(domainName, domain, enterprise,  
				new AsyncCallbackWrapper<ArrayList<CompanyBank>>(callback));
	}
	@Override
	public void getCompanyBanks(String domainName, int domain,
			AsyncCallback<ArrayList<CompanyBank>> callback){
		AON.start();
		serviceAsync.getCompanyBanks(domainName, domain,   
				new AsyncCallbackWrapper<ArrayList<CompanyBank>>(callback));
	}

	// -------------------------------------------------------------- ACCOUNT
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


}
