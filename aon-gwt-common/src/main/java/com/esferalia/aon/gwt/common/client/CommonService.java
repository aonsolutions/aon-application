package com.esferalia.aon.gwt.common.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.Cnae;
import com.esferalia.aon.occam.api.model.Cnae2009;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.EmployeeSegSocial;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetParams;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetMassiveParams;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.occam.api.model.Newsletter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.PayMethodParams;
import com.esferalia.aon.occam.api.model.ProjectParams;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.api.model.SecondaryUserCertificate;
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.TaskHolderParams;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryObject;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryParams;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.occam.api.model.customer.CustomersDomainSyncParams;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemAddInfo;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.DomainSigAddInfo;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.occam.api.model.registry.SellerWorkloadContent;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.registry.TargetFull;
import com.esferalia.aon.occam.api.model.sales.SalesParams;
import com.esferalia.aon.occam.api.model.scope.ScopeParams;
import com.esferalia.aon.occam.api.model.scope.UserScopeFull;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.tag.TagParams;
import com.esferalia.aon.occam.api.model.target.TargetParams;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffAddInfo;
import com.esferalia.aon.occam.api.model.tariff.TariffCatalogue;
import com.esferalia.aon.occam.api.model.tariff.TariffParams;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Common")
public interface CommonService extends RemoteService {

	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain, String user) throws AonCoreException;
	AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain, String user, Date atDate) throws AonCoreException;
	AonConfiguration getAonConfiguration(Occam occam) throws AonCoreException;
	AonConfiguration getAonConfiguration(Occam occam, ConfigParams params) throws AonCoreException;
	Domain getParentDomain(String domainName, int domain, String user, Integer id) throws AonCoreException;
	
	// **************************************************
	// *************************************** [SECURITY]
	// **************************************************
	User getCurrentUser(String domainName, int domain, String currentUser) throws AonCoreException;
	
	// **************************************************
	// ************************************* [ENTERPRISE]
	// **************************************************
