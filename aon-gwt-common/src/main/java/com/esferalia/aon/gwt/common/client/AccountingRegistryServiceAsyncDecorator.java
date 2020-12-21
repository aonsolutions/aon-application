package com.esferalia.aon.gwt.common.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AccountingRegistryServiceAsyncDecorator implements AccountingRegistryServiceAsync {

	private AccountingRegistryServiceAsync serviceAsync;

	public AccountingRegistryServiceAsyncDecorator(AccountingRegistryServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	@Override
	public void getAccountingRegistry(String domainName, int domain, String user, AccountingRegistry ar, AsyncCallback<AccountingRegistry> callback) {
		AON.start();
		serviceAsync.getAccountingRegistry(domainName, domain, user, ar, new AsyncCallbackWrapper<AccountingRegistry>(callback));
	}

	@Override
	public void getAccountingRegistries(String domainName, int domain, String user, Integer id, AsyncCallback<LinkedList<AccountingRegistry>> callback) {
		AON.start();
		serviceAsync.getAccountingRegistries(domainName, domain, user, id, new AsyncCallbackWrapper<LinkedList<AccountingRegistry>>(callback));
	}

	@Override
	public void getAccountingRegistries(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<AccountingRegistry>> callback) {
		AON.start();
		serviceAsync.getAccountingRegistries(domainName, domain, user, query, new AsyncCallbackWrapper<LinkedList<AccountingRegistry>>(callback));
	}

	@Override
	public void getAccountingRegistries(String domainName, int domain, String user, AccountingRegistryParams params, AsyncCallback<LinkedList<AccountingRegistry>> callback) {
		AON.start();
		serviceAsync.getAccountingRegistries(domainName, domain, user, params, new AsyncCallbackWrapper<LinkedList<AccountingRegistry>>(callback));
	}

	@Override
	public void getInvoiceRegistries(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<InvoiceRegistry>> callback) {
		AON.start();
		serviceAsync.getInvoiceRegistries(domainName, domain, user, query, new AsyncCallbackWrapper<LinkedList<InvoiceRegistry>>(callback));
	}

	@Override
	public void getInvoiceProducts(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<Product>> callback) {
		AON.start();
		serviceAsync.getInvoiceProducts(domainName, domain, user, query, new AsyncCallbackWrapper<LinkedList<Product>>(callback));
	}

	@Override
	public void insert(String domainName, int domain, String user, AccountingRegistry reg, AsyncCallback<AccountingRegistry> callback) {
		AON.start();
		serviceAsync.insert(domainName, domain, user, reg, new AsyncCallbackWrapper<AccountingRegistry>(callback));
	}

	@Override
	public void update(String domainName, int domain, String user, AccountingRegistry reg, AsyncCallback<AccountingRegistry> callback) {
		AON.start();
		serviceAsync.update(domainName, domain, user, reg, new AsyncCallbackWrapper<AccountingRegistry>(callback));
	}

}
