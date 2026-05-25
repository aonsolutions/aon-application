package com.esferalia.aon.gwt.common.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.ImportError;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.CustomerParams;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Registry")
public interface RegistryService extends RemoteService {
	
	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************
	LinkedList<Customer> getCustomers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) ;
	CustomerFull getCustomerFull(String domainName,int domain, String user, Integer id) throws AonCoreException;
	CustomerFull save(String domainName,int domain, String user, CustomerFull customerFull) throws AonCoreException;
	Domain getDomainLinked(String domainName,int domain, String user, Integer customerId) throws AonCoreException;
	
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
	
	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************
	Map<String, Customer> getCustomersSuggestion(String domainName, int domain, String user, Integer searchDomain, String query);
	Map<String, OldItem> getProductsSuggestion(String domainName, int domain, String user, Integer searchDomain, String query);
	Map<String, Integer> getProductCategoriesSuggestion(String domainName, int domain, String user, Integer searchDomain, String productCategoryQuery);
	Map<String, Integer> getProductTagsSuggestion(String domainName, int domain, String user, Integer searchDomain, String productTagQuery);
	Map<Integer, Integer> getCustomerProductsUpdates(String domainName, int domain, String user, Integer searchDomain, CustomerFeeParams customerFeeParams);
	
	LinkedList<Fee> getCustomerFeeList(String domainName, int domain, String user, CustomerFeeParams customerFeeParams);
	Integer saveCustomerFeeList(String domainName, int domain, String user, LinkedList<Fee>  feeList);
	Integer saveMassiveCustomerFee(String domainName, int domain, String user, Fee fee, CustomerFeeParams params);
	Fee createCustomerFeeList(String domainName, int domain, String user, Fee fee);
	void updateRitemCustomerFee(String domainName, int domain, String user, Integer id, Integer ritem);
	Map<Integer, Integer> getMinMaxCustomerFeeYear(String domainName, int domain, String user, Integer searchDomain);
	Integer getItemIdByProductCode(String domainName, int domain, String user, String productCode);
	void deleteCustomerFeeList(String domainName, int domain, String user, LinkedList<Fee> selectedFees);
	void deleteCustomerFeeList(String domainName, int domain, String user, CustomerFeeParams params);
	void deleteCustomerFee(String domainName, int domain, String user, Fee fee);
	
	Map<String, Workplace> getWorkplacesSuggestion(String domainName, int domain, String user, Integer searchDomain, String workplaceQuery);
	Map<String, Seller> getSellersSuggestion(String domainName, int domain, String user, Integer searchDomain, String sellerQuery);
	Map<String, InvoicingGroup> getInvoicingGroupsSuggestion(String domainName, int domain, String user, Integer searchDomain, String invoicingGroupQuery);
	Map<String, Project> getProjectsSuggestion(String domainName, int domain, String user, Integer customerId, Integer searchDomain, String projectQuery);
	Seller getSellerByTaskHolder(String domainName, int domain, String user, Integer taskHolderId, Integer officeDomain);
	
	Map<String, Fee> getCustomerFeeSuggestion(String domainName, int domain, String user, Integer itemId, Integer customerId, String customerFeeQuery);
	
	List<Fee> parseFeeFile(Domain domain, User user, String data);
	ImportError importFee(Domain domain, User user, Fee fee, Integer index);
	
	List<Customer> getCustomerWithoutFee(String domainName, int domain, String user, CustomerParams customerParams) throws IllegalArgumentException;
	List<Customer> getCustomerWithoutFee(CustomerParams customerParams) throws IllegalArgumentException;
	
	void reorderCustomerFeeLine(String domainName, int domain, String user, Integer customer);
	
	// **************************************************
	// ********************************** [BOOKING CHECK]
	// **************************************************
	LinkedList<BookingCheck> getBookingWithoutFeeList(String domainName, int domain, String user, CustomerFeeParams params);
	LinkedList<BookingCheck> getFeeWithoutBookingList(String domainName, int domain, String user, CustomerFeeParams params);
	LinkedList<BookingCheck> getBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params);
	LinkedList<BookingCheck> getCustomerBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params);
	LinkedList<BookingCheck> getCustomerChildBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params);
	void saveBookingCheck(String domainName, int domain, String user, BookingCheck bookingCheck);
	void deleteBookingList(String domainName, int domain, String user, LinkedList<BookingCheck> selectedBookings);
	Seller getCustomerSeller(String domainName, int domain, String user, Integer customerId);
	String getCustomerSellerEmail(String domainName, int domain, String user, Integer customerId);
	
	// **************************************************
	// ********************************** [SUPPORT AGENT]
	// **************************************************
	HashMap<Seller, RegistryMedia> getActiveSupportAgents(String domainName, int domain, String user);
	List<Customer> getCustomerWithoutAgent(String domainName, int domain, String user);
	
	Account createRegistryAccount(String domainName, Integer domainId, String user, String registryName, String registryAlias, RegistrySource registrySource);

}
