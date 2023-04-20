package com.esferalia.aon.gwt.common.client;

import java.util.LinkedList;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
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

	@Override
	public void getDomainLinked(String domainName, int domain, String user, Integer customerId, AsyncCallback<Domain> callback) {
		AON.start();
		serviceAsync.getDomainLinked(domainName, domain, user, customerId, new AsyncCallbackWrapper<Domain>(callback));
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
	
	// **************************************************
	// *********************************** [CUSTOMER FEE]
	// **************************************************
	
	@Override
	public void getCustomersSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, String>> callback) {
		AON.start();
		serviceAsync.getCustomersSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, String>>(callback));
	}

	@Override
	public void getProductsSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, String>> callback) {
		AON.start();
		serviceAsync.getProductsSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, String>>(callback));
	}
	
	@Override
	public void getCustomerProductsUpdates(String domainName, int domain, String user, CustomerFeeParams customerFeeParams, AsyncCallback<Map<Integer, Integer>> callback) {
		AON.start();
		serviceAsync.getCustomerProductsUpdates(domainName, domain, user, customerFeeParams, new AsyncCallbackWrapper<Map<Integer, Integer>>(callback));
	}
	
	@Override
	public void getCustomerFeeList(String domainName, int domain, String user, CustomerFeeParams customerFeeParams, AsyncCallback<LinkedList<Fee>> callback) {
		AON.start();
		serviceAsync.getCustomerFeeList(domainName, domain, user, customerFeeParams, new AsyncCallbackWrapper<LinkedList<Fee>>(callback));
	}
	@Override
	public void saveCustomerFeeList(String domainName, int domain, String user, LinkedList<Fee> feeList, AsyncCallback<Integer> callback) {
		AON.start();
		serviceAsync.saveCustomerFeeList(domainName, domain, user, feeList, new AsyncCallbackWrapper<Integer>(callback));
	}

	@Override
	public void saveMassiveCustomerFee(String domainName, int domain, String user, Fee fee, CustomerFeeParams params, AsyncCallback<Integer> callback) {
		AON.start();
		serviceAsync.saveMassiveCustomerFee(domainName, domain, user, fee, params, new AsyncCallbackWrapper<Integer>(callback));
	}

	@Override
	public void getMinMaxCustomerFeeYear(String domainName, int domain, String user, AsyncCallback<Map<Integer, Integer>> callback) {
		AON.start();
		serviceAsync.getMinMaxCustomerFeeYear(domainName, domain, user, new AsyncCallbackWrapper<Map<Integer, Integer>>(callback));
	}

	@Override
	public void getItemIdByProductCode(String domainName, int domain, String user, String productCode, AsyncCallback<Integer> callback) {
		AON.start();
		serviceAsync.getItemIdByProductCode(domainName, domain, user, productCode, new AsyncCallbackWrapper<Integer>(callback));
	}

	@Override
	public void deleteCustomerFeeList(String domainName, int domain, String user, LinkedList<Fee> selectedFees, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.deleteCustomerFeeList(domainName, domain, user, selectedFees, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deleteCustomerFeeList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.deleteCustomerFeeList(domainName, domain, user, params, new AsyncCallbackWrapper<Void>(callback));
	}

}
