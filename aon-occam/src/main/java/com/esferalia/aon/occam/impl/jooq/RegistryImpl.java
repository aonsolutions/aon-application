package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IRegistry;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CompanyFilter;
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
import com.esferalia.aon.occam.api.model.Filter.RegistrySellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Filter.TargetFilter;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.api.model.registry.Question;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistryProfile;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;

public class RegistryImpl implements IRegistry{
	
	// ------------------------------------- CATEGORY
	
	@Override
	public Category getCategory(AONContext ctx, Integer categoryId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCategory(ctx, categoryId));
	}

	@Override
	public Category insertCategory(AONContext ctx, Category category) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertCategory(ctx, category));
	}
	
	@Override
	public Category updateCategory(AONContext ctx, Category category) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.updateCategory(ctx, category));
	}

	@Override
	public Category deleteCategory(AONContext ctx, Integer categoryId) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.deleteCategory(ctx, categoryId));
	}
	
	@Override
	public LinkedList<Category> getCategoryList(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCategoryList(ctx));
	}
	
	@Override
	public Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCategoryStream(ctx, filter));
	}

	// ------------------------------------- CREDITOR

	@Override
	public Stream<Creditor> getBasicCreditors(AONContext ctx, CreditorFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CreditorDAO.getBasicCreditors(ctx, filter));
	}

	@Override
	public Registry getRegistry(AONContext ctx, RegistryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRegistry(ctx, filter));
	}
	
	// ------------------------------------- RMEDIA

	@Override
	public Stream<RegistryMedia> getRMediaStream(AONContext ctx, RegistryMediaFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRMediaStream(ctx, filter));
	}

	// ------------------------------------- RNOTE
	
	@Override
	public Stream<RegistryNote> getRNoteStream(AONContext ctx, RegistryNoteFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRNoteStream(ctx, filter));
	}

	@Override
	public Stream<Segment> getRSegmentStream(AONContext ctx, Integer registryId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRSegmentStream(ctx, registryId));
	}

	@Override
	public Stream<Seller> getRSellerStream(AONContext ctx, RegistrySellerFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRSellerStream(ctx, filter));
	}

	@Override
	public Stream<RAddress> getRAddressStream(AONContext ctx, RegistryAddressFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRAddressStream(ctx, filter));
	}
	
	@Override
	public RAddress insertRAddress(AONContext ctx, RAddress raddress) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertRAddress(ctx, raddress));
	}

	@Override
	public Registry insertRegistry(AONContext ctx, Registry registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertRegistry(ctx, registry));
	}

	@Override
	public Registry updateRegistry(AONContext ctx, Registry registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.updateRegistry(ctx, registry));
	}

	@Override
	public Registry deleteRegistry(AONContext ctx, Integer registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.deleteRegistry(ctx, registry));
	}

	@Override
	public RegistryMedia insertRMedia(AONContext ctx, RegistryMedia rmedia) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertRMedia(ctx, rmedia));
	}

	@Override
	public RegistryMedia updateRMedia(AONContext ctx, RegistryMedia rmedia) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.updateRMedia(ctx, rmedia));
	}

	@Override
	public RegistryMedia deleteRMedia(AONContext ctx, Integer registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.deleteRMedia(ctx, registry));

	}

	@Override
	public Stream<Question> getRegistryQuestionStream(AONContext ctx, Integer registry) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRegistryQuestionStream(ctx, registry));
	}

	@Override
	public Stream<RegistryProfile> getRegistryProfileStream(AONContext ctx, Integer registry, Integer question) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRegistryProfileStream(ctx, registry, question));
	}
	
	// -------------------- CUSTOMER
	
	@Override
	public Stream<Customer> getCustomerStream(AONContext ctx, CustomerFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCustomerStream(ctx, filter));
	}
	
	@Override
	public Customer insertCustomer(AONContext ctx, Customer customer) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertCustomer(ctx, customer));
	}
	
	// -------------------- SELLER
	
	@Override
	public Stream<Seller> getSellerStream(AONContext ctx, SellerFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getSellerStream(ctx, filter));
	}
	
	// -------------------- CARRIER
	
	@Override
	public Stream<Carrier> getCarrierStream(AONContext ctx, CarrierFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCarrierStream(ctx, filter));
	}
	
	@Override
	public Carrier insertCarrier(AONContext ctx, Carrier carrier) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertCarrier(ctx, carrier));
	}
	
	// -------------------- RECORD DATA
	
	@Override
	public Stream<RecordData> getRecordDataStream(AONContext ctx, RecordDataFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRecordDataStream(ctx, filter));
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
	public Stream<Company> getCompanyStream(AONContext ctx, CompanyFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CompanyDAO.getCompanyStream(ctx, filter));
	}
	
	// ------------------------------------- RNOTE
	
	@Override
	public Stream<RegistryItem> getRItemStream(AONContext ctx, RegistryItemFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRItemStream(ctx, filter));
	}

	// -------------------- SUPPLIER

	@Override
	public Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getSupplierStream(ctx, filter));
	}
	
	@Override
	public Supplier insertSupplier(AONContext ctx, Supplier supplier) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertSupplier(ctx, supplier));
	}

	// -------------------- TARGET

	@Override
	public Stream<Target> getTargetStream(AONContext ctx, TargetFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getTargetStream(ctx, filter));
	}
	
	@Override
	public Target insertTarget(AONContext ctx, Target target) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertTarget(ctx, target));
	}
	
	// -------------------- PERSON
	
	@Override
	public Stream<Person> getPersonStream(AONContext ctx, PersonFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getPersonStream(ctx, filter));
	}
	
	// -------------------- RBANK

	@Override
	public Stream<RegistryBank> getRBankStream(AONContext ctx, RegistryBankFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRBankStream(ctx, filter));
	}

	@Override
	public RegistryBank insertRBank(AONContext ctx, RegistryBank rbank) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertRBank(ctx, rbank));
	}

	@Override
	public RegistryBank updateRBank(AONContext ctx, RegistryBank rbank) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.updateRBank(ctx, rbank));
	}

	@Override
	public RegistryBank deleteRBank(AONContext ctx, RegistryBankFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.deleteRBank(ctx, filter));
	}

	// -------------------- RPAYMETHOD
	
	@Override
	public Stream<RegistryPayMethod> getRPayMethodStream(AONContext ctx, RegistryPayMethodFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRPayMethodStream(ctx, filter));
	}

	@Override
	public RegistryPayMethod insertRPayMethod(AONContext ctx, RegistryPayMethod rpaymethod) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertRPayMethod(ctx, rpaymethod));
	}

	@Override
	public RegistryPayMethod updateRPayMethod(AONContext ctx, RegistryPayMethod rpaymethod) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.updateRPayMethod(ctx, rpaymethod));
	}

	@Override
	public RegistryPayMethod deleteRPayMethod(AONContext ctx, RegistryPayMethodFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.deleteRPayMethod(ctx, filter));
	}


	@Override
	public Stream<RegistryAddInfo> getRegistryAddInfoStream(AONContext ctx, RegistryAddInfoFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRegistryAddInfoStream(ctx, filter));
	}
	
	@Override
	public RegistryAddInfo insertRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertRegistryAddInfo(ctx, raddinfo));
	}
	
	@Override
	public RegistryAddInfo updateRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.updateRegistryAddInfo(ctx, raddinfo));
	}

	
	// -------------------- RDIRSTAFF

	@Override
	public Stream<RDirStaff> getRDirStaffStream(AONContext ctx, RDirStaffFilter filter) {
		// TODO Auto-generated method stub
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRDirStaffStream(ctx, filter));
	}
}
