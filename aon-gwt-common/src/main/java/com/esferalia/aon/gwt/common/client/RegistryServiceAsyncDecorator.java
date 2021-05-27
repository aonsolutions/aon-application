package com.esferalia.aon.gwt.common.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RegistryServiceAsyncDecorator implements RegistryServiceAsync {

	private RegistryServiceAsync serviceAsync;

	public RegistryServiceAsyncDecorator(RegistryServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************
	@Override
	public void getCustomers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit, AsyncCallback<LinkedList<Customer>> callback) {
		AON.start();
		serviceAsync.getCustomers(domainName, domain, user, params,ofs,limit, new AsyncCallbackWrapper<LinkedList<Customer>>(callback));
	}
	@Override
	public void getCustomerFull(String domainName, int domain, String user, Integer id, AsyncCallback<CustomerFull> callback) {
		AON.start();
		serviceAsync.getCustomerFull(domainName, domain, user, id, new AsyncCallbackWrapper<CustomerFull>(callback));
	}

	@Override
	public void save(String domainName, int domain, String user, CustomerFull customerFull, AsyncCallback<CustomerFull> callback) {
		AON.start();
		serviceAsync.save(domainName, domain, user, customerFull, new AsyncCallbackWrapper<CustomerFull>(callback));
	}

	// **************************************************
	// *************************************** [CREDITOR]
	// **************************************************
	@Override
	public void getCreditors(String domainName, int domain, String user, RegistryParams params, int ofs, int limit, AsyncCallback<LinkedList<Creditor>> callback) {
		AON.start();
		serviceAsync.getCreditors(domainName, domain, user, params,ofs,limit, new AsyncCallbackWrapper<LinkedList<Creditor>>(callback));
	}
	
	@Override
	public void getCreditorFull(String domainName, int domain, String user, Integer id, AsyncCallback<CreditorFull> callback) {
		AON.start();
		serviceAsync.getCreditorFull(domainName, domain, user, id, new AsyncCallbackWrapper<CreditorFull>(callback));
	}

	@Override
	public void save(String domainName, int domain, String user, CreditorFull creditorFull, AsyncCallback<CreditorFull> callback) {
		AON.start();
		serviceAsync.save(domainName, domain, user, creditorFull, new AsyncCallbackWrapper<CreditorFull>(callback));
	}

	// **************************************************
	// *************************************** [SUPPLIER]
	// **************************************************
	@Override
	public void getSuppliers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit, AsyncCallback<LinkedList<Supplier>> callback) {
		AON.start();
		serviceAsync.getSuppliers(domainName, domain, user, params,ofs,limit, new AsyncCallbackWrapper<LinkedList<Supplier>>(callback));
	}
	@Override
	public void getSupplierFull(String domainName, int domain, String user, Integer id, AsyncCallback<SupplierFull> callback) {
		AON.start();
		serviceAsync.getSupplierFull(domainName, domain, user, id, new AsyncCallbackWrapper<SupplierFull>(callback));
	}

	@Override
	public void save(String domainName, int domain, String user, SupplierFull supplierFull, AsyncCallback<SupplierFull> callback) {
		AON.start();
		serviceAsync.save(domainName, domain, user, supplierFull, new AsyncCallbackWrapper<SupplierFull>(callback));
	}

}
