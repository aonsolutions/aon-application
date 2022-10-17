package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IRegistry;
import com.esferalia.aon.occam.api.model.AonCompany;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CompanyFilter;
import com.esferalia.aon.occam.api.model.Filter.CreditorFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
import com.esferalia.aon.occam.api.model.Filter.RDirStaffFilter;
import com.esferalia.aon.occam.api.model.Filter.RecordDataFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryBankFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryNoteFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryPayMethodFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistrySegmentFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistrySellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Filter.TargetFilter;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Question;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistryProfile;
import com.esferalia.aon.occam.api.model.registry.RegistryType;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainLinkedDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RDirStaffDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryBankDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryNoteDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryOldDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryPayMethodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistrySuggestionDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TargetDAO;

public class RegistryImpl implements IRegistry{
	
	// ------------------------------------- CATEGORY
	@Deprecated 
	@Override
	public Category getCategory(AONContext ctx, Integer categoryId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getCategory(ctx, categoryId));
	}

	@Deprecated
	@Override
	public Category insertCategory(AONContext ctx, Category category) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.insertCategory(ctx, category));
	}
	
	@Deprecated
	@Override
	public Category updateCategory(AONContext ctx, Category category) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.updateCategory(ctx, category));
	}
	@Deprecated
	@Override
	public Category deleteCategory(AONContext ctx, Integer categoryId) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.deleteCategory(ctx, categoryId));
	}
	
	@Deprecated
	@Override
	public LinkedList<Category> getCategoryList(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getCategoryList(ctx));
	}
	
	@Deprecated
	@Override
	public Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getCategoryStream(ctx, filter));
	}

	// ------------------------------------- CREDITOR

	
	@Override
	public Stream<Creditor> getCreditorStream(AONContext ctx, CreditorFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CreditorDAO.getStream(ctx, filter));
	}
	
	@Override
	public Stream<Creditor> getCreditorStream(AONContext ctx, CreditorFilter filter, int offset, int limit) {
		return ctx.getDslContext().transactionResult(
				configuration -> CreditorDAO.getStream(ctx, filter, offset, limit));
	}

	@Override
	public Creditor saveCreditor(AONContext ctx, Creditor creditor) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CreditorDAO.save(ctx, creditor));
	}
	
	@Override
	@Deprecated
	public Creditor insertCreditor(AONContext ctx, Creditor creditor) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.insertCreditor(ctx, creditor));
	}
	
	@Override
	public Stream<Creditor> getBasicCreditors(AONContext ctx, CreditorFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CreditorDAO.getBasicCreditors(ctx, filter));
	}

	@Override
	public Registry getRegistry(AONContext ctx, RegistryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRegistry(ctx, filter));
	}
	
	@Override
	public Stream<Registry> getRegistryStream(AONContext ctx, RegistryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRegistryStream(ctx, filter));
	}
	
	@Override
	public Stream<Registry> getAonRegistryStream(AONContext ctx, RegistryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getAonRegistryStream(ctx, filter));
	}
	
	// ------------------------------------- REGISTRY MEDIA (RMEDIA)

	@Override
	public Stream<RegistryMedia> getRMediaStream(AONContext ctx, RegistryMediaFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRMediaStream(ctx, filter));
	}
	
	@Override
	public Stream<RegistryMedia> getStream(AONContext ctx, RegistryMediaFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryMediaDAO.getStream(ctx, filter));
	}
	
	@Override
	public RegistryMedia get(AONContext ctx, RegistryMediaFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryMediaDAO.get(ctx, filter));
	}

	@Override
	public RegistryMedia deleteRMedia(AONContext ctx, Integer registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.deleteRMedia(ctx, registry));
	}
	
	@Override
	public RegistryMedia save(AONContext ctx, RegistryMedia rmedia) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryMediaDAO.save(ctx, rmedia));
	}
	
	// ------------------------------------- RNOTE

	@Override
	public RegistryNote getRegistryNote(AONContext ctx, RegistryNoteFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryNoteDAO.get(ctx, filter));
	}
	
	@Override
	public Stream<RegistryNote> getRegistryNoteStream(AONContext ctx, RegistryNoteFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryNoteDAO.getStream(ctx, filter));
	}
	
	@Override
	public List<RegistryNote> getRegistryNoteList(AONContext ctx, RegistryNoteFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryNoteDAO.getList(ctx, filter));
	}
	
	@Override
	public RegistryNote saveRegistryNote(AONContext ctx, RegistryNote rnote) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryNoteDAO.save(ctx, rnote));
	}
	
	@Override
	public void deleteRegistryNote(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
			configuration -> RegistryNoteDAO.delete(ctx, id));
	}
	
	@Override
	public Stream<Segment> getRSegmentStream(AONContext ctx, Integer registryId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRSegmentStream(ctx, registryId));
	}
	
	@Override
	public Integer[] getRSegmentStream(AONContext ctx, RegistrySegmentFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRSegmentStream(ctx, filter));
	}

	@Override
	public Stream<Seller> getRSellerStream(AONContext ctx, RegistrySellerFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRSellerStream(ctx, filter));
	}

	@Override
	public Registry save(AONContext ctx, Registry registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.save(ctx, registry));
	}

	@Override
	public void deleteRegistry(AONContext ctx, Integer registry) {
		ctx.getDslContext().transaction(
				configuration -> RegistryDAO.delete(ctx, registry));
	}


	@Override
	public Stream<Question> getRegistryQuestionStream(AONContext ctx, Integer registry) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRegistryQuestionStream(ctx, registry));
	}

	@Override
	public Stream<RegistryProfile> getRegistryProfileStream(AONContext ctx, Integer registry, Integer question) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRegistryProfileStream(ctx, registry, question));
	}
	
	// -------------------- CUSTOMER
	
	@Override
	public Stream<Customer> getCustomerStream(AONContext ctx, CustomerFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CustomerDAO.getStream(ctx, filter));
	}
	
	public Customer saveCustomer(AONContext ctx, Customer customer) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CustomerDAO.save(ctx, customer));
	}
	
	@Override
	public Customer insertCustomer(AONContext ctx, Customer customer) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.insertCustomer(ctx, customer));
	}
	
	// -------------------- SELLER
	
	@Override
	public Stream<Seller> getSellerStream(AONContext ctx, SellerFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> SellerDAO.getStream(ctx, filter));
	}
	
	// -------------------- CARRIER
	
	@Override
	public Stream<Carrier> getCarrierStream(AONContext ctx, CarrierFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getCarrierStream(ctx, filter));
	}
	
	@Override
	public Carrier insertCarrier(AONContext ctx, Carrier carrier) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.insertCarrier(ctx, carrier));
	}
	
	// -------------------- RECORD DATA
	
	@Override
	public Stream<RecordData> getRecordDataStream(AONContext ctx, RecordDataFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRecordDataStream(ctx, filter));
	}
	
	@Override
	public RecordData saveRecordData(AONContext ctx, RecordData recordData) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.saveRecordData(ctx, recordData));
	}
	
	// -------------------- Company
	
	@Override
	public Stream<Company> getUserCompanyStream(AONContext ctx, Integer[] scopes) {
		return ctx.getDslContext().transactionResult(
				configuration -> CompanyDAO.getUserCompanyStream(ctx, scopes));
	}

	@Override
	public Stream<Company> getUserCompanyStream(AONContext ctx, Integer[] scopes, CompanyFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CompanyDAO.getUserCompanyStream(ctx, scopes, filter));
	}


	@Override
	public CompanyFull getCompanyFull(AONContext ctx, Integer domain) {
		return ctx.getDslContext().transactionResult(
				configuration -> CompanyDAO.getFull(ctx, domain));
	}
	
	@Override
	public Stream<Company> getCompanyStream(AONContext ctx, CompanyFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CompanyDAO.getCompanyStream(ctx, filter));
	}
	
	@Override
	public Stream<Company> getCompanyStream(AONContext ctx, CompanyFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(
				configuration -> CompanyDAO.getStream(ctx, filter, page, perPage));
	}
	
	@Override
	public Stream<AonCompany> getCompanyStream(AONContext ctx, byte[] auth, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(
				configuration -> CompanyDAO.getCompanyStream(ctx, auth, page, perPage));
	}
	
	@Override
	public Company saveCompany(AONContext ctx, Company company) {
		return ctx.getDslContext().transactionResult(
				configuration -> CompanyDAO.save(ctx, company));
	}
	
	// ------------------------------------- RNOTE
	
	@Override
	public Stream<RegistryItem> getRItemStream(AONContext ctx, RegistryItemFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRItemStream(ctx, filter));
	}

	// -------------------- SUPPLIER

	@Override
	public Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> SupplierDAO.getStream(ctx, filter));
	}
	
	@Override
	public Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter, int offset, int limit) {
		return ctx.getDslContext().transactionResult(
				configuration -> SupplierDAO.getStream(ctx, filter, offset, limit));
	}
	
	@Override
	@Deprecated
	public Supplier insertSupplier(AONContext ctx, Supplier supplier) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.insertSupplier(ctx, supplier));
	}

	@Override
	public Supplier saveSupplier(AONContext ctx, Supplier supplier) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> SupplierDAO.save(ctx, supplier));
	}
	
	// -------------------- TARGET

	@Override
	public Stream<Target> getTargetStream(AONContext ctx, TargetFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TargetDAO.getStream(ctx, filter));
	}
	
	@Override
	public Target save(AONContext ctx, Target target) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TargetDAO.save(ctx, target));
	}
	
	// -------------------- PERSON
	
	@Override
	public Stream<Person> getPersonStream(AONContext ctx, PersonFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getPersonStream(ctx, filter));
	}
	
	// -------------------- RBANK

	
	
	@Override
	public Stream<RegistryBank> getRBankStream(AONContext ctx, RegistryBankFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRBankStream(ctx, filter));
	}

	@Override
	public RegistryBank insertRBank(AONContext ctx, RegistryBank rbank) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.insertRBank(ctx, rbank));
	}

	@Override
	public RegistryBank updateRBank(AONContext ctx, RegistryBank rbank) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.updateRBank(ctx, rbank));
	}

	@Override
	public void deleteRBank(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> RegistryOldDAO.deleteRBank(ctx, id));
	}
	

	@Override
	public RegistryBank getRegistryBank(AONContext ctx, RegistryBankFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryBankDAO.get(ctx, filter));
	}

	@Override
	public Stream<RegistryBank> getRegistryBankStream(AONContext ctx, RegistryBankFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryBankDAO.getStream(ctx, filter));
	}

	@Override
	public RegistryBank saveRegistryBank(AONContext ctx, RegistryBank rbank) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryBankDAO.save(ctx, rbank));
	}

	@Override
	public void deleteRegistryBank(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> RegistryBankDAO.delete(ctx, id));
	}

	// -------------------- RPAYMETHOD
	
	@Override
	public RegistryPayMethod getRegistryPayMethod(AONContext ctx, RegistryPayMethodFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryPayMethodDAO.get(ctx, filter));
	}
	
	@Override
	public Stream<RegistryPayMethod> getRegistryPayMethodStream(AONContext ctx, RegistryPayMethodFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryPayMethodDAO.getStream(ctx, filter));
	}
	

	@Override
	public void deleteRegistryPayMethod(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> RegistryPayMethodDAO.delete(ctx, id));
	}

	@Override
	public RegistryPayMethod saveRegistryPayMethod(AONContext ctx, RegistryPayMethod rpaymethod) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryPayMethodDAO.save(ctx, rpaymethod));
	}
	
	// -------------------- REGISTRY ADD INFO

	@Override
	public Stream<RegistryAddInfo> getRegistryAddInfoStream(AONContext ctx, RegistryAddInfoFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRegistryAddInfoStream(ctx, filter));
	}
	
	@Override
	public RegistryAddInfo insertRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.insertRegistryAddInfo(ctx, raddinfo));
	}
	
	@Override
	public RegistryAddInfo updateRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.updateRegistryAddInfo(ctx, raddinfo));
	}

	@Override
	public void deleteRegistryAddInfo(AONContext ctx, Integer raddinfoId) {
		ctx.getDslContext().transaction(configuration -> RegistryOldDAO.deleteRegistryAddInfo(ctx, raddinfoId));
	}

	
	// -------------------- RDIRSTAFF

	@Override
	public Stream<RDirStaff> getRDirStaffStream(AONContext ctx, RDirStaffFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RDirStaffDAO.getStream(ctx, filter));
	}
	
	@Override
	public RDirStaff insertRDirStaff(AONContext ctx, RDirStaff rdirstaff) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RDirStaffDAO.save(ctx, rdirstaff));
	}

	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************
	@Override
	public Stream<Customer> getCustomers(AONContext ctx, CustomerFilter filter, int ofs, int limit) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CustomerDAO.getStream(ctx, filter, ofs, limit));
	}
	@Override
	public CustomerFull getCustomerFull(AONContext ctx, Integer id) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CustomerDAO.getFull(ctx, id));
	}

	@Override
	public CustomerFull save(AONContext ctx, CustomerFull customerFull) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CustomerDAO.save(ctx, customerFull));
	}

	@Override
	public Domain getDomainLinked(AONContext ctx, Integer customerId) {
		return ctx.getDslContext().transactionResult(
				configuration -> CustomerDAO.getDomainLinked(ctx, customerId));
	}
	
	@Override
	public List<Domain> getDomainOfficeLinked(AONContext ctx, String document) {
		return ctx.getDslContext().transactionResult(
				configuration -> CustomerDAO.getDomainOfficeLinked(ctx, document));
	}
	
	// **************************************************
	// *************************************** [CREDITOR]
	// **************************************************
	@Override
	public Stream<Creditor> getCreditors(AONContext ctx, CreditorFilter filter, int ofs, int limit) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CreditorDAO.getStream(ctx, filter, ofs, limit));
	}
	@Override
	public CreditorFull getCreditorFull(AONContext ctx, Integer id) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CreditorDAO.getFull(ctx, id));
	}

	@Override
	public CreditorFull save(AONContext ctx, CreditorFull creditorFull) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CreditorDAO.save(ctx, creditorFull));
	}

	// **************************************************
	// *************************************** [SUPPLIER]
	// **************************************************
	@Override
	public Stream<Supplier> getSuppliers(AONContext ctx, SupplierFilter filter, int ofs, int limit) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> SupplierDAO.getStream(ctx, filter, ofs, limit));
	}
	@Override
	public SupplierFull getSupplierFull(AONContext ctx, Integer id) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> SupplierDAO.getFull(ctx, id));
	}

	@Override
	public SupplierFull save(AONContext ctx, SupplierFull supplierFull) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> SupplierDAO.save(ctx, supplierFull));
	}

	// **************************************************
	// **************************** [REGISTRY SUGGESTION]
	// **************************************************
	@Override
	public Stream<Registry> getSuggestionRegistries(AONContext ctx, LinkedList<RegistryType> list,
			RegistryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistrySuggestionDAO.getSuggestionRegistries(ctx, list, filter));
	}
	
	@Override
	public Stream<Registry> getGlobalSuggestionRegistries(RegistryFilter filter) {
		return 	RegistrySuggestionDAO.getGlobalSuggestionRegistries(filter);
	}
	
	
	// **************************************************
	// **************************** [REGISTRY ADDRESS]
	// **************************************************
	
	@Override
	public Stream<RAddress> getRAddressStream(AONContext ctx, RegistryAddressFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.getRAddressStream(ctx, filter));
	}
	
	@Override
	public RAddress insertRAddress(AONContext ctx, RAddress raddress) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryOldDAO.insertRAddress(ctx, raddress));
	}

	@Override
	public RegistryAddress get(AONContext ctx, RegistryAddressFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryAddressDAO.get(ctx, filter));
	}

	@Override
	public RegistryAddress getMain(AONContext ctx, Integer registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryAddressDAO.getMain(ctx, registry));
	}

	@Override
	public Stream<RegistryAddress> getStream(AONContext ctx, RegistryAddressFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryAddressDAO.getStream(ctx, filter));
	}

	@Override
	public RegistryAddress save(AONContext ctx, RegistryAddress registryAddress) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryAddressDAO.save(ctx, registryAddress));
	}

	@Override
	public List<DomainLinked> getDomainLinkedList(AONContext ctx, Integer registry) {
		return ctx.getDslContext().transactionResult(
				configuration -> DomainLinkedDAO.getList(ctx, registry));	
	}

	@Override
	public DomainLinked saveDomainLinked(AONContext ctx, DomainLinked domainLinked) {
		return ctx.getDslContext().transactionResult(
				configuration -> DomainLinkedDAO.save(ctx, domainLinked));
	}

}
