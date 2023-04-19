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

public interface RegistryServiceAsync {

	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************
	void getCustomers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit, AsyncCallback<LinkedList<Customer>> asyncCallback);
	void getCustomerFull(String domainName, int domain, String user, Integer id, AsyncCallback<CustomerFull> callback);
	void save(String domainName, int domain, String user, CustomerFull customerFull, AsyncCallback<CustomerFull> callback);
	void getDomainLinked(String domainName, int domain, String user, Integer customerId, AsyncCallback<Domain> callback);

	// **************************************************
	// *************************************** [CREDITOR]
	// **************************************************
	void getCreditors(String domainName, int domain, String user, RegistryParams params, int ofs, int limit, AsyncCallback<LinkedList<Creditor>> asyncCallback);
	void getCreditorFull(String domainName, int domain, String user, Integer id, AsyncCallback<CreditorFull> callback);
	void save(String domainName, int domain, String user, CreditorFull creditorFull, AsyncCallback<CreditorFull> callback);

	// **************************************************
	// *************************************** [SUPPLIER]
	// **************************************************
	void getSuppliers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit, AsyncCallback<LinkedList<Supplier>> asyncCallback);
	void getSupplierFull(String domainName, int domain, String user, Integer id, AsyncCallback<SupplierFull> callback);
	void save(String domainName, int domain, String user, SupplierFull supplierFull, AsyncCallback<SupplierFull> callback);
	
	// **************************************************
	// *********************************** [CUSTOMER FEE]
	// **************************************************
	void getCustomersSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, String>> asyncCallback);
	void getProductsSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, String>> asyncCallback);
	void getCustomerProductsUpdates(String domainName, int domain, String user, CustomerFeeParams customerFeeParams, AsyncCallback<Map<Integer, Integer>> asyncCallback);
	
	void getCustomerFeeList(String domainName, int domain, String user, CustomerFeeParams customerFeeParams, AsyncCallback<LinkedList<Fee>> asyncCallback);
	void saveCustomerFeeList(String domainName, int domain, String user, LinkedList<Fee> feeList, AsyncCallback<Integer> asyncCallback);
	void saveMassiveCustomerFee(String domainName, int domain, String user, Fee fee, CustomerFeeParams params, AsyncCallback<Integer> asyncCallback);
	void getMinMaxCustomerFeeYear(String domainName, int domain, String user, AsyncCallback<Map<Integer, Integer>> asyncCallback);
	void getItemIdByProductCode(String domainName, int domain, String user, String productCode, AsyncCallback<Integer> asyncCallback);
}
