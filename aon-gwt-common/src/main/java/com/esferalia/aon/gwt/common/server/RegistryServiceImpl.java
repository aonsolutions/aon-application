package com.esferalia.aon.gwt.common.server;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
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
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.templates.FeeImport;

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
	
	@Override
	public void deleteCustomerFull(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteCustomerFull(domainName, domain,user, id);
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
	
	@Override
	public void deleteCreditorFull(String domainName, int domain, String user, Integer id) {
		AON.deleteCreditorFull(domainName, domain,user, id);
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
	
	@Override
	public void deleteSupplierFull(String domainName, int domain, String user, Integer id) {
		AON.deleteSupplierFull(domainName, domain,user, id);
	}
	
	// **************************************************
	// *********************************** [CUSTOMER FEE]
	// **************************************************
	
	@Override
	public Map<String, Customer> getCustomersSuggestion(String domainName, int domain, String user, Integer searchDomain, String query) {
		return AON.getCustomersSuggestion(domainName, domain, user, searchDomain, query);
	}
	
	@Override
	public Map<String, OldItem> getProductsSuggestion(String domainName, int domain, String user, Integer searchDomain, String query) {
		return AON.getProductsSuggestion(domainName, domain, user, searchDomain, query);
	}
	
	@Override
	public Map<String, Integer> getProductCategoriesSuggestion(String domainName, int domain, String user, Integer searchDomain, String query) {
		return AON.getProductCategoriesSuggestion(domainName, domain, user, searchDomain, query);
	}
	
	@Override
	public Map<String, Integer> getProductTagsSuggestion(String domainName, int domain, String user, Integer searchDomain, String query) {
		return AON.getProductTagsSuggestion(domainName, domain, user, searchDomain, query);
	}
	
	@Override
	public Map<Integer, Integer> getCustomerProductsUpdates(String domainName, int domain, String user, Integer searchDomain, CustomerFeeParams customerFeeParams) {
		return AON.getCustomerProductsUpdates(domainName, domain, user, searchDomain, customerFeeParams);
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
	public Fee createCustomerFeeList(String domainName, int domainId, String user, Fee fee) {
		if(null == fee.getDomain()){
			Domain domain = AON.getDomain(domainName, domainId, user);
			fee.setDomain(domain);
		}
		return AON.createCustomerFeeList(domainName, domainId, user, fee);
	}

	@Override
	public void updateRitemCustomerFee(String domainName, int domainId, String user, Integer customerFee, Integer ritem) {
		AON.updateRitemCustomerFee(domainName, domainId, user, customerFee, ritem);
	}
	
	@Override
	public Map<Integer, Integer> getMinMaxCustomerFeeYear(String domainName, int domain, String user, Integer searchDomain) {
		return AON.getMinMaxCustomerFeeYear(domainName, domain, user, searchDomain);
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
	public void deleteCustomerFee(String domainName, int domainId, String login, Fee fee) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(domainName, domainId, login)){
			AON.deleteFee(ctx, fee);
		}	
	}
	
	@Override
	public Map<String, Workplace> getWorkplacesSuggestion(String domainName, int domain, String user, Integer searchDomain, String query) {
		return AON.getWorkplacesSuggestion(domainName, domain, user, searchDomain, query);
	}
	
	@Override
	public Map<String, Seller> getSellersSuggestion(String domainName, int domain, String user, Integer searchDomain, String query) {
		return AON.getSellersSuggestion(domainName, domain, user, searchDomain, query);
	}
	
	@Override
	public Map<String, InvoicingGroup> getInvoicingGroupsSuggestion(String domainName, int domain, String user, Integer searchDomain, String query) {
		return AON.getInvoicingGroupsSuggestion(domainName, domain, user, searchDomain, query)
			.collect(HashMap::new, (m, v) -> m.put(v.getDescription(), v), HashMap::putAll);
	}
	
	@Override
	public Map<String, Project> getProjectsSuggestion(String domainName, int domain, String user, Integer customerId, Integer searchDomain, String query) {
		Map<String, Project> projects = AON.getProjectsSuggestion(domainName, domain, user, customerId, searchDomain, query);
		return projects;
	}
	
	@Override
	public Seller getSellerByTaskHolder(String domainName, int domain, String user, Integer taskHolderId, Integer officeDomain) {
		Seller seller = AON.getSeller(domainName, domain, user, f -> f.getDomainProperty().eq(officeDomain).and(f.getTaskHolderProperty().eq(taskHolderId)));
		return seller;
	}

	@Override
	public Map<String, Fee> getCustomerFeeSuggestion(String domainName, int domain, String user, Integer itemId, Integer customerId, String customerFeeQuery) {
		return AON.getCustomerFeeSuggestion(domainName, domain, user, itemId, customerId, customerFeeQuery);
	}
	
	@Override
	public List<Fee> parseFeeFile(Domain domain, User user, String data) {
		byte[] fileData = java.util.Base64.getDecoder().decode(data);
		return FeeImport.getInstance().importation(domain, user.getLogin(), fileData);
	}
	
	@Override
	public ImportError importFee(Domain domain, User user, Fee fee, Integer index) {
		return FeeImport.insertFee(domain, user, index, fee);
	}
	
	@Override
	public List<Customer> getCustomerWithoutFee(String domainName, int domainId, String user, CustomerParams customerParams) {
		Domain domain = AON.getDomain(domainName, domainId, user, f->f.getNameProperty().eq(domainName));
		return AON.getCustomerWithoutFee(domain, user, customerParams);
	}
	
	@Override
	public List<Customer> getCustomerWithoutFee(CustomerParams params) {
		return AON.getCustomerWithoutFee(new Domain().setName(params.getDomainName()).setId(params.getDomain()), params.getUser(), params);
	}
	
	@Override
	public void reorderCustomerFeeLine(String domainName, int domain, String user, Integer customer) {
		AON.reorderCustomerFeeLine(domainName, domain, user, customer);
	}
	
	// **************************************************
	// ********************************** [BOOKING CHECK]
	// **************************************************

	@Override
	public LinkedList<BookingCheck> getBookingWithoutFeeList(String domainName, int domain, String user, CustomerFeeParams params) {
		return AON.getBookingWithoutFeeList(domainName, domain, user, params);
	}
	
	@Override
	public LinkedList<BookingCheck> getFeeWithoutBookingList(String domainName, int domain, String user, CustomerFeeParams params) {
		return AON.getFeeWithoutBookingList(domainName, domain, user, params);
	}
	
	@Override
	public LinkedList<BookingCheck> getBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params) {
		return AON.getBookingCheckList(domainName, domain, user, params);
	}
	
	@Override
	public LinkedList<BookingCheck> getCustomerBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params) {
		return AON.getCustomerBookingCheckList(domainName, domain, user, params);
	}
	
	@Override
	public LinkedList<BookingCheck> getCustomerChildBookingCheckList(String domainName, int domain, String user, CustomerFeeParams params) {
		return AON.getCustomerChildBookingCheckList(domainName, domain, user, params);
	}
	
	@Override
	public void saveBookingCheck(String domainName, int domain, String user, BookingCheck bookingCheck) {
		AON.saveBookingCheck(domainName, domain, user, bookingCheck);
	}
	@Override
	public void deleteBookingList(String domainName, int domain, String user, LinkedList<BookingCheck> selectedBookings) {
		AON.deleteBookingList(domainName, domain, user, selectedBookings);
	}
	
	@Override
	public Seller getCustomerSeller(String domainName, int domain, String user, Integer customerId) {
		Stream<RegistrySeller> rsellerStream = AON.getRegistrySellerStream(
				new Domain().setName(domainName).setId(domain), 
				user, 
				f -> f.getRegistryProperty().eq(customerId)
					.and(f.getTypeProperty().eq((byte)1))
					.and(f.getStatusProperty().eq((byte)0))
					.and(f.getStartDateProperty().le(new Date(new java.util.Date().getTime())))
					.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(new Date(new java.util.Date().getTime()))))
					.and(f.getDomainProperty().eq(domain))
		);
		
		Optional<RegistrySeller> rsellerOpt = rsellerStream.findFirst();
		
		return rsellerOpt.isPresent() ? rsellerOpt.get().getSeller() : new Seller();
	}
	
	@Override
	public String getCustomerSellerEmail(String domainName, int domain, String user, Integer customerId) {
		Stream<RegistrySeller> rsellerStream = AON.getRegistrySellerStream(
				new Domain().setName(domainName).setId(domain), 
				user, 
				f -> f.getRegistryProperty().eq(customerId)
					.and(f.getTypeProperty().eq((byte)1))
					.and(f.getStatusProperty().eq((byte)0))
					.and(f.getStartDateProperty().le(new Date(new java.util.Date().getTime())))
					.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(new Date(new java.util.Date().getTime()))))
					.and(f.getDomainProperty().eq(domain))
		);
		
		Optional<RegistrySeller> rsellerOpt = rsellerStream.findFirst();
		
		if(!rsellerOpt.isPresent()) return "";
		
		RegistryMedia email = AON.getRegistryMedia(
				new Domain().setName(domainName).setId(domain), 
				new User().setLogin(user), 
				f -> f.getRegistryProperty().eq(rsellerOpt.get().getSeller().getId())
					.and(f.getMediaProperty().eq((byte)4)) // Email
		);
		
		return (null == email || null == email.getId()) ? "" : email.getValue();
		
	}
	
	// **************************************************
	// ********************************** [SUPPORT AGENT]
	// **************************************************

	@Override
	public HashMap<Seller, RegistryMedia> getActiveSupportAgents(String domainName, int domain, String user) {
		HashMap<Seller, RegistryMedia> supportAgents = new HashMap<Seller, RegistryMedia>();
		
		Stream<RegistrySeller> rsellerStream = AON.getRegistrySellerStream(
				new Domain().setName(domainName).setId(domain), 
				user, 
				f -> f.getTypeProperty().eq((byte)1)
					.and(f.getStartDateProperty().le(new Date(new java.util.Date().getTime())))
					.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(new Date(new java.util.Date().getTime()))))
					.and(f.getStatusProperty().eq((byte)0))
					.and(f.getDomainProperty().eq(domain))
		);
		
		List<Seller> sellerList = rsellerStream.map(rseller -> rseller.getSeller()).filter( distinctByKey(seller -> seller.getId()) ).toList();
		
		sellerList.forEach(seller -> {
			// Seller email
			Stream<RegistryMedia> rmediaStream = AON.getRegistryMediaStream(
					new Domain().setName(domainName).setId(domain), 
					new User().setLogin(user),  
					f -> f.getRegistryProperty().eq(seller.getId()).and(f.getDomainProperty().eq(domain)));
			Optional<RegistryMedia> emailMedia = rmediaStream.filter(rmedia -> rmedia.getMedia().equals(MediaType.EMAIL)).findFirst();
			
			supportAgents.put(seller, emailMedia.isPresent() ? emailMedia.get() : new RegistryMedia());
		});
		
		return supportAgents;
	}
	
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>(); 
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null; 
	}
	
	@Override
	public List<Customer> getCustomerWithoutAgent(String domainName, int domain, String user) {
		List<Customer> customers = new ArrayList<>();
		
		Stream<Customer> customerStream = AON.getCustomerStream(domainName, domain, user, f -> f.getStatusProperty().eq((byte) 0).and(f.getDomainProperty().eq(domain)));
		
		for(Customer customer : customerStream.toList()) {
			
			// Si no tiene cuota se obvia
			
//			LinkedList<Fee> feeList = AON.getFeeList(domainName, domain, user, new CustomerFeeParams().setCustomer(customer.getId()).setOffset(0).setLimit(1));
			Stream<Fee> feeStream = AON.getFeeStream(domainName, domain, user, f -> f.getCustomerProperty().eq(customer.getId()).and(f.getDomainProperty().eq(domain)));
			if(feeStream.toList().isEmpty()) continue;
			
			// Si tiene agente de soporte asociado
			List<RegistrySeller> rseller = AON.getRegistrySellerStream(
					new Domain().setName(domainName).setId(domain), 
					user,
					f -> f.getRegistryProperty().eq(customer.getId())
						.and(f.getTypeProperty().eq((byte)1))
						.and(f.getStatusProperty().eq((byte)0))
						.and(f.getStartDateProperty().le(new Date(new java.util.Date().getTime())))
						.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(new Date(new java.util.Date().getTime()))))
						.and(f.getDomainProperty().eq(domain))
			).toList();
			if(!rseller.isEmpty()) continue;
			
			customers.add(customer);
		}
		
		return customers;
	}
	@Override
	public Account createRegistryAccount(String domainName, Integer domainId, String user, String registryName, String registryAlias, RegistrySource registrySource) {
		return AON.createRegistryAccount(domainName, domainId, user, registryName, registryAlias, registrySource);
	}
	
}
