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
	public void getCustomersSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, Customer>> callback) {
		AON.start();
		serviceAsync.getCustomersSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, Customer>>(callback));
	}

	@Override
	public void getProductsSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, OldItem>> callback) {
		AON.start();
		serviceAsync.getProductsSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, OldItem>>(callback));
	}

	@Override
	public void getProductCategoriesSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, Integer>> callback) {
		AON.start();
		serviceAsync.getProductCategoriesSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, Integer>>(callback));
	}

	@Override
	public void getProductTagsSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, Integer>> callback) {
		AON.start();
		serviceAsync.getProductTagsSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, Integer>>(callback));
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
	public void createCustomerFeeList(String domainName, int domain, String user, Fee fee, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.createCustomerFeeList(domainName, domain, user, fee, new AsyncCallbackWrapper<Void>(callback));
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

	@Override
	public void getWorkplacesSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, Workplace>> callback) {
		AON.start();
		serviceAsync.getWorkplacesSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, Workplace>>(callback));
	}

	@Override
	public void getSellersSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, Seller>> callback) {
		AON.start();
		serviceAsync.getSellersSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, Seller>>(callback));
	}
	
	@Override
	public void getSupporstSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, RegistrySeller>> callback) {
		AON.start();
		serviceAsync.getSupporstSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, RegistrySeller>>(callback));
	}
	
//	@Override
//	public void getSellerType(String domainName, int domain, String user, String query, AsyncCallback<Map<String, RegistrySeller>> callback) {
//		AON.start();
//		serviceAsync.getSellerType(domainName, domain, user, query, callback);
//	}

	@Override
	public void getInvoicingGroupsSuggestion(String domainName, int domain, String user, String query, AsyncCallback<Map<String, InvoicingGroup>> callback) {
		AON.start();
		serviceAsync.getInvoicingGroupsSuggestion(domainName, domain, user, query, new AsyncCallbackWrapper<Map<String, InvoicingGroup>>(callback));
	}

	@Override
	public void getProjectsSuggestion(String domainName, int domain, String user, Integer customerId, String query, AsyncCallback<Map<String, Project>> callback) {
		AON.start();
		serviceAsync.getProjectsSuggestion(domainName, domain, user, customerId, query, new AsyncCallbackWrapper<Map<String, Project>>(callback));
	}

	@Override
	public void parseFeeFile(Domain domain, User user, String data, AsyncCallback<List<Fee>> callback) {
		AON.start();
		serviceAsync.parseFeeFile(domain, user, data, new AsyncCallbackWrapper<List<Fee>>(callback));
	}

	@Override
	public void importFee(Domain domain, User user, Fee fee, Integer index, AsyncCallback<ImportError> callback) {
		AON.start();
		serviceAsync.importFee(domain, user, fee, index, new AsyncCallbackWrapper<ImportError>(callback));
	}
	
	@Override
	public void getCustomerWithoutFee(String domainName, int domain, String user, CustomerParams customerParams, AsyncCallback<List<Customer>> callback) {
		AON.start();
		serviceAsync.getCustomerWithoutFee(domainName, domain, user, customerParams, new AsyncCallbackWrapper<List<Customer>>(callback));
	}

	// **************************************************
	// ********************************** [BOOKING CHECK]
	// **************************************************

	@Override
	public void getBookingWithoutFeeList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<LinkedList<BookingCheck>> callback) {
		AON.start();
		serviceAsync.getBookingWithoutFeeList(domainName, domain, user, params, new AsyncCallbackWrapper<LinkedList<BookingCheck>>(callback));
	}
	
	@Override
	public void getFeeWithoutBookingList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<LinkedList<BookingCheck>> callback) {
		AON.start();
		serviceAsync.getFeeWithoutBookingList(domainName, domain, user, params, new AsyncCallbackWrapper<LinkedList<BookingCheck>>(callback));
	}
	
	@Override
	public void getBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<LinkedList<BookingCheck>> callback) {
		AON.start();
		serviceAsync.getBookingCheckList(domainName, domain, user, params, new AsyncCallbackWrapper<LinkedList<BookingCheck>>(callback));
	}
	
	@Override
	public void getCustomerBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params, AsyncCallback<LinkedList<BookingCheck>> callback) {
		AON.start();
		serviceAsync.getCustomerBookingCheckList(domainName, domain, user, params, new AsyncCallbackWrapper<LinkedList<BookingCheck>>(callback));
	}

	@Override
	public void saveBookingCheck(String domainName, int domain, String user, BookingCheck bookingCheck, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.saveBookingCheck(domainName, domain, user, bookingCheck, new AsyncCallbackWrapper<Void>(callback));
	}

}