//	LinkedList<Enterprise> getParentEnterprises(String domainName, int domain, String user,String query) throws AonCoreException;
	Enterprise getEnterprise(String domainName, int domain, String user, int id) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain, String user) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain, String user,int enterprise) throws AonCoreException;
	
	// **************************************************
	// **************************************** [ACCOUNT]
	// **************************************************
	Account getAccount(String domainName,int domain, String user,Integer id) throws AonCoreException;
	Account getAccount(String domainName,int domain, String user,String code) throws AonCoreException;
	LinkedList<Account> getAccounts(String domainName,int domain, String user,String query) throws AonCoreException;
	Account save(String domainName, int domain, String user, Account account) throws AonCoreException;
	Account delete(String domainName, int domain, String user, Account account) throws AonCoreException;
	String getAccountNextCode(String domainName, int domain, String user, String prefix);
	
	// **************************************************
	// ************************************* [PAY_METHOD]
	// **************************************************
	LinkedList<PayMethod> getPayMethods(String domainName, int domain, String user) throws AonCoreException;
	LinkedList<PayMethod> getPayMethods(PayMethodParams params) throws AonCoreException;
	PayMethod savePayMethod(String domainName,int domain, String user, PayMethod payMethod) throws AonCoreException;
	void deletePayMethod(String domainName,int domain, String user, Integer id) throws AonCoreException;
	void groupPayMethod(String domainName,int domain, String user, List<PayMethod> selectedPaymethodList, PayMethod groupedPaymthod) throws AonCoreException;

	// **************************************************
	// **************************************** [INVOICE]
	// **************************************************
	LinkedList<OldProduct> getInvoiceProducts(String domainName,int domain, String user,String query) throws AonCoreException;
	
	// **************************************************
	// *************************************** [REGISTRY]
	// **************************************************
	
	List<Customer> getCustomers(String domainName, int domain, String user, Integer account) throws AonCoreException;
	List<Supplier> getSuppliers(String domainName, int domain, String user, Integer account) throws AonCoreException;
	List<Creditor> getCreditors(String domainName, int domain, String user, Integer account) throws AonCoreException;
	
	CustomerFull getCustomer(String domainName, int domain, String user, Integer registry) throws AonCoreException;
	SupplierFull getSupplier(String domainName, int domain, String user, Integer registry) throws AonCoreException;
	CreditorFull getCreditor(String domainName, int domain, String user, Integer registry) throws AonCoreException;
	
	// **************************************************
	// ************************************ [COST CENTER]
	// **************************************************
	
	List<ApplicationParameter> getCostCenters(String domainName, int domain, String user) throws AonCoreException;
	void saveCostCenter(String domainName, int domain, String user, ApplicationParameter costCenter) throws AonCoreException;
	void deleteCostCenter(String domainName, int domain, String user, Integer id) throws AonCoreException;
	
	// **************************************************
	// *********************************** [INVEST ASSET]
	// **************************************************
	
	List<InvestAsset> getInvestAssets(InvestAssetParams params) throws AonCoreException;
	void deleteInvestAsset(String domainName, int domain, String user, Integer id) throws AonCoreException;
	InvestAsset saveInvestAsset(String domainName, int domain, String user, InvestAsset investAsset) throws AonCoreException;
	List<Activity> getActivities(String domainName, int domain, String user) throws AonCoreException;
	InvestAsset getInvestAsset(String domainName, int domain, String user, Integer id) throws AonCoreException;

	// **************************************************
	// ********************************* [LOAD PDF MODEL]
	// **************************************************
	
	void savePDFModel(Occam occam, IFiscalModel model, String data);	
	
	// **************************************************
	// *************************************** [QUESTION]
	// **************************************************
	
	List<Question> getQuestions(QuestionParams params) throws AonCoreException;
	void deleteQuestion(String domainName, int domain, String user, Integer id) throws AonCoreException;
	Question saveQuestion(String domainName, int domain, String user, Question question) throws AonCoreException;
	Question getQuestion(String domainName, int domain, String user, Integer id) throws AonCoreException;
	
	// **************************************************
	// **************************** [REMESA VENCIMIENTOS]
	// **************************************************
	
	FBatch getFBatch(String domainName, Integer domainId, String user, Integer fbatchId) throws AonCoreException;
	FBatch createUpdateFBatch(String domainName, Integer domainId, String user, FBatch fbatch) throws AonCoreException;
	LinkedList<RegistryBank> getCompanyBanks(String domainName, Integer domainId, String user) throws AonCoreException;
	
	// **************************************************
	// ***************************** [MARKETING CAMPAIGN]
	// **************************************************
	
	List<MarketingCampaign> getMarketingCampaigns(MarketingCompaignParams params) throws AonCoreException;
	void deleteMarketingCampaign(String domainName, int domain, String user, Integer id) throws AonCoreException;
	MarketingCampaign saveMarketingCampaign(String domainName, int domain, String user, MarketingCampaign marketingCampaign) throws AonCoreException;
	MarketingCampaign getMarketingCampaign(String domainName, int domain, String user, Integer id) throws AonCoreException;
	
	// **************************************************
	// ******************************* [MARKETING ACTION]
	// **************************************************
	
	List<MarketingAction> getMarketingActions(MarketingActionParams params) throws AonCoreException;
	MarketingAction getMarketingAction(String domainName, int domain, String user, Integer id) throws AonCoreException;
	void deleteMarketingAction(String domainName, int domain, String user, Integer id) throws AonCoreException;
	MarketingAction saveMarketingAction(String domainName, int domain, String user, MarketingAction marketingAction) throws AonCoreException;
	
	List<News> getNewsSuggestion(String domainName, int domain, String user) throws AonCoreException;
	List<Newsletter> getNewsletterSuggestion(String domainName, int domain, String user) throws AonCoreException;
	List<Survey> getSurveySuggestion(String domainName, int domain, String user) throws AonCoreException;
	List<Tag> getTagSuggestion(String domainName, int domain, String user, TagType tagType) throws AonCoreException;
	
	// **************************************************
	// ************************ [MARKETING ACTION TARGET]
	// **************************************************
	
	List<MarketingActionTarget> getMarketingActionTargets(MarketingActionTargetParams params) throws AonCoreException;
	List<MarketingActionTarget> getMarketingActionTargets(MarketingActionTargetMassiveParams params) throws AonCoreException;
	void deleteMarketingActionTarget(String domainName, int domain, String user, Integer actionTargetId) throws AonCoreException;
	MarketingActionTarget saveMarketingActionTarget(String domainName, int domain, String user,MarketingActionTarget marketingActionTarget) throws AonCoreException;
	
	List<Target> getTargetSuggestion(String domainName, int domain, String user) throws AonCoreException;
	
	List<ProjectType> getAviableProjectTypes(String domainName, int domain, String user, int domainSearch) throws AonCoreException;
	List<Workgroup> getAviableWorkgroups(String domainName, int domain, String user, Integer domainSearch) throws AonCoreException;
	List<TaskHolder> getAviableTaskHolders(String domainName, int domain, String user, Integer workgroup) throws AonCoreException;

	List<User> getAviableServiceUsers(String domainName, int domain, String user) throws AonCoreException;
	
	List<ProjectActivity> getAviableProjectActivity(String domainName, int domain, String user) throws AonCoreException;
	List<ProjectType> getAviableProjectType(String domainName, int domain, String user, Integer domainSearch) throws AonCoreException;
	
	
	// **************************************************
	// ***************************** [PROJECT COMMERCIAL]
	// **************************************************
	
	Seller getSellerByTaskHolder(String domainName, int domain, String user, int taskHolder) throws AonCoreException;
	Seller getNextLinealSellerByWorkgroup(String domainName, int domain, String user, int workgroup) throws AonCoreException;
	ProjectCommercial saveProjectCommercial(String domainName, int domain, String user, ProjectCommercial projectCommercial) throws AonCoreException;
	void deleteProjectCommercial(String domainName, int domain, String user, Integer projectCommercial) throws AonCoreException;

	// **************************************************
	// ***************************************** [SELLER]
	// **************************************************
	
	List<Seller> getSellers(SellerParams params) throws AonCoreException;
	Integer getSellersCount(SellerParams params) throws AonCoreException;
	Seller getSeller(String domainName, int domain, String user, Integer id) throws AonCoreException;
	Seller saveSeller(String domainName, int domain, String user, Seller seller) throws AonCoreException;
	void deleteSeller(String domainName, int domain, String user, Integer sellerId) throws AonCoreException;

	List<CommissionType> getAviableCommisionTypes(String domainName, int domain, String user) throws AonCoreException;
	List<TaskHolder> getAviableSellerTaskHolders(String domainName, int domain, String user) throws AonCoreException;
	List<Workgroup> getTaskHolderWorkgroups(String domainName, int domain, String user, Integer taskHolderId) throws AonCoreException;
	User getTaskHolderUser(String domainName, int domain, String user, Integer userId) throws AonCoreException;
	
	List<RegistryAddress> getRegistryAddresses(String domainName, Integer domain, String user, Integer registry) throws AonCoreException;
	RegistryAddress getRegistryAddress(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	RegistryAddress saveRegistryAddress(String domainName, Integer domain, String user, RegistryAddress registryAddress) throws AonCoreException;
	void deleteRegistryAddress(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	
	List<RegistryMedia> getRegistryMedias(String domainName, Integer domain, String user, Integer registry) throws AonCoreException;
	RegistryMedia getRegistryMedia(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	RegistryMedia saveRegistryMedia(String domainName, Integer domain, String user, RegistryMedia registryMedia) throws AonCoreException;
	void deleteRegistryMedia(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	
	List<RegistryAddInfo> getRegistryAddInfos(String domainName, Integer domain, String user, Integer registry) throws AonCoreException;
	RegistryAddInfo getRegistryAddInfo(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	RegistryAddInfo saveRegistryAddInfo(String domainName, Integer domain, String user, RegistryAddInfo registryAddInfo) throws AonCoreException;
	void deleteRegistryAddInfo(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	List<String> getRAddInfoAviableAttributes(String domainName, Integer domain, String user) throws AonCoreException;
	
	List<Attach> getRegistryAttaches(String domainName, Integer domain, String user, Integer registry) throws AonCoreException;
	Attach getRegistryAttach(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	Attach saveRegistryAttach(String domainName, Integer domain, String user, Attach attach) throws AonCoreException;
	void deleteRegistryAttach(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	List<Category> getAviableCategories(String domainName, Integer domainId, String user) throws AonCoreException;

	// **************************************************
	// ******************************** [SELLER WORKLOAD]
	// **************************************************
	
	List<SellerWorkload> getSellersWorkload(SellerWorkloadParams params) throws AonCoreException;
	Integer getSellersWorkloadCount(SellerWorkloadParams params) throws AonCoreException;
	SellerWorkloadContent getSellersWorkloadContent(SellerWorkloadParams params) throws AonCoreException;
	
	// **************************************************
	// **************************************** [PRODUCT]
	// **************************************************
	
	List<Product> getProducts(ProductParams params) throws AonCoreException;
	List<ProductBooking> getProductsBooking(ProductParams params) throws AonCoreException;
	Product getProduct(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	ProductBooking getProductBooking(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	void deleteProduct(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	Product saveProduct(String domainName, Integer domain, String user, Product product) throws AonCoreException;
	ProductBooking saveProductBooking(String domainName, Integer domain, String user, ProductBooking product) throws AonCoreException;
	Product createProduct(String domainName, Integer domain, String user, Product product, List<ProductTag> productTags, Item item) throws AonCoreException;
	ProductBooking createProductBooking(String domainName, Integer domain, String user, ProductBooking product, List<ProductTag> productTags, Item item) throws AonCoreException;
	
	Item getItem(String domainName, Integer domain, String user, Integer productId) throws AonCoreException;
	Item saveItem(String domainName, int domain, String user, Item item) throws AonCoreException;
	List<ProductCategory> getProductCategories(String domainName, Integer domain, String user) throws AonCoreException;
	List<Tax> getTaxTypes(String domainName, Integer domain, String user, TaxType taxType) throws AonCoreException;
	List<ProductTag> getProductTags(String domainName, Integer domain, String user, Integer productId) throws AonCoreException;
	List<Tag> getTags(String domainName, Integer domain, String user) throws AonCoreException;
	void saveProductTags(String domainName, int domain, String user, Integer id, List<ProductTag> productTags) throws AonCoreException;
	
	List<Tariff> getTariffs(String domainName, int domain, String user) throws AonCoreException;
	List<ItemTariff> getItemTariffs(String domainName, int domain, String user, Integer id) throws AonCoreException;
	void deleteItemTariff(String domainName, int domain, String user, Integer id) throws AonCoreException;
	ItemTariff saveItemTariff(String domainName, int domain, String user, ItemTariff itemTariff) throws AonCoreException;
	
	List<Item> getItems(String domainName, int domain, String user, ProductType productType) throws AonCoreException;
	List<ItemComposition> getItemCompositions(String domainName, int domain, String user, Integer itemId) throws AonCoreException;
	void deleteItemComposition(String domainName, int domain, String user, Integer idk) throws AonCoreException;
	void deleteItemCompositions(String domainName, int domain, String user, List<Integer> itemCompositions)throws AonCoreException;
	ItemComposition saveItemComposition(String domainName, int domain, String user, ItemComposition itemComposition) throws AonCoreException;
	List<ItemComposition> saveItemCompositions(String domainName, int domain, String user, List<ItemComposition> itemCompositions) throws AonCoreException;
	
	List<ItemAddInfo> getItemAddInfos(String domainName, int domain, String user, Integer itemId) throws AonCoreException;
	void saveItemAddInfos(String domainName, int domain, String user, List<ItemAddInfo> itemAddInfoList) throws AonCoreException;
	

	// **************************************************
	// ***************************************** [TARIFF]
	// **************************************************

	List<Tariff> getTariffs(TariffParams params) throws AonCoreException;
	Tariff getTariff(String domainName, int domain, String user, Integer tariffId) throws AonCoreException;
	void deleteTariff(String domainName, int domain, String user, Integer id) throws AonCoreException;
	Tariff saveTariff(String domainName, int domain, String user, Tariff tariff) throws AonCoreException;
	
	List<TariffAddInfo> getTariffAddInfoList(String domainName, int domain, String user, Integer tariffId) throws AonCoreException;
	TariffAddInfo saveTariffAddInfo(String domainName, int domain, String user, TariffAddInfo tariffAddInfo) throws AonCoreException;
	void deleteTariffAddInfo(String domainName, int domain, String user, Integer id) throws AonCoreException;
	
	List<TariffCatalogue> getTariffCatalgueList(String domainName, int domain, String user, Integer tariffId) throws AonCoreException;
	TariffCatalogue saveTariffCatalogue(String domainName, int domain, String user, TariffCatalogue tariffCatalogue) throws AonCoreException;
	void deleteTariffCatalogue(String domainName, int domain, String user, Integer id) throws AonCoreException;
	
	List<Catalogue> getCatalogueList(String domainName, int domain, String user) throws AonCoreException;

	Domain getOfficeSibling(String domainName, int domain, String user) throws AonCoreException;
	
	// **************************************************
	// ****************************************** [SALES]
	// **************************************************

	List<Sales> getSales(SalesParams params) throws AonCoreException;
	Sales getSale(String domainName, int domain, String user, Integer saleId) throws AonCoreException;
	List<Seller> getTaskHolderUsers(String domainName, int domain, String user) throws AonCoreException;
	List<Workplace> getWorkplaces(String domainName, int domain, String user) throws AonCoreException;
	
	// **************************************************
	// ******************* [TARGET - ENTERPRISE CREATION]
	// **************************************************
	
	Map<TargetFull, List<RegistrySeller>> getTargetNotUserFull(TargetParams params) throws AonCoreException;
	Customer getCustomerByDocument(String domainName, int domain, String user, String document) throws AonCoreException;
	TargetFull getTargetFull(String domainName, int domain, String user, Integer registry) throws AonCoreException;
	Company getCompanyByDocument(String domainName, int domain, String user, String document) throws AonCoreException;
	Seller getSellerByUserLogin(String domainName, int domain, String user, Integer targetId) throws AonCoreException;
	
	// **************************************************
	// **************************************** [PROJECT]
	// **************************************************
	
	List<Project> getProjects(ProjectParams params) throws AonCoreException;
	Project getProject(String domainName, int domain, String user, Integer projectId) throws AonCoreException;
	void deleteProject(String domainName, int domain, String user, Integer projectId) throws AonCoreException;
	Project saveProject(String domainName, int domain, String user, Project project) throws AonCoreException;
	
	Integer getProjectsCount(ProjectParams params) throws AonCoreException;
	
	List<ProjectHolder> getProjectHolders(String domainName, int domain, String user, Integer projectId) throws AonCoreException;
	ProjectHolder saveProjectHolder(String domainName, int domain, String user, ProjectHolder projectHolder) throws AonCoreException;
	void deleteProjectHolder(String domainName, int domain, String user, Integer projectHolderId) throws AonCoreException;
	List<TaskHolder> getTaskHolders(String domainName, Integer domainId, String user, Integer domainSearch) throws AonCoreException;

	List<ActivityType> getActivityTypes(String domainName, int domain, String user, Integer domainSearch) throws AonCoreException;

	// **************************************************
	// ********************************* [CUSTOMER NOTES]
	// **************************************************
	
	List<RegistryNote> getRegistryNotes(String currentDomainName, int currentDomain, String currentUser, Integer customerId) throws AonCoreException;
	RegistryNote saveNote(String currentDomainName, int currentDomain, String currentUser, RegistryNote note) throws AonCoreException;
	void deleteNote(String currentDomainName, int currentDomain, String currentUser, Integer id) throws AonCoreException;

	// **************************************************
	// ****************************** [CUSTOMER INVOICES]
	// **************************************************
	
	List<Invoice> getCustomerInvoices(String currentDomainName, int currentDomain, String currentUser, Integer customerId) throws AonCoreException;
	String getInvoicePDF(String currentDomainName, int currentDomain, String currentUser, Integer officeDomain, Integer invoiceId) throws AonCoreException;
	
	// **************************************************
	// ************************************ [TASK HOLDER]
	// **************************************************
	
	List<TaskHolder> getTaskHolderList(TaskHolderParams params) throws AonCoreException;
	TaskHolder getTaskHolder(String domainName, int domain, String user, Integer taskHolderId) throws AonCoreException;
	void deleteTaskHolder(String domainName, int domain, String user, Integer taskHolderId) throws AonCoreException;
	TaskHolder saveTaskHolder(String domainName, int domain, String user, TaskHolder taskHolder) throws AonCoreException;

	Integer getTaskHoldersCount(TaskHolderParams params) throws AonCoreException;
	
	TaskHolderWorkgroup getTaskHolderWorkgroup(String domainName, Integer domain, String user, Integer taskHolderWorkgroupId) throws AonCoreException;
	TaskHolderWorkgroup saveTaskHolderWorkgroup(String domainName, Integer domain, String user, TaskHolderWorkgroup taskHolderWorkgroup) throws AonCoreException;
	void deleteTaskHolderWorkgroup(String domainName, Integer domain, String user, Integer taskHolderWorkgroupId) throws AonCoreException;
	List<TaskHolderWorkgroup> getTaskHolderWorkgroupList(String domainName, Integer domain, String user, Integer taskHolderId) throws AonCoreException;
	List<User> getUsersForTaskHolder(String domainName, Integer domainId, String user, boolean all) throws AonCoreException;
	User getUser(String domainName, Integer domainId, String user, Integer userId) throws AonCoreException;
	
	// **************************************************
	// ****************************************** [SCOPE]
	// **************************************************
	
	List<Scope> getScopeList(ScopeParams params) throws AonCoreException;
	Integer getScopesCount(ScopeParams params) throws AonCoreException;
	
	Scope getScope(String domainName, int domain, String user, Integer scopeId) throws AonCoreException;
	Scope saveScope(String domainName, int domain, String user, Scope scope) throws AonCoreException;
	void deleteScope(String domainName, int domain, String user, Integer scopeId) throws AonCoreException;
	
	List<UserScopeFull> getUserScopeList(String domainName, Integer domain, String user, Integer scopeId) throws AonCoreException;
	void saveUserScope(String domainName, Integer domainId, String user, UserScope userScope) throws AonCoreException;
	void deleteUserScope(String domainName, Integer domain, String user, Integer userScopeId) throws AonCoreException;
	
	List<Domain> getDomainScopeList(String domainName, Integer domain, String user, Integer scopeId) throws AonCoreException;
	void saveDomainScope(String domainName, Integer domainId, String user, Integer domainChange, Integer scopeId) throws AonCoreException;
	void deleteDomainScope(String domainName, Integer domainId, String user, Domain domain) throws AonCoreException;
	
	List<Domain> getDomains(String domainName, Integer domainId, String user) throws AonCoreException;

	// **************************************************
	// ******************************************** [TAG]
	// **************************************************

	List<Tag> getTagList(TagParams params) throws AonCoreException;
	Tag saveTag(String domainName, Integer domainId, String user, Tag tag) throws AonCoreException;
	void deleteTag(String domainName, int domain, String user, Integer id) throws AonCoreException;

	List<String> getSchemas() throws AonCoreException;
	
	// **************************************************
	// *********************** [CUSTOMER LINKED ACTIVITY]
	// **************************************************
	
	List<Customer> getCustomers(CustomersLinkedParams params) throws AonCoreException;
	List<Customer> getCustomersLinked(CustomersLinkedParams params) throws AonCoreException;
	List<Customer> getCustomersNotLinked(CustomersLinkedParams params) throws AonCoreException;
	HashMap<Integer, Domain> getCustomersDomain(String domainName, Integer domainId, String user, ArrayList<Integer> customerIds) throws AonCoreException;
	List<ActivitySummaryObject> getActivitySummary(String domainName, String userLogin, ActivitySummaryParams params) throws AonCoreException;
	List<DomainCompany> getAviableSyncDomains(CustomersDomainSyncParams paramsDomains) throws AonCoreException;
	void syncCustomer(String domainName, Integer domainId, String user, Integer customerId, DomainCompany domainCompany, boolean isSig) throws AonCoreException;
	List<DomainSigAddInfo> getDomainSigAddInfo(String domainName, Integer domainId, String user, Integer customerId) throws AonCoreException;
	
	LinkedList<Fee> getCustomerFeesRelatedRegistry(String domainName, int domain, String user, Integer customerRelatedRegistry) throws AonCoreException;
	
	void createBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, ProductBooking product, Fee newFee) throws AonCoreException;
	void updateBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Fee oldFee, ProductBooking product, Fee newFee) throws AonCoreException;
	void removeBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Fee oldFee, ProductBooking product) throws AonCoreException;
	
	List<RegistryPayMethod> getRegistryPayMethods(String domainName, Integer domainId, String user, Integer registry) throws AonCoreException;
	RegistryPayMethod saveRegistryPayMethod(String domainName, Integer domainId, String user, RegistryPayMethod registryPayMethod) throws AonCoreException;
	List<RegistryRelationship> getRegistryRelationshipsByRelated(String domainName, Integer domainId, String user, Integer customerRelatedRegistry) throws AonCoreException;
	Customer saveCustomer(String domainName, Integer domainId, String user, Customer customer) throws AonCoreException;
	
	List<RegistryRelationship> getRegistryRelationships(String domainName, int domain, String user) throws AonCoreException;
	void requestBookingInfo(String domainName, int domain, String user, ProductBooking product, Integer customerRegistry) throws AonCoreException;

	// **************************************************
	// *********************** [CERTIFICATES]
	// **************************************************
	
	DomainUserRoles getDomainUserRoles(String domainName, Integer domainId, String user) throws AonCoreException;
	List<Certificate> getCertificates(String domainName, Integer domainId, String user, boolean withParent) throws AonCoreException;
	void deleteCertificate(String domainName, Integer domainId, String user, Certificate certificate) throws AonCoreException;
	void downloadCertificate(String domainName, Integer domainId, String user, Integer certificateId, String filePath) throws AonCoreException;
	void verifyCertificate(String domainName, Integer domainId, String user, Integer certificateId, List<CertificateType> tags) throws AonCoreException;
	CertificateInfo getCertificateInfo(String domainName, Integer domainId, String user, Integer certificateId) throws AonCoreException;
	List<SecondaryUserCertificate> getSecondaryUsers(String domainName, Integer domainId, String user, Integer rattachId) throws AonCoreException;
	String getSecondaryUsersPDF(String domainName, Integer domainId, String user, Integer rattachId) throws AonCoreException;
	String getAssignedCCCsPDF(String domainName, Integer domainId, String user, Integer rattachId) throws AonCoreException;
	EmployeeSegSocial getIpfxNaf(String domainName, Integer domainId, String user, ArrayList<String> nssList) throws AonCoreException;
	void deleteSecondaryUser(String domainName, Integer domainId, String user, Integer rattachId, String ipfType, String ipf) throws AonCoreException;
	void createSecondaryUser(String domainName, Integer domainId, String user, Integer rattachId, String ipfType, String ipf, String naf) throws AonCoreException;
	
	// **************************************************
	// *********************** [REGISTRY ENTRY]
	// **************************************************
	
	CompanyFull getCompanyFull(String domainName, int domain, String user) throws AonCoreException;
	CompanyFull saveCompanyFull(String domainName, int domain, String user, CompanyFull company) throws AonCoreException;
	Attach getCompanyLogo(String domainName, Integer domain, String user, Integer registry) throws AonCoreException;
	Attach getCompanySignature(String domainName, Integer domain, String user, Integer registry) throws AonCoreException;
	List<Calendar> getCalendars(String domainName, Integer domain, String user) throws AonCoreException;
	List<Agreement> getAcgreements(String domainName, Integer domain, String user) throws AonCoreException;
	List<Workplace> getWorkplaces(String domainName, Integer domain, String user, Integer registry) throws AonCoreException;
	void deleteWrokplace(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	Workplace saveWorkplace(String domainName, Integer domainId, String user, Workplace workplace) throws AonCoreException;
	
	List<Activity> getEnterpriseActivities(String domainName, Integer domain, String user, Integer registry) throws AonCoreException;
	Activity saveEnterpriseActivity(String domainName, Integer domainId, String user, Activity enterpriseActivity) throws AonCoreException;
	void deleteEnterpriseActivity(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	List<Cnae2009> getCnae2009List(String domainName, Integer domainId, String user) throws AonCoreException;
	List<Cnae> getCnae2025List(String domainName, Integer domainId, String user) throws AonCoreException;
	List<Iae> getIaeList(String domainName, Integer domainId, String user) throws AonCoreException;
	
	List<RDirStaff> getRDirStaffs(String domainName, Integer domain, String user, Integer registry) throws AonCoreException;
	RDirStaff saveRDirStaff(String domainName, Integer domainId, String user, RDirStaff rDirStaff) throws AonCoreException;
	void deleteRDirStaff(String domainName, Integer domain, String user, Integer id) throws AonCoreException;
	
	List<RecordData> getRecordDatas(String domainName, Integer domain, String user, Integer registry, boolean witdhData) throws AonCoreException;
	void deleteRecordData(String domainName, Integer domain, String user, Integer id, boolean deleteData) throws AonCoreException;
	RecordData saveRecordData(String domainName, Integer domainId, String user, RecordData recordData) throws AonCoreException;
	
	// *********************** [AMORTIZATION TYPE]
	List<AmortizationType> getAmortizationTypes(Occam occam, int domain) throws AonCoreException;
}
