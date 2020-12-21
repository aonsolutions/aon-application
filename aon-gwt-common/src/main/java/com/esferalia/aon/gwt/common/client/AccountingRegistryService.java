package com.esferalia.aon.gwt.common.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AccountingRegistry")
public interface AccountingRegistryService extends RemoteService {

	AccountingRegistry getAccountingRegistry(String domainName, int domain, String user, AccountingRegistry ar) throws AonCoreException;
	LinkedList<AccountingRegistry> getAccountingRegistries(String domainName,int domain, String user,Integer id) throws AonCoreException;
	LinkedList<AccountingRegistry> getAccountingRegistries(String domainName,int domain, String user,String query) throws AonCoreException;
	LinkedList<AccountingRegistry> getAccountingRegistries(String domainName,int domain, String user,AccountingRegistryParams params) throws AonCoreException;
	LinkedList<InvoiceRegistry> getInvoiceRegistries(String domainName,int domain, String user,String query) throws AonCoreException;
	LinkedList<Product> getInvoiceProducts(String domainName,int domain, String user,String query) throws AonCoreException;
	AccountingRegistry insert(String domainName,int domain, String user,AccountingRegistry reg) throws AonCoreException;
	AccountingRegistry update(String domainName,int domain, String user,AccountingRegistry reg) throws AonCoreException;

}
