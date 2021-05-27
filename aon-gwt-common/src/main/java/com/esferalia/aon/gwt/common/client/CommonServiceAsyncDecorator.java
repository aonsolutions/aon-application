package com.esferalia.aon.gwt.common.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CommonServiceAsyncDecorator implements CommonServiceAsync {

	private CommonServiceAsync serviceAsync;

	public CommonServiceAsyncDecorator(CommonServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	@Override
	public void getAonConfiguration(String currentDomainName, int currentDomain, String user, Date atDate, AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(currentDomainName, currentDomain, user, atDate, new AsyncCallbackWrapper<AonConfiguration>(callback));
	}

	@Override
	public void getAonConfiguration(String currentDomainName, int currentDomain, String user, AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(currentDomainName, currentDomain, user, new AsyncCallbackWrapper<AonConfiguration>(callback));
	}

	// **************************************************
	// *************************************** [SECURITY]
	// **************************************************
	@Override
	public void getCurrentUser(String domainName, int domain, String user, AsyncCallback<User> callback) {
		AON.start();
		serviceAsync.getCurrentUser(domainName, domain, user, new AsyncCallbackWrapper<User>(callback));
	}

	// **************************************************
	// ************************************* [ENTERPRISE]
	// **************************************************
	@Override
	public void getParentEnterprises(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<Enterprise>> callback) {
		AON.start();
		serviceAsync.getParentEnterprises(domainName, domain, user, query, new AsyncCallbackWrapper<LinkedList<Enterprise>>(callback));
	}

	@Override
	public void getEnterprise(String domainName, int domain, String user, int id, AsyncCallback<Enterprise> callback) {
		AON.start();
		serviceAsync.getEnterprise(domainName, domain, user, id, new AsyncCallbackWrapper<Enterprise>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, String user, int enterprise, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		serviceAsync.getCompanyBanks(domainName, domain, user, enterprise, new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, String user, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		serviceAsync.getCompanyBanks(domainName, domain, user, new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}

	// **************************************************
	// **************************************** [ACCOUNT]
	// **************************************************
	@Override
	public void getAccount(String domainName, int domain, String user, Integer id, AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.getAccount(domainName, domain, user, id, new AsyncCallbackWrapper<Account>(callback));
	}

	@Override
	public void getAccount(String domainName, int domain, String user, String code, AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.getAccount(domainName, domain, user, code, new AsyncCallbackWrapper<Account>(callback));
	}

	@Override
	public void getAccounts(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<Account>> callback) {
		AON.start();
		serviceAsync.getAccounts(domainName, domain, user, query, new AsyncCallbackWrapper<LinkedList<Account>>(callback));
	}

	@Override
	public void save(String domainName, int domain, String user, Account account, AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.save(domainName, domain, user, account, new AsyncCallbackWrapper<Account>(callback));
	}

	@Override
	public void delete(String domainName, int domain, String user, Account account, AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.delete(domainName, domain, user, account, new AsyncCallbackWrapper<Account>(callback));
	}

	@Override
	public void getAccountNextCode(String domainName, int domain, String user, String prefix, AsyncCallback<String> callback) {
		AON.start();
		serviceAsync.getAccountNextCode(domainName, domain, user, prefix, new AsyncCallbackWrapper<String>(callback));
	}

	// **************************************************
	// ************************************* [PAY_METHOD]
	// **************************************************
	@Override
	public void getPayMethods(String domainName, int domain, String user, AsyncCallback<LinkedList<PayMethod>> callback) {
		AON.start();
		serviceAsync.getPayMethods(domainName, domain, user, new AsyncCallbackWrapper<LinkedList<PayMethod>>(callback));
	}
	
	@Override
	public void savePayMethod(String domainName, int domain, String user, PayMethod payMethod, AsyncCallback<PayMethod> callback) {
		AON.start();
		serviceAsync.savePayMethod(domainName, domain, user, payMethod, new AsyncCallbackWrapper<PayMethod>(callback));
	}
	
	@Override
	public void deletePayMethod(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.deletePayMethod(domainName, domain, user, id, new AsyncCallbackWrapper<Void>(callback));
	}

	// **************************************************
	// *************************************** [CREDITOR]
	// **************************************************
	@Override
	public void getBasicCreditors(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<Creditor>> callback) {
		AON.start();
		serviceAsync.getBasicCreditors(domainName, domain, user, query, new AsyncCallbackWrapper<LinkedList<Creditor>>(callback));
	}

	// **************************************************
	// **************************************** [INVOICE]
	// **************************************************
	@Override
	public void getInvoiceRegistries(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<InvoiceRegistry>> callback) {
		AON.start();
		serviceAsync.getInvoiceRegistries(domainName, domain, user, query, new AsyncCallbackWrapper<LinkedList<InvoiceRegistry>>(callback));
	}

	@Override
	public void getInvoiceProducts(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<OldProduct>> callback) {
		AON.start();
		serviceAsync.getInvoiceProducts(domainName, domain, user, query, new AsyncCallbackWrapper<LinkedList<OldProduct>>(callback));
	}

}
