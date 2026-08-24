package com.esferalia.aon.gwt.common.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccountingRegistryServiceAsync {

	void initialize(String domainName, int domain, String user, AccountingRegistry ar, AsyncCallback<AccountingRegistry> callback);
//	void getAccountingRegistry(String domainName, int domain, String user, AccountingRegistry ar, AsyncCallback<AccountingRegistry> callback);
	void getAccountingRegistries(String domainName, int domain, String user, Integer id, AsyncCallback<LinkedList<AccountingRegistry>> callback);
	void getAccountingRegistries(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<AccountingRegistry>> asyncCallback);
	void getAccountingRegistries(String domainName, int domain, String user, AccountingRegistryParams params, AsyncCallback<LinkedList<AccountingRegistry>> callback);
	void getInvoiceProducts(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<Product>> asyncCallback);
	void insert(String domainName, int domain, String user, AccountingRegistry reg, AsyncCallback<AccountingRegistry> callback);
	void update(String domainName, int domain, String user, AccountingRegistry reg, AsyncCallback<AccountingRegistry> callback);

}
