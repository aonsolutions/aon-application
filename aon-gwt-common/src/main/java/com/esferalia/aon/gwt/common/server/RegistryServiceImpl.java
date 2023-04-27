package com.esferalia.aon.gwt.common.server;

import java.util.LinkedList;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Aon Registry Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Registry", "/aon_gwt_aio/ms/Registry"})
public class RegistryServiceImpl extends AonStatelessRemoteServiceServlet implements RegistryService {

	private static final long serialVersionUID = -8911264415447499114L;

	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************

	@Override
	public LinkedList<Customer> getCustomers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) {
		return AON.getCustomers(domainName, domain,user, params, ofs, limit);
	}
	@Override
	public CustomerFull getCustomerFull(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getCustomerFull(domainName, domain,user, id);
	}

	@Override
	public CustomerFull save(String domainName, int domain, String user, CustomerFull customerFull) throws AonCoreException {
		return AON.save(domainName, domain,user, customerFull);
	}
	
	@Override
	public Domain getDomainLinked(String domainName, int domain, String user, Integer customerId) throws AonCoreException {
		return AON.getDomainLinked(domainName, domain,user, customerId);
	}

	// **************************************************
	// *************************************** [CREDITOR]
	// **************************************************
	@Override
	public LinkedList<Creditor> getCreditors(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) {
		return AON.getCreditors(domainName, domain,user, params, ofs, limit);
	}
	@Override
	public CreditorFull getCreditorFull(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getCreditorFull(domainName, domain,user, id);
	}

	@Override
	public CreditorFull save(String domainName, int domain, String user, CreditorFull creditorFull) throws AonCoreException {
		return AON.save(domainName, domain,user, creditorFull);
	}

	// **************************************************
	// *************************************** [SUPPLIER]
	// **************************************************

	@Override
	public LinkedList<Supplier> getSuppliers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) {
		return AON.getSuppliers(domainName, domain,user, params, ofs, limit);
	}
	@Override
	public SupplierFull getSupplierFull(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getSupplierFull(domainName, domain,user, id);
	}

	@Override
	public SupplierFull save(String domainName, int domain, String user, SupplierFull supplierFull) throws AonCoreException {
		return AON.save(domainName, domain,user, supplierFull);
	}
	
	// **************************************************
	// *********************************** [CUSTOMER FEE]
	// **************************************************
	
	@Override
	public Map<String, String> getCustomersSuggestion(String domainName, int domain, String user, String query) {
		return AON.getCustomersSuggestion(domainName, domain, user, query);
	}
	@Override
	public Map<String, OldItem> getProductsSuggestion(String domainName, int domain, String user, String query) {
		return AON.getProductsSuggestion(domainName, domain, user, query);
	}
	
	@Override
	public Map<Integer, Integer> getCustomerProductsUpdates(String domainName, int domain, String user, CustomerFeeParams customerFeeParams) {
		return AON.getCustomerProductsUpdates(domainName, domain, user, customerFeeParams);
	}
	
	@Override
	public LinkedList<Fee> getCustomerFeeList(String domainName, int domain, String user, CustomerFeeParams customerFeeParams) {
		return AON.getFeeList(domainName, domain, user, customerFeeParams);
	}
	
	@Override
	public Integer saveCustomerFeeList(String domainName, int domain, String user, LinkedList<Fee> feeList) {
		return AON.saveFees(domainName, domain, user, feeList);
	}

	@Override
	public Integer saveMassiveCustomerFee(String domainName, int domain, String user, Fee fee, CustomerFeeParams params) {
		return AON.saveMassiveFees(domainName, domain, user, fee, params);
	}
	
	@Override
	public Map<Integer, Integer> getMinMaxCustomerFeeYear(String domainName, int domain, String user) {
		return AON.getMinMaxCustomerFeeYear(domainName, domain, user);
	}
	@Override
	public Integer getItemIdByProductCode(String domainName, int domain, String user, String productCode) {
		return AON.getItemIdByProductCode(domainName, domain, user, productCode);
	}
	@Override
	public void deleteCustomerFeeList(String domainName, int domain, String user, LinkedList<Fee> selectedFees) {
		AON.deleteFee(domainName, domain, user, selectedFees.stream());
	}
	@Override
	public void deleteCustomerFeeList(String domainName, int domain, String user, CustomerFeeParams params) {
		AON.deleteFee(domainName, domain, user, params);
	}
	@Override
	public Map<String, Workplace> getWorkplacesSuggestion(String domainName, int domain, String user, String query) {
		return AON.getWorkplacesSuggestion(domainName, domain, user, query);
	}
	@Override
	public Map<String, Seller> getSellersSuggestion(String domainName, int domain, String user, String query) {
		return AON.getSellersSuggestion(domainName, domain, user, query);
	}
	@Override
	public Map<String, InvoicingGroup> getInvoicingGroupsSuggestion(String domainName, int domain, String user, String query) {
		return AON.getInvoicingGroupsSuggestion(domainName, domain, user, query);
	}
	
}
