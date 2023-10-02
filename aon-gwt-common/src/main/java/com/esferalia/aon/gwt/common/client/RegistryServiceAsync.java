package com.esferalia.aon.gwt.common.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.security.User;
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
	void getCustomersSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, Customer>> asyncCallback);
	void getProductsSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, OldItem>> asyncCallback);
	void getProductCategoriesSuggestion(String domainName, int domain, String user, String productCategoryQuery, AsyncCallback<Map<String, Integer>> asyncCallback);
	void getProductTagsSuggestion(String domainName, int domain, String user, String productTagQuery, AsyncCallback<Map<String, Integer>> asyncCallback);
	void getCustomerProductsUpdates(String domainName, int domain, String user, CustomerFeeParams customerFeeParams, AsyncCallback<Map<Integer, Integer>> asyncCallback);
	
	void getCustomerFeeList(String domainName, int domain, String user, CustomerFeeParams customerFeeParams, AsyncCallback<LinkedList<Fee>> asyncCallback);
	void saveCustomerFeeList(String domainName, int domain, String user, LinkedList<Fee> feeList, AsyncCallback<Integer> asyncCallback);
	void createCustomerFeeList(String domainName, int domain, String user, Fee fee, AsyncCallback<Void> asyncCallback);
	void saveMassiveCustomerFee(String domainName, int domain, String user, Fee fee, CustomerFeeParams params, AsyncCallback<Integer> asyncCallback);
	void getMinMaxCustomerFeeYear(String domainName, int domain, String user, AsyncCallback<Map<Integer, Integer>> asyncCallback);
	void getItemIdByProductCode(String domainName, int domain, String user, String productCode, AsyncCallback<Integer> asyncCallback);
	void deleteCustomerFeeList(String domainName, int domain, String user, LinkedList<Fee> selectedFees, AsyncCallback<Void> asyncCallback);
	void deleteCustomerFeeList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<Void> asyncCallback);
	
	void getWorkplacesSuggestion(String domainName, int domain, String user, String workplaceQuery, AsyncCallback<Map<String, Workplace>> asyncCallback);
	void getSellersSuggestion(String domainName, int domain, String user, String sellerQuery, AsyncCallback<Map<String, Seller>> asyncCallback);
	
	void getSupporstSuggestion(String domainName, int domain, String user, String sellerQuery, AsyncCallback<Map<String, RegistrySeller>> asyncCallback);
	
//	void getSellerType(String domainName, int domain, String user, String sellerQuery, AsyncCallback<Map<String, RegistrySeller>> asyncCallback);
	
	void getInvoicingGroupsSuggestion(String domainName, int domain, String user, String invoicingGroupQuery, AsyncCallback<Map<String, InvoicingGroup>> asyncCallback);
	void getProjectsSuggestion(String domainName, int domain, String user, Integer customerId, String projectQuery, AsyncCallback<Map<String, Project>> asyncCallback);
	
	void parseFeeFile(Domain domain, User user, String data, AsyncCallback<List<Fee>> asyncCallback);
	void importFee(Domain domain, User user, Fee fee, Integer index, AsyncCallback<ImportError> callback);
	
	void getCustomerWithoutFee(String domainName, int domain, String user, CustomerParams customerParams, AsyncCallback<List<Customer>> asyncCallback);
	
	// **************************************************
	// ********************************** [BOOKING CHECK]
	// **************************************************
	void getBookingWithoutFeeList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<LinkedList<BookingCheck>> asyncCallback);
	void getFeeWithoutBookingList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<LinkedList<BookingCheck>> asyncCallback);
	void getBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<LinkedList<BookingCheck>> asyncCallback);
	void getCustomerBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<LinkedList<BookingCheck>> asyncCallback);
	void saveBookingCheck(String domainName, int domain, String user, BookingCheck bookingCheck, AsyncCallback<Void> asyncCallback);
	void deleteBookingList(String domainName, int domain, String user, LinkedList<BookingCheck> selectedBookings, AsyncCallback<Void> asyncCallback);
	
}
