package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CompanyFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
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

public interface IRegistry {
	
	public Category getCategory(AONContext ctx, Integer categoryId);
	public LinkedList<Category> getCategoryList(AONContext ctx);
	public Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter);
	public Category insertCategory(AONContext ctx, Category category);
	public Category updateCategory(AONContext ctx, Category category);
	public Category deleteCategory(AONContext ctx, Integer categoryId);
	
	
	public Stream<Creditor> getBasicCreditors(AONContext ctx, CreditorFilter filter);
	
	public Registry getRegistry(AONContext ctx, RegistryFilter filter);
	
	public Stream<RegistryMedia> getRMediaStream(AONContext ctx, RegistryMediaFilter filter);
	public Stream<RegistryNote> getRNoteStream(AONContext ctx, RegistryNoteFilter filter);

	public Stream<Segment> getRSegmentStream(AONContext ctx, Integer registryId);

	public Stream<Seller> getRSellerStream(AONContext ctx, RegistrySellerFilter registryId);

	public Stream<RAddress> getRAddressStream(AONContext ctx, RegistryAddressFilter filter);
	public RAddress insertRAddress(AONContext ctx, RAddress raddress);
	
	public Registry insertRegistry(AONContext ctx, Registry registry);
	public Registry updateRegistry(AONContext ctx, Registry registry);
	public Registry deleteRegistry(AONContext ctx, Integer registry);
	
	public RegistryMedia insertRMedia(AONContext ctx, RegistryMedia rmedia);
	public RegistryMedia updateRMedia(AONContext ctx, RegistryMedia rmedia);
	public RegistryMedia deleteRMedia(AONContext ctx, Integer registry);
	
	public Stream<Question> getRegistryQuestionStream(AONContext ctx, Integer registry);
	public Stream<RegistryProfile> getRegistryProfileStream(AONContext ctx, Integer registry, Integer question);

	// ------------------- CUSTOMER
	public Stream<Customer> getCustomerStream(AONContext ctx, CustomerFilter filter);
	public Customer insertCustomer(AONContext ctx, Customer customer);

	// ------------------- SELLER
	public Stream<Seller> getSellerStream(AONContext ctx, SellerFilter filter);
	
	// ------------------- CARRIER
	public Stream<Carrier> getCarrierStream(AONContext ctx, CarrierFilter filter);
	public Carrier insertCarrier(AONContext ctx, Carrier carrier);
	
	// ------------------- SUPPLIER
	public Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter);
	public Supplier insertSupplier(AONContext ctx, Supplier supplier);

	// ------------------- TARGET
	public Stream<Target> getTargetStream(AONContext ctx, TargetFilter filter);
	public Target insertTarget(AONContext ctx, Target target);


	// ------------------- RECORD DATA
	public Stream<RecordData> getRecordDataStream(AONContext ctx, RecordDataFilter filter);

	// ------------------- COMPANY
	public Stream<Company> getUserCompanyStream(AONContext ctx, Integer[] scopes);
	public Stream<Company> getUserCompanyStream(AONContext ctx, Integer[] scopes, CompanyFilter filter);
	public Stream<Company> getCompanyStream(AONContext ctx, CompanyFilter filter);
	
	public Stream<RegistryItem> getRItemStream(AONContext ctx, RegistryItemFilter filter);

	// ------------------- PERSON
	public Stream<Person> getPersonStream(AONContext ctx, PersonFilter filter);
	
	// ------------------- RBANK
	
	public Stream<RegistryBank> getRBankStream(AONContext ctx, RegistryBankFilter filter);
	public RegistryBank insertRBank(AONContext ctx, RegistryBank rbank);
	public RegistryBank updateRBank(AONContext ctx, RegistryBank rbank);
	public RegistryBank deleteRBank(AONContext ctx, RegistryBankFilter filter);
	
	// ------------------- RPAYMETHOD
	
	public Stream<RegistryPayMethod> getRPayMethodStream(AONContext ctx, RegistryPayMethodFilter filter);
	public RegistryPayMethod insertRPayMethod(AONContext ctx, RegistryPayMethod rpaymethod);
	public RegistryPayMethod updateRPayMethod(AONContext ctx, RegistryPayMethod rpaymethod);
	public RegistryPayMethod deleteRPayMethod(AONContext ctx, RegistryPayMethodFilter filter);

	// ------------------- RADDINFO
	
	public Stream<RegistryAddInfo> getRegistryAddInfoStream(AONContext ctx, RegistryAddInfoFilter filter);
	public RegistryAddInfo insertRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo);
	public RegistryAddInfo updateRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo);

}
