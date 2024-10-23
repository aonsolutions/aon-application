package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
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
import com.esferalia.aon.occam.api.model.Filter.GeoZoneFilter;
import com.esferalia.aon.occam.api.model.Filter.NewsletterFilter;
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
import com.esferalia.aon.occam.api.model.Filter.SegmentFilter;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Filter.SurveyFilter;
import com.esferalia.aon.occam.api.model.Filter.TargetFilter;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.occam.api.model.Newsletter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.CustomerParams;
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
import com.esferalia.aon.occam.api.model.registry.RegistrySegment;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.RegistryType;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
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
	public Stream<Registry> getRegistryStream(AONContext ctx, RegistryFilter filter);
	public Stream<Registry> getAonRegistryStream(AONContext ctx, RegistryFilter filter);
	
	// ----- RNOTE

	public Stream<RegistryNote> getRegistryNoteStream(AONContext ctx, RegistryNoteFilter filter);
	public List<RegistryNote> getRegistryNoteList(AONContext ctx, RegistryNoteFilter filter);
	public RegistryNote getRegistryNote(AONContext ctx, RegistryNoteFilter filter);
	public RegistryNote saveRegistryNote(AONContext ctx, RegistryNote rnote);
	public void deleteRegistryNote(AONContext ctx, Integer id);

	public Stream<Segment> getRSegmentStream(AONContext ctx, Integer registryId);
	public Integer[] getRSegmentStream(AONContext ctx, RegistrySegmentFilter filter);
	public Stream<RegistrySegment> getRegistrySegmentStream(AONContext ctx, RegistrySegmentFilter filter);
	public RegistrySegment saveRegistrySegment(AONContext ctx, RegistrySegment rsegment);
	public Stream<Segment> getSegmentStream(AONContext ctx, SegmentFilter filter);

	public Stream<Seller> getRSellerStream(AONContext ctx, RegistrySellerFilter registryId);

	public Stream<com.esferalia.aon.occam.api.model.registry.Question> getRegistryQuestionStream(AONContext ctx, Integer registry);
	public Stream<RegistryProfile> getRegistryProfileStream(AONContext ctx, Integer registry, Integer question);

	
	// ------------------- REGISTRY
	public Registry save(AONContext ctx, Registry registry);
	public void deleteRegistry(AONContext ctx, Integer registry);
	
	// ------------------- REGISTRY ADDRESS
	public Stream<RAddress> getRAddressStream(AONContext ctx, RegistryAddressFilter filter);
	public RAddress insertRAddress(AONContext ctx, RAddress raddress);

	public RegistryAddress get(AONContext ctx, RegistryAddressFilter filter);
	public RegistryAddress getMain(AONContext ctx, Integer registry);
	public Stream<RegistryAddress> getStream(AONContext ctx, RegistryAddressFilter filter);
	public RegistryAddress save(AONContext ctx, RegistryAddress registryAddress);
	public void deleteRegistryAddress(AONContext ctx, Integer id);
	
	// ------------------- REGISTRY MEDIA
	public Stream<RegistryMedia> getRMediaStream(AONContext ctx, RegistryMediaFilter filter);
	public Stream<RegistryMedia> getStream(AONContext ctx, RegistryMediaFilter filter);
	public RegistryMedia get(AONContext ctx, RegistryMediaFilter filter);
	public RegistryMedia save(AONContext ctx, RegistryMedia media);
	public RegistryMedia deleteRMedia(AONContext ctx, Integer registry);
	public void deleteRegistryMedia(AONContext ctx, Integer id);

	// ------------------- CUSTOMER
	public Stream<Customer> getCustomerStream(AONContext ctx, CustomerFilter filter);
	public Customer insertCustomer(AONContext ctx, Customer customer);
	public Customer saveCustomer(AONContext ctx, Customer customer);
	
	// ------------------- SELLER
	public Stream<Seller> getSellerStream(AONContext ctx, SellerFilter filter);
	public Stream<Seller> getSellerStream(AONContext ctx, SellerFilter filter, int offset, int limit);

	public List<Seller> getSellerList(CloseableAONContext ctx, SellerParams params);
	public Integer getSellerListCount(CloseableAONContext ctx, SellerParams params);
	public Seller saveSeller(CloseableAONContext ctx, Seller seller);
	public void deleteSeller(CloseableAONContext ctx, Integer sellerId);
	
	// ------------------- SELLER WORKLOAD
	public List<SellerWorkload> getSellerWorkloadList(CloseableAONContext ctx, SellerWorkloadParams params);
	public Integer getSellerWorkloadListCount(CloseableAONContext ctx, SellerWorkloadParams params);
	
	// ------------------- RSELLER
	public RegistrySeller getRegistrySeller(AONContext ctx, RegistrySellerFilter filter);
	public Stream<RegistrySeller> getRegistrySellerStream(AONContext ctx, RegistrySellerFilter filter);
	public Stream<RegistrySeller> getRegistrySellerStream(AONContext ctx, RegistrySellerFilter filter, int offset, int limit);
	public RegistrySeller saveRegistrySeller(AONContext ctx, RegistrySeller registrySeller);
	public int deleteRegistrySeller(AONContext ctx, RegistrySellerFilter filter);
	
	// ------------------- CARRIER
	public Carrier getCarrier(AONContext ctx, CarrierFilter filter, Options...options);
	public Stream<Carrier> getCarrierStream(AONContext ctx, CarrierFilter filter, Options...options);
	public Carrier saveCarrier(AONContext ctx, Carrier carrier);
	public void deleteCarrier(AONContext ctx, Integer id);
	
	// ------------------- SUPPLIER
	public Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter);
	public Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter, int offset, int limit);
	public Supplier saveSupplier(AONContext ctx, Supplier supplier);
	public Supplier insertSupplier(AONContext ctx, Supplier supplier);

	// ------------------- CREDITOR
	public Stream<Creditor> getCreditorStream(AONContext ctx, CreditorFilter filter);
	public Stream<Creditor> getCreditorStream(AONContext ctx, CreditorFilter filter, int offset, int limit);
	public Creditor saveCreditor(AONContext ctx, Creditor creditor);
	public Creditor insertCreditor(AONContext ctx, Creditor creditor);

	// ------------------- TARGET
	public Stream<Target> getTargetStream(AONContext ctx, TargetFilter filter);
	public Stream<Target> getTargetStream(AONContext ctx, TargetFilter filter, int ofs, int limit);
	public Target save(AONContext ctx, Target target);


	// ------------------- RECORD DATA
	public Stream<RecordData> getRecordDataStream(AONContext ctx, RecordDataFilter filter);
	public RecordData saveRecordData(AONContext ctx, RecordData recordData);

	// ------------------- COMPANY
	public Stream<Company> getUserCompanyStream(AONContext ctx, Integer[] scopes);
	public Stream<Company> getUserCompanyStream(AONContext ctx, Integer[] scopes, CompanyFilter filter);
	public CompanyFull getCompanyFull(AONContext ctx, Integer domain);
	public Stream<Company> getCompanyStream(AONContext ctx, CompanyFilter filter);
	public Stream<Company> getCompanyStream(AONContext ctx, CompanyFilter filter, Integer page, Integer perPage);
	public Stream<AonCompany> getCompanyStream(AONContext ctx, byte[] auth, Integer page, Integer perPage);
	public Stream<AonCompany> getCompanyStream(AONContext ctx, byte[] auth, CompanyFilter filter, Integer page, Integer perPage);
	public Company saveCompany(AONContext ctx, Company company);
	public Stream<RegistryItem> getRItemStream(AONContext ctx, RegistryItemFilter filter);
	public Stream<RegistryItem> getRItemStream(AONContext ctx, RegistryItemFilter filter, int limit, int offset);

	// ------------------- PERSON
	public Stream<Person> getPersonStream(AONContext ctx, PersonFilter filter);
	
	// ------------------- RBANK
	
	public Stream<RegistryBank> getRBankStream(AONContext ctx, RegistryBankFilter filter);
	public RegistryBank insertRBank(AONContext ctx, RegistryBank rbank);
	public RegistryBank updateRBank(AONContext ctx, RegistryBank rbank);
	public void deleteRBank(AONContext ctx, Integer id);
	
	public RegistryBank getRegistryBank(AONContext ctx, RegistryBankFilter filter);
	public Stream<RegistryBank> getRegistryBankStream(AONContext ctx, RegistryBankFilter filter);
	public RegistryBank saveRegistryBank(AONContext ctx, RegistryBank rbank);
	public void deleteRegistryBank(AONContext ctx, Integer id);
	
	// ------------------- RPAYMETHOD
	public RegistryPayMethod getRegistryPayMethod(AONContext ctx, RegistryPayMethodFilter filter, Options...options);
	public Stream<RegistryPayMethod> getRegistryPayMethodStream(AONContext ctx, RegistryPayMethodFilter filter);
	public void deleteRegistryPayMethod(AONContext ctx, Integer id);
	public RegistryPayMethod saveRegistryPayMethod(AONContext ctx, RegistryPayMethod rpaymethod);

	// ------------------- RADDINFO
	
	public Stream<RegistryAddInfo> getRegistryAddInfoStream(AONContext ctx, RegistryAddInfoFilter filter);
	public RegistryAddInfo insertRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo);
	public RegistryAddInfo updateRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo);
	public void deleteRegistryAddInfo(AONContext ctx, Integer raddinfoId);
	public RegistryAddInfo saveRegistryAddInfo(AONContext ctx, RegistryAddInfo registryAddInfo);
	public List<String> getRAddInfoAviableAttributes(CloseableAONContext ctx, RegistryAddInfoFilter filter);
	
	// ------------------- RDIRSTAFF
	
	public Stream<RDirStaff> getRDirStaffStream(AONContext ctx, RDirStaffFilter filter);
	public RDirStaff insertRDirStaff(AONContext ctx, RDirStaff rdirstaff);

	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************
	public Stream<Customer> getCustomers(AONContext ctx, CustomerFilter filter, int ofs, int limit);
	public CustomerFull getCustomerFull(AONContext ctx, Integer id);
	public CustomerFull save(AONContext ctx, CustomerFull customerFull);
	public Domain getDomainLinked(AONContext ctx, Integer customerId);
	public List<Domain> getDomainOfficeLinked(AONContext ctx, String document);
	
	public List<Customer> getCustomerWithoutFee(AONContext ctx);
	public List<Customer> getCustomerWithoutFee(AONContext ctx, CustomerParams customerParams);
	
	// **************************************************
	// *************************************** [CREDITOR]
	// **************************************************
	public Stream<Creditor> getCreditors(AONContext ctx, CreditorFilter filter, int ofs, int limit);
	public CreditorFull getCreditorFull(AONContext ctx, Integer id);
	public CreditorFull save(AONContext ctx, CreditorFull creditorFull);

	// **************************************************
	// *************************************** [SUPPLIER]
	// **************************************************
	public Stream<Supplier> getSuppliers(AONContext ctx, SupplierFilter filter, int ofs, int limit);
	public SupplierFull getSupplierFull(AONContext ctx, Integer id);
	public SupplierFull save(AONContext ctx, SupplierFull supplierFull);

	// **************************************************
	// **************************** [REGISTRY SUGGESTION]
	// **************************************************
	public Stream<Registry> getSuggestionRegistries(AONContext ctx, LinkedList<RegistryType> list, RegistryFilter filter);
	public Stream<Registry> getGlobalSuggestionRegistries(RegistryFilter filter);

	// ------------------- DOMAIN LINKED
	public List<DomainLinked> getDomainLinkedList(AONContext ctx, Integer registry);
	public DomainLinked saveDomainLinked(AONContext ctx, DomainLinked domainLinked);
	
	// QUESTION
	public List<Question> getQuestionList(CloseableAONContext ctx, QuestionParams params);
	public void deleteQuestion(CloseableAONContext ctx, Integer id);
	public Question saveQuestion(CloseableAONContext ctx, Question question);
	public Question getQuestion(CloseableAONContext ctx, Integer id);
	
	// REGISTRY PROFILE
	public void saveRegistryProfile(CloseableAONContext ctx, Integer registryId, String questionAlias, String value);
	
	// MARKETING CAMPAIGN
	public List<MarketingCampaign> getMarketingCampaignlist(CloseableAONContext ctx, MarketingCompaignParams params);
	public void deleteMarketingCampaign(CloseableAONContext ctx, Integer id);
	public MarketingCampaign saveMarketingCampaign(CloseableAONContext ctx, MarketingCampaign marketingCampaign);
	public MarketingCampaign getMarketingCampaign(CloseableAONContext ctx, Integer id);
	
	// MARKETING ACTION
	public List<MarketingAction> getMarketingActions(CloseableAONContext ctx, MarketingActionParams params);
	public MarketingAction getMarketingAction(CloseableAONContext ctx, Integer id);
	public void deleteMarketingAction(CloseableAONContext ctx, Integer id);
	public MarketingAction saveMarketingAction(CloseableAONContext ctx, MarketingAction marketingAction);
	
	public Stream<Newsletter> getNewsletterStream(CloseableAONContext ctx, NewsletterFilter filter);
	public Stream<Survey> getSurveyStream(CloseableAONContext ctx, SurveyFilter filter);
	
	// MARKETING ACTION TARGET
	public List<MarketingActionTarget> getMarketingActionTargets(CloseableAONContext ctx, MarketingActionTargetParams params);
	public void deleteMarketingActionTarget(CloseableAONContext ctx, Integer id);
	public MarketingActionTarget saveMarketingActionTarget(CloseableAONContext ctx, MarketingActionTarget marketingActionTarget);
	
	public Stream<GeoZone> geozoneStream(CloseableAONContext ctx, GeoZoneFilter filter);

}
