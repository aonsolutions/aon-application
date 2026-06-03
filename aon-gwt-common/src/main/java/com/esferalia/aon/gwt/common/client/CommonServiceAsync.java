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
import com.esferalia.aon.occam.api.model.Signature;
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
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
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
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommonServiceAsync {

	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	void getAonConfiguration(String currentDomainName, int currentDomain, String user, AsyncCallback<AonConfiguration> callback);
	void getAonConfiguration(String currentDomainName, int currentDomain, String user, Date atDate, AsyncCallback<AonConfiguration> callback);
	void getAonConfiguration(Occam occam, AsyncCallback<AonConfiguration> asyncCallback);
	void getAonConfiguration(Occam occam, ConfigParams params, AsyncCallback<AonConfiguration> asyncCallback);
	void getParentDomain(String domainName, int domain, String user, Integer id, AsyncCallback<Domain> asyncCallback) throws AonCoreException;

	// **************************************************
	// *************************************** [SECURITY]
	// **************************************************
	void getCurrentUser(String domainName, int domain, String user, AsyncCallback<User> callback);

	// **************************************************
	// ************************************* [ENTERPRISE]
	// **************************************************
//	void getParentEnterprises(String domainName, int domain, String user, String query,AsyncCallback<LinkedList<Enterprise>> callback);
	void getEnterprise(String domainName, int domain, String user, int id,AsyncCallback<Enterprise> callback);
	void getCompanyBanks(String domainName, int domain, String user, int enterprise,AsyncCallback<LinkedList<CompanyBank>> callback);
	void getCompanyBanks(String domainName, int domain, String user,AsyncCallback<LinkedList<CompanyBank>> callback);

	// **************************************************
	// **************************************** [ACCOUNT]
	// **************************************************
	void getAccount(String domainName, int domain, String user, String code,AsyncCallback<Account> callback);
	void getAccount(String domainName, int domain, String user, Integer id,AsyncCallback<Account> callback);
	void getAccounts(String domainName, int domain, String user, String query,AsyncCallback<LinkedList<Account>> callback);
	void save(String domainName, int domain, String user, Account account,AsyncCallback<Account> callback);
	void delete(String domainName, int domain, String user, Account account,AsyncCallback<Account> callback);
	void getAccountNextCode(String domainName, int domain, String user, String prefix, AsyncCallback<String> asyncCallback);

	// **************************************************
	// ************************************* [PAY_METHOD]
	// **************************************************
	
	void getPayMethods(String domainName, int domain, String user,AsyncCallback<LinkedList<PayMethod>> callback) throws AonCoreException;
	void getPayMethods(PayMethodParams params,AsyncCallback<LinkedList<PayMethod>> callback) throws AonCoreException;
	void savePayMethod(String domainName,int domain, String user, PayMethod payMethod ,AsyncCallback<PayMethod> callback) throws AonCoreException;
	void deletePayMethod(String domainName,int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException;
	void groupPayMethod(String domainName,int domain, String user, List<PayMethod> selectedPaymethodList, PayMethod groupedPaymthod, AsyncCallback<Void> callback) throws AonCoreException;

	// **************************************************
	// **************************************** [INVOICE]
	// **************************************************
	void getInvoiceProducts(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<OldProduct>> asyncCallback);
	
	// **************************************************
	// *************************************** [REGISTRY]
	// **************************************************
	
	void getCustomers(String domainName, int domain, String user, Integer account, AsyncCallback<List<Customer>> asyncCallback) throws AonCoreException;
	void getSuppliers(String domainName, int domain, String user, Integer account, AsyncCallback<List<Supplier>> asyncCallback) throws AonCoreException;
	void getCreditors(String domainName, int domain, String user, Integer account, AsyncCallback<List<Creditor>> asyncCallback) throws AonCoreException;
	
	void getCustomer(String domainName, int domain, String user, Integer registry, AsyncCallback<CustomerFull> asyncCallback) throws AonCoreException;
	void getSupplier(String domainName, int domain, String user, Integer registry, AsyncCallback<SupplierFull> asyncCallback) throws AonCoreException;
	void getCreditor(String domainName, int domain, String user, Integer registry, AsyncCallback<CreditorFull> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ************************************ [COST CENTER]
	// **************************************************
	
	void getCostCenters(String domainName, int domain, String user, AsyncCallback<List<ApplicationParameter>> asyncCallback) throws AonCoreException;
	void saveCostCenter(String domainName, int domain, String user, ApplicationParameter costCenter, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void deleteCostCenter(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// *********************************** [INVEST ASSET]
	// **************************************************
	
	void getInvestAssets(InvestAssetParams params, AsyncCallback<List<InvestAsset>> asyncCallback) throws AonCoreException;
	void deleteInvestAsset(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveInvestAsset(String domainName, int domain, String user, InvestAsset investAsset, AsyncCallback<InvestAsset> asyncCallback) throws AonCoreException;
	void getActivities(String domainName, int domain, String user, AsyncCallback<List<Activity>> asyncCallback) throws AonCoreException;
	void getInvestAsset(String domainName, int domain, String user, Integer id, AsyncCallback<InvestAsset> asyncCallback) throws AonCoreException;

	// **************************************************
	// ********************************* [LOAD PDF MODEL]
	// **************************************************
	
	void savePDFModel(Occam occam, IFiscalModel model, String data, AsyncCallback<Void> callback);
	
	// **************************************************
	// *************************************** [QUESTION]
	// **************************************************
	
	void getQuestions(QuestionParams params, AsyncCallback<List<Question>> asyncCallback) throws AonCoreException;
	void deleteQuestion(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveQuestion(String domainName, int domain, String user, Question question, AsyncCallback<Question> asyncCallback) throws AonCoreException;
	void getQuestion(String domainName, int domain, String user, Integer id, AsyncCallback<Question> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// **************************** [REMESA VENCIMIENTOS]
	// **************************************************
	
	void getFBatch(String domainName, Integer domainId, String user, Integer fbatchId, AsyncCallback<FBatch> asyncCallback) throws AonCoreException;
	void createUpdateFBatch(String domainName, Integer domainId, String user, FBatch fbatch, AsyncCallback<FBatch> asyncCallback) throws AonCoreException;
	void getCompanyBanks(String domainName, Integer domainId, String user, AsyncCallback<LinkedList<RegistryBank>> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ***************************** [MARKETING CAMPAIGN]
	// **************************************************
	
	void getMarketingCampaigns(MarketingCompaignParams params, AsyncCallback<List<MarketingCampaign>> asyncCallback) throws AonCoreException;
	void deleteMarketingCampaign(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveMarketingCampaign(String domainName, int domain, String user, MarketingCampaign marketingCampaign, AsyncCallback<MarketingCampaign> asyncCallback) throws AonCoreException;
	void getMarketingCampaign(String domainName, int domain, String user, Integer id, AsyncCallback<MarketingCampaign> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ******************************* [MARKETING ACTION]
	// **************************************************
	
	void getMarketingActions(MarketingActionParams params, AsyncCallback<List<MarketingAction>> asyncCallback) throws AonCoreException;
	void getMarketingAction(String domainName, int domain, String user, Integer id, AsyncCallback<MarketingAction> asyncCallback) throws AonCoreException;
	void deleteMarketingAction(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveMarketingAction(String domainName, int domain, String user, MarketingAction marketingAction, AsyncCallback<MarketingAction> asyncCallback) throws AonCoreException;
	
	void getNewsSuggestion(String domainName, int domain, String user, AsyncCallback<List<News>> asyncCallback) throws AonCoreException;
	void getNewsletterSuggestion(String domainName, int domain, String user, AsyncCallback<List<Newsletter>> asyncCallback) throws AonCoreException;
	void getSurveySuggestion(String domainName, int domain, String user, AsyncCallback<List<Survey>> asyncCallback) throws AonCoreException;
	void getTagSuggestion(String domainName, int domain, String user, TagType tagType, AsyncCallback<List<Tag>> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ************************ [MARKETING ACTION TARGET]
	// **************************************************
	
	void getMarketingActionTargets(MarketingActionTargetParams params, AsyncCallback<List<MarketingActionTarget>> asyncCallback) throws AonCoreException;
	void getMarketingActionTargets(MarketingActionTargetMassiveParams params, AsyncCallback<List<MarketingActionTarget>> asyncCallback) throws AonCoreException;
	void deleteMarketingActionTarget(String domainName, int domain, String user, Integer actionTargetId, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveMarketingActionTarget(String domainName, int domain, String user,MarketingActionTarget marketingActionTarget, AsyncCallback<MarketingActionTarget> asyncCallback) throws AonCoreException;
	
	void getTargetSuggestion(String domainName, int domain, String user, AsyncCallback<List<Target>> asyncCallback) throws AonCoreException;
	
	void getAviableProjectTypes(String domainName, int domain, String user, int domainSearch, AsyncCallback<List<ProjectType>> asyncCallback) throws AonCoreException;
	void getAviableWorkgroups(String domainName, int domain, String user, Integer domainSearch, AsyncCallback<List<Workgroup>> asyncCallback) throws AonCoreException;
	void getAviableTaskHolders(String domainName, int domain, String user, Integer workgroup, AsyncCallback<List<TaskHolder>> asyncCallback) throws AonCoreException;

	void getAviableServiceUsers(String domainName, int domain, String user, AsyncCallback<List<User>> asyncCallback) throws AonCoreException;
	
	void getAviableProjectActivity(String domainName, int domain, String user, AsyncCallback<List<ProjectActivity>> asyncCallback) throws AonCoreException;
	void getAviableProjectType(String domainName, int domain, String user, Integer domainSearch, AsyncCallback<List<ProjectType>> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ***************************** [PROJECT COMMERCIAL]
	// **************************************************
	
	void getSellerByTaskHolder(String domainName, int domain, String user, int taskHolder, AsyncCallback<Seller> asyncCallback) throws AonCoreException;
	void getNextLinealSellerByWorkgroup(String domainName, int domain, String user, int workgroup, AsyncCallback<Seller> asyncCallback) throws AonCoreException;
	void saveProjectCommercial(String domainName, int domain, String user, ProjectCommercial projectCommercial, AsyncCallback<ProjectCommercial> asyncCallback) throws AonCoreException;
	void deleteProjectCommercial(String domainName, int domain, String user, Integer projectCommercial, AsyncCallback<Void> asyncCallback) throws AonCoreException;

	// **************************************************
	// ***************************************** [SELLER]
	// **************************************************
	
	void getSellers(SellerParams params, AsyncCallback<List<Seller>> asyncCallback) throws AonCoreException;
	void getSellersCount(SellerParams params, AsyncCallback<Integer> asyncCallback) throws AonCoreException;
	void getSeller(String domainName, int domain, String user, Integer id, AsyncCallback<Seller> asyncCallback) throws AonCoreException;
	void saveSeller(String domainName, int domain, String user, Seller seller, AsyncCallback<Seller> asyncCallback) throws AonCoreException;
	void deleteSeller(String domainName, int domain, String user, Integer sellerId, AsyncCallback<Void> asyncCallback) throws AonCoreException;

	void getAviableCommisionTypes(String domainName, int domain, String user, AsyncCallback<List<CommissionType>> asyncCallback) throws AonCoreException;
	void getAviableSellerTaskHolders(String domainName, int domain, String user, AsyncCallback<List<TaskHolder>> asyncCallback) throws AonCoreException;
	void getTaskHolderWorkgroups(String domainName, int domain, String user, Integer taskHolderId, AsyncCallback<List<Workgroup>> asyncCallback) throws AonCoreException;
	void getTaskHolderUser(String domainName, int domain, String user, Integer userId, AsyncCallback<User> asyncCallback) throws AonCoreException;
	
	void getRegistryAddresses(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RegistryAddress>> asyncCallback) throws AonCoreException;
	void getRegistryAddress(String domainName, Integer domain, String user, Integer id, AsyncCallback<RegistryAddress> asyncCallback) throws AonCoreException;
	void saveRegistryAddress(String domainName, Integer domain, String user, RegistryAddress registryAddress, AsyncCallback<RegistryAddress> asyncCallback) throws AonCoreException;
	void deleteRegistryAddress(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getRegistryMedias(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RegistryMedia>> asyncCallback) throws AonCoreException;
	void getRegistryMedia(String domainName, Integer domain, String user, Integer id, AsyncCallback<RegistryMedia> asyncCallback) throws AonCoreException;
	void saveRegistryMedia(String domainName, Integer domain, String user, RegistryMedia registryMedia, AsyncCallback<RegistryMedia> asyncCallback) throws AonCoreException;
	void deleteRegistryMedia(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getRegistryAddInfos(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RegistryAddInfo>> asyncCallback) throws AonCoreException;
	void getRegistryAddInfo(String domainName, Integer domain, String user, Integer id, AsyncCallback<RegistryAddInfo> asyncCallback) throws AonCoreException;
	void saveRegistryAddInfo(String domainName, Integer domain, String user, RegistryAddInfo registryAddInfo, AsyncCallback<RegistryAddInfo> asyncCallback) throws AonCoreException;
	void deleteRegistryAddInfo(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void getRAddInfoAviableAttributes(String domainName, Integer domain, String user, AsyncCallback<List<String>> asyncCallback) throws AonCoreException;
	
	void getRegistryAttaches(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<Attach>> asyncCallback) throws AonCoreException;
	void getRegistryAttach(String domainName, Integer domain, String user, Integer id, AsyncCallback<Attach> asyncCallback) throws AonCoreException;
	void saveRegistryAttach(String domainName, Integer domain, String user, Attach attach, AsyncCallback<Attach> asyncCallback) throws AonCoreException;
	void deleteRegistryAttach(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void getAviableCategories(String domainName, Integer domainId, String user, AsyncCallback<List<Category>> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ******************************** [SELLER WORKLOAD]
	// **************************************************
	
	void getSellersWorkload(SellerWorkloadParams params, AsyncCallback<List<SellerWorkload>> asyncCallback) throws AonCoreException;
	void getSellersWorkloadCount(SellerWorkloadParams params, AsyncCallback<Integer> asyncCallback) throws AonCoreException;
	void getSellersWorkloadContent(SellerWorkloadParams params, AsyncCallback<SellerWorkloadContent> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// **************************************** [PRODUCT]
	// **************************************************
	
	void getProducts(ProductParams params, AsyncCallback<List<Product>> asyncCallback) throws AonCoreException;
	void getProductsBooking(ProductParams params, AsyncCallback<List<ProductBooking>> asyncCallback) throws AonCoreException;
	void getProduct(String domainName, Integer domain, String user, Integer id, AsyncCallback<Product> asyncCallback) throws AonCoreException;
	void getProductBooking(String domainName, Integer domain, String user, Integer id, AsyncCallback<ProductBooking> asyncCallback) throws AonCoreException;
	void deleteProduct(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveProduct(String domainName, Integer domain, String user, Product product, AsyncCallback<Product> asyncCallback) throws AonCoreException;
	void saveProductBooking(String domainName, Integer domain, String user, ProductBooking product, AsyncCallback<ProductBooking> asyncCallback) throws AonCoreException;
	void createProduct(String domainName, Integer domain, String user, Product product, List<ProductTag> productTags, Item item, AsyncCallback<Product> asyncCallback) throws AonCoreException;
	void createProductBooking(String domainName, Integer domain, String user, ProductBooking product, List<ProductTag> productTags, Item item, AsyncCallback<ProductBooking> asyncCallback) throws AonCoreException;
	void getItem(String domainName, Integer domain, String user, Integer productId, AsyncCallback<Item> asyncCallback) throws AonCoreException;
	void saveItem(String domainName, int domain, String user, Item item, AsyncCallback<Item> asyncCallback) throws AonCoreException;
	void getProductCategories(String domainName, Integer domain, String user, AsyncCallback<List<ProductCategory>> asyncCallback) throws AonCoreException;
	void getTaxTypes(String domainName, Integer domain, String user, TaxType taxType, AsyncCallback<List<Tax>> asyncCallback) throws AonCoreException;
	void getProductTags(String domainName, Integer domain, String user, Integer productId, AsyncCallback<List<ProductTag>> asyncCallback) throws AonCoreException;
	void getTags(String domainName, Integer domain, String user, AsyncCallback<List<Tag>> asyncCallback) throws AonCoreException;
	void saveProductTags(String domainName, int domain, String user, Integer id, List<ProductTag> productTags, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getTariffs(String domainName, int domain, String user, AsyncCallback<List<Tariff>> asyncCallback) throws AonCoreException;
	void getItemTariffs(String domainName, int domain, String user, Integer id,AsyncCallback<List<ItemTariff>> asyncCallback) throws AonCoreException;
	void deleteItemTariff(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveItemTariff(String domainName, int domain, String user, ItemTariff itemTariff, AsyncCallback<ItemTariff> asyncCallback) throws AonCoreException;
	
	void getItems(String domainName, int domain, String user, ProductType productType, AsyncCallback<List<Item>> asyncCallback) throws AonCoreException;
	void getItemCompositions(String domainName, int domain, String user, Integer itemId, AsyncCallback<List<ItemComposition>> asyncCallback) throws AonCoreException;
	void deleteItemComposition(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void deleteItemCompositions(String domainName, int domain, String user, List<Integer> itemCompositions, AsyncCallback<Void> asyncCallback)throws AonCoreException;
	void saveItemComposition(String domainName, int domain, String user, ItemComposition itemComposition, AsyncCallback<ItemComposition> asyncCallback) throws AonCoreException;
	void saveItemCompositions(String domainName, int domain, String user, List<ItemComposition> itemCompositions, AsyncCallback<List<ItemComposition>> asyncCallback) throws AonCoreException;
	
	void getItemAddInfos(String domainName, int domain, String user, Integer itemId, AsyncCallback<List<ItemAddInfo>> asyncCallback) throws AonCoreException;
	void saveItemAddInfos(String domainName, int domain, String user, List<ItemAddInfo> itemAddInfoList, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ***************************************** [TARIFF]
	// **************************************************

	void getTariffs(TariffParams params, AsyncCallback<List<Tariff>> asyncCallback) throws AonCoreException;
	void getTariff(String domainName, int domain, String user, Integer tariffId, AsyncCallback<Tariff> asyncCallback) throws AonCoreException;
	void deleteTariff(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveTariff(String domainName, int domain, String user, Tariff tariff, AsyncCallback<Tariff> asyncCallback) throws AonCoreException;
	
	void getTariffAddInfoList(String domainName, int domain, String user, Integer tariffId, AsyncCallback<List<TariffAddInfo>> asyncCallback) throws AonCoreException;
	void saveTariffAddInfo(String domainName, int domain, String user, TariffAddInfo tariffAddInfo, AsyncCallback<TariffAddInfo> asyncCallback) throws AonCoreException;
	void deleteTariffAddInfo(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getTariffCatalgueList(String domainName, int domain, String user, Integer tariffId, AsyncCallback<List<TariffCatalogue>> asyncCallback) throws AonCoreException;
	void saveTariffCatalogue(String domainName, int domain, String user, TariffCatalogue tariffCatalogue, AsyncCallback<TariffCatalogue> asyncCallback) throws AonCoreException;
	void deleteTariffCatalogue(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getCatalogueList(String domainName, int domain, String user, AsyncCallback<List<Catalogue>> asyncCallback) throws AonCoreException;
	
	void getOfficeSibling(String domainName, int domain, String user, AsyncCallback<Domain> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ****************************************** [SALES]
	// **************************************************
	
	void getSales(SalesParams params, AsyncCallback<List<Sales>> asyncCallback) throws AonCoreException;
	void getSale(String domainName, int domain, String user, Integer saleId, AsyncCallback<Sales> asyncCallback) throws AonCoreException;
	void getTaskHolderUsers(String domainName, int domain, String user, AsyncCallback<List<Seller>> asyncCallback) throws AonCoreException;
	void getWorkplaces(String domainName, int domain, String user, AsyncCallback<List<Workplace>> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ******************* [TARGET - ENTERPRISE CREATION]
	// **************************************************
	
	void getTargetNotUserFull(TargetParams params, AsyncCallback<Map<TargetFull, List<RegistrySeller>>> asyncCallback) throws AonCoreException;
	void getCustomerByDocument(String domainName, int domain, String user, String document, AsyncCallback<Customer> asyncCallback) throws AonCoreException;
	void getTargetFull(String domainName, int domain, String user, Integer registry, AsyncCallback<TargetFull> asyncCallback) throws AonCoreException;
	void getCompanyByDocument(String domainName, int domain, String user, String document, AsyncCallback<Company> asyncCallback) throws AonCoreException;
	void getSellerByUserLogin(String domainName, int domain, String user, Integer targetId, AsyncCallback<Seller> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// **************************************** [PROJECT]
	// **************************************************
	
	void getProjects(ProjectParams params, AsyncCallback<List<Project>> asyncCallback) throws AonCoreException;
	void getProject(String domainName, int domain, String user, Integer projectId, AsyncCallback<Project> asyncCallback) throws AonCoreException;
	void deleteProject(String domainName, int domain, String user, Integer projectId, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveProject(String domainName, int domain, String user, Project project, AsyncCallback<Project> asyncCallback) throws AonCoreException;

	void getProjectsCount(ProjectParams params, AsyncCallback<Integer> asyncCallback) throws AonCoreException;
	
	void getProjectHolders(String domainName, int domain, String user, Integer projectId, AsyncCallback<List<ProjectHolder>> asyncCallback) throws AonCoreException;
	void saveProjectHolder(String domainName, int domain, String user, ProjectHolder project, AsyncCallback<ProjectHolder> asyncCallback) throws AonCoreException;
	void deleteProjectHolder(String domainName, int domain, String user, Integer projectHolderId, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void getTaskHolders(String domainName, Integer domainId, String user, Integer domainSearch, AsyncCallback<List<TaskHolder>> asyncCallback) throws AonCoreException;
	
	void getActivityTypes(String domainName, int domain, String user, Integer domainSearch, AsyncCallback<List<ActivityType>> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ********************************* [CUSTOMER NOTES]
	// **************************************************
	
	void getRegistryNotes(String currentDomainName, int currentDomain, String currentUser, Integer customerId, AsyncCallback<List<RegistryNote>> asyncCallback) throws AonCoreException;
	void saveNote(String currentDomainName, int currentDomain, String currentUser, RegistryNote note, AsyncCallback<RegistryNote> asyncCallback) throws AonCoreException;
	void deleteNote(String currentDomainName, int currentDomain, String currentUser, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ****************************** [CUSTOMER INVOICES]
	// **************************************************
	
	void getCustomerInvoices(String currentDomainName, int currentDomain, String currentUser, Integer customerId, AsyncCallback<List<Invoice>> asyncCallback) throws AonCoreException;
	void getInvoicePDF(String currentDomainName, int currentDomain, String currentUser, Integer officeDomain, Integer invoiceId, AsyncCallback<String> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ************************************ [TASK HOLDER]
	// **************************************************
	
	void getTaskHolderList(TaskHolderParams params, AsyncCallback<List<TaskHolder>> asyncCallback) throws AonCoreException;
	void getTaskHolder(String domainName, int domain, String user, Integer taskHolderId, AsyncCallback<TaskHolder> asyncCallback) throws AonCoreException;
	void deleteTaskHolder(String domainName, int domain, String user, Integer taskHolderId, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveTaskHolder(String domainName, int domain, String user, TaskHolder taskHolder, AsyncCallback<TaskHolder> asyncCallback) throws AonCoreException;

	void getTaskHoldersCount(TaskHolderParams params, AsyncCallback<Integer> asyncCallback) throws AonCoreException;
	
	void saveTaskHolderWorkgroup(String domainName, Integer domain, String user, TaskHolderWorkgroup taskHolderWorkgroup, AsyncCallback<TaskHolderWorkgroup> asyncCallback) throws AonCoreException;
	void getTaskHolderWorkgroup(String domainName, Integer domain, String user, Integer taskHolderWorkgroupId, AsyncCallback<TaskHolderWorkgroup> asyncCallback) throws AonCoreException;
	void deleteTaskHolderWorkgroup(String domainName, Integer domain, String user, Integer taskHolderWorkgroupId, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void getTaskHolderWorkgroupList(String domainName, Integer domain, String user, Integer taskHolderId, AsyncCallback<List<TaskHolderWorkgroup>> asyncCallback) throws AonCoreException;
	void getUsersForTaskHolder(String domainName, Integer domainId, String user, boolean all, AsyncCallback<List<User>> asyncCallback) throws AonCoreException;
	void getUser(String domainName, Integer domainId, String user, Integer userId, AsyncCallback<User> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// ****************************************** [SCOPE]
	// **************************************************
	
	void getScopeList(ScopeParams params, AsyncCallback<List<Scope>> asyncCallback) throws AonCoreException;
	void getScopesCount(ScopeParams params, AsyncCallback<Integer> asyncCallback) throws AonCoreException;
	
	void getScope(String domainName, int domain, String user, Integer scopeId, AsyncCallback<Scope> asyncCallback) throws AonCoreException;
	void saveScope(String domainName, int domain, String user, Scope scope, AsyncCallback<Scope> asyncCallback) throws AonCoreException;
	void deleteScope(String domainName, int domain, String user, Integer scopeId, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getUserScopeList(String domainName, Integer domain, String user, Integer scopeId, AsyncCallback<List<UserScopeFull>> asyncCallback) throws AonCoreException;
	void saveUserScope(String domainName, Integer domainId, String user, UserScope userScope, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void deleteUserScope(String domainName, Integer domain, String user, Integer userScopeId, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getDomainScopeList(String domainName, Integer domain, String user, Integer scopeId, AsyncCallback<List<Domain>> asyncCallback) throws AonCoreException;
	void saveDomainScope(String domainName, Integer domainId, String user, Integer domainChange, Integer scopeId, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void deleteDomainScope(String domainName, Integer domainId, String user, Domain domain, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getDomains(String domainName, Integer domainId, String user, AsyncCallback<List<Domain>> asyncCallback) throws AonCoreException;

	// **************************************************
	// ******************************************** [TAG]
	// **************************************************

	void getTagList(TagParams params, AsyncCallback<List<Tag>> asyncCallback) throws AonCoreException;
	void saveTag(String domainName, Integer domainId, String user, Tag tag, AsyncCallback<Tag> asyncCallback) throws AonCoreException;
	void deleteTag(String domainName, int domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getSchemas(AsyncCallback<List<String>> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// *********************** [CUSTOMER LINKED ACTIVITY]
	// **************************************************
	
	void getCustomers(CustomersLinkedParams params, AsyncCallback<List<Customer>> asyncCallback) throws AonCoreException;
	void getCustomersLinked(CustomersLinkedParams params, AsyncCallback<List<Customer>> asyncCallback) throws AonCoreException;
	void getCustomersNotLinked(CustomersLinkedParams params, AsyncCallback<List<Customer>> asyncCallback) throws AonCoreException;
	void getCustomersDomain(String domainName, Integer domainId, String user, ArrayList<Integer> customerIds, AsyncCallback<HashMap<Integer, Domain>> asyncCallback) throws AonCoreException;
	void getActivitySummary(String domainName, String userLogin,  ActivitySummaryParams params,AsyncCallback<List<ActivitySummaryObject>> asyncCallback) throws AonCoreException;
	void getAviableSyncDomains(CustomersDomainSyncParams paramsDomains, AsyncCallback<List<DomainCompany>> asyncCallback) throws AonCoreException;
	void syncCustomer(String domainName, Integer domainId, String user, Integer customerId, DomainCompany domainCompany, boolean isSig, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void getDomainSigAddInfo(String domainName, Integer domainId, String user, Integer customerId, AsyncCallback<List<DomainSigAddInfo>> asyncCallback) throws AonCoreException;
	
	void getCustomerFeesRelatedRegistry(String domainName, int domain, String user, Integer customerRelatedRegistry, AsyncCallback<LinkedList<Fee>> asyncCallback) throws AonCoreException;
	
	void createBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, ProductBooking product, Fee newFee, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void updateBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Fee oldFee, ProductBooking product, Fee newFee, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void removeBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Fee fee, ProductBooking product, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getRegistryPayMethods(String domainName, Integer domainId, String user, Integer registry, AsyncCallback<List<RegistryPayMethod>> asyncCallback) throws AonCoreException;
	void saveRegistryPayMethod(String domainName, Integer domainId, String user, RegistryPayMethod registryPayMethod, AsyncCallback<RegistryPayMethod> asyncCallback) throws AonCoreException;
	void getRegistryRelationshipsByRelated(String domainName, Integer domainId, String user, Integer customerRelatedRegistry, AsyncCallback<List<RegistryRelationship>> asyncCallback) throws AonCoreException;
	void saveCustomer(String domainName, Integer domainId, String user, Customer customer, AsyncCallback<Customer> asyncCallback) throws AonCoreException;
	
	void getRegistryRelationships(String domainName, int domain, String user, AsyncCallback<List<RegistryRelationship>> asyncCallback) throws AonCoreException;
	void requestBookingInfo(String domainName, int domain, String user, ProductBooking product, Integer customerRegistry, AsyncCallback<Void> asyncCallback) throws AonCoreException;

	// **************************************************
	// *********************** [CERTIFICATES]
	// **************************************************
	
	void getDomainUserRoles(String domainName, Integer domainId, String user, AsyncCallback<DomainUserRoles> asyncCallback) throws AonCoreException;
	void getCertificates(String domainName, Integer domainId, String user, boolean withParent, AsyncCallback<List<Certificate>> asyncCallback) throws AonCoreException;
	void deleteCertificate(String domainName, Integer domainId, String user, Certificate certificate, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void downloadCertificate(String domainName, Integer domainId, String user, Integer certificateId, String filePath, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void verifyCertificate(String domainName, Integer domainId, String user, Integer certificateId, List<CertificateType> tags, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void getCertificateInfo(String domainName, Integer domainId, String user, Integer certificateId, AsyncCallback<CertificateInfo> asyncCallback) throws AonCoreException;
	void getSecondaryUsers(String domainName, Integer domainId, String user, Integer rattachId, AsyncCallback<List<SecondaryUserCertificate>> asyncCallback) throws AonCoreException;
	void getSecondaryUsersPDF(String domainName, Integer domainId, String user, Integer rattachId, AsyncCallback<String> asyncCallback) throws AonCoreException;
	void getAssignedCCCsPDF(String domainName, Integer domainId, String user, Integer rattachId, AsyncCallback<String> asyncCallback) throws AonCoreException;
	void getIpfxNaf(String domainName, Integer domainId, String user, ArrayList<String> nssList, AsyncCallback<EmployeeSegSocial> asyncCallback) throws AonCoreException;
	void deleteSecondaryUser(String domainName, Integer domainId, String user, Integer rattachId, String ipfType, String ipf, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void createSecondaryUser(String domainName, Integer domainId, String user, Integer rattachId, String ipfType, String ipf, String naf, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	// **************************************************
	// *********************** [REGISTRY ENTRY]
	// **************************************************
	
	void getCompanyFull(String domainName, int domain, String user, AsyncCallback<CompanyFull> asyncCallback) throws AonCoreException;
	void saveCompanyFull(String domainName, int domain, String user, CompanyFull company, AsyncCallback<CompanyFull> asyncCallback) throws AonCoreException;
	void getCompanyLogo(String domainName, Integer domain, String user, Integer registry, AsyncCallback<Attach> asyncCallback) throws AonCoreException;
	void getCompanySignature(String domainName, Integer domain, String user, Integer registry, AsyncCallback<Attach> asyncCallback) throws AonCoreException;
	void getCalendars(String domainName, Integer domain, String user, AsyncCallback<List<Calendar>> asyncCallback) throws AonCoreException;
	void getAcgreements(String domainName, Integer domain, String user, AsyncCallback<List<Agreement>> asyncCallback) throws AonCoreException;
	void getWorkplaces(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<Workplace>> asyncCallback) throws AonCoreException;
	void deleteWrokplace(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveWorkplace(String domainName, Integer domainId, String user, Workplace workplace, AsyncCallback<Workplace> asyncCallback) throws AonCoreException;
	
	void getEnterpriseActivities(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<Activity>> asyncCallback) throws AonCoreException;
	void saveEnterpriseActivity(String domainName, Integer domainId, String user, Activity enterpriseActivity, AsyncCallback<Activity> asyncCallback) throws AonCoreException;
	void deleteEnterpriseActivity(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void getCnae2009List(String domainName, Integer domainId, String user, AsyncCallback<List<Cnae2009>> asyncCallback) throws AonCoreException;
	void getCnae2025List(String domainName, Integer domainId, String user, AsyncCallback<List<Cnae>> asyncCallback) throws AonCoreException;
	void getIaeList(String domainName, Integer domainId, String user, AsyncCallback<List<Iae>> asyncCallback) throws AonCoreException;
	
	void getRDirStaffs(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RDirStaff>> asyncCallback) throws AonCoreException;
	void saveRDirStaff(String domainName, Integer domainId, String user, RDirStaff rDirStaff, AsyncCallback<RDirStaff> asyncCallback) throws AonCoreException;
	void deleteRDirStaff(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getRecordDatas(String domainName, Integer domain, String user, Integer registry, boolean witdhData, AsyncCallback<List<RecordData>> asyncCallback) throws AonCoreException;
	void getRecordData(String domainName, Integer domain, String user, Integer recordDataId, AsyncCallback<RecordData> asyncCallback) throws AonCoreException;
	void deleteRecordData(String domainName, Integer domain, String user, Integer id, boolean deleteData, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveRecordData(String domainName, Integer domainId, String user, RecordData recordData, AsyncCallback<RecordData> asyncCallback) throws AonCoreException;
	void deleteRecordDataAttach(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	
	void getRregistryBanks(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RegistryBank>> asyncCallback) throws AonCoreException;
	void saveRregistryBank(String domainName, Integer domain, String user, RegistryBank registryBank, AsyncCallback<RegistryBank> asyncCallback) throws AonCoreException;
	void deleteRregistryBank(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void getAccountsForBank(String domainName, Integer domain, String user, AsyncCallback<List<Account>> asyncCallback) throws AonCoreException;
	void getAccountsForRegistry(String domainName, Integer domain, String user, RegistrySource registrySource, String pattern, AsyncCallback<List<Account>> asyncCallback) throws AonCoreException;
	
	void reassignScope(String domainName, Integer domainId, String user, int originScope, int finalScope, boolean deleteOrigin, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void getSignatures(String domainName, Integer domain, String user, AsyncCallback<LinkedList<Signature>> asyncCallback) throws AonCoreException;
	void deleteSignature(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveSignature(String domainName, Integer domainId, String user, Signature signature, AsyncCallback<Signature> asyncCallback) throws AonCoreException;
	
	// *********************** [AMORTIZATION TYPE]
	void getAmortizationTypes(Occam occam, int domain, AsyncCallback<List<AmortizationType>> asyncCallback);
	
}
