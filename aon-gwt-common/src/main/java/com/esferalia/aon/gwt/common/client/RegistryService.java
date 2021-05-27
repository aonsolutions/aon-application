package com.esferalia.aon.gwt.common.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Registry")
public interface RegistryService extends RemoteService {
	
	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************
	LinkedList<Customer> getCustomers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit);
	CustomerFull getCustomerFull(String domainName,int domain, String user, Integer id) throws AonCoreException;
	CustomerFull save(String domainName,int domain, String user, CustomerFull customerFull) throws AonCoreException;
	
	// **************************************************
	// *************************************** [CREDITOR]
	// **************************************************
	LinkedList<Creditor> getCreditors(String domainName, int domain, String user, RegistryParams params, int ofs, int limit);
	CreditorFull getCreditorFull(String domainName,int domain, String user, Integer id) throws AonCoreException;
	CreditorFull save(String domainName,int domain, String user, CreditorFull creditorFull) throws AonCoreException;

	// **************************************************
	// *************************************** [SUPPLIER]
	// **************************************************
	LinkedList<Supplier> getSuppliers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit);
	SupplierFull getSupplierFull(String domainName,int domain, String user, Integer id) throws AonCoreException;
	SupplierFull save(String domainName,int domain, String user, SupplierFull supplierFull) throws AonCoreException;

}
