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

public class CommonServiceAsyncDecorator implements CommonServiceAsync {

	private CommonServiceAsync serviceAsync;

	public CommonServiceAsyncDecorator(CommonServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	@Override
	public void getAonConfiguration(String currentDomainName, int currentDomain, String user, Date atDate, AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(currentDomainName, currentDomain, user, atDate, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAonConfiguration(String currentDomainName, int currentDomain, String user, AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(currentDomainName, currentDomain, user, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void getAonConfiguration(Occam occam, AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(occam, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getAonConfiguration(Occam occam, ConfigParams params, AsyncCallback<AonConfiguration> callback) {
		AON.start();
		serviceAsync.getAonConfiguration(occam, params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getParentDomain(String domainName, int domain, String user, Integer id, AsyncCallback<Domain> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getParentDomain(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// *************************************** [SECURITY]
	// **************************************************
	@Override
	public void getCurrentUser(String domainName, int domain, String user, AsyncCallback<User> callback) {
		AON.start();
		serviceAsync.getCurrentUser(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// ************************************* [ENTERPRISE]
	// **************************************************
	@Override
	public void getEnterprise(String domainName, int domain, String user, int id, AsyncCallback<Enterprise> callback) {
		AON.start();
		serviceAsync.getEnterprise(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, String user, int enterprise, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		serviceAsync.getCompanyBanks(domainName, domain, user, enterprise, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, String user, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		serviceAsync.getCompanyBanks(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// **************************************** [ACCOUNT]
	// **************************************************
	@Override
	public void getAccount(String domainName, int domain, String user, Integer id, AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.getAccount(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAccount(String domainName, int domain, String user, String code, AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.getAccount(domainName, domain, user, code, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAccounts(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<Account>> callback) {
		AON.start();
		serviceAsync.getAccounts(domainName, domain, user, query, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(String domainName, int domain, String user, Account account, AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.save(domainName, domain, user, account, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(String domainName, int domain, String user, Account account, AsyncCallback<Account> callback) {
		AON.start();
		serviceAsync.delete(domainName, domain, user, account, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAccountNextCode(String domainName, int domain, String user, String prefix, AsyncCallback<String> callback) {
		AON.start();
		serviceAsync.getAccountNextCode(domainName, domain, user, prefix, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// ************************************* [PAY_METHOD]
	// **************************************************
	@Override
	public void getPayMethods(String domainName, int domain, String user, AsyncCallback<LinkedList<PayMethod>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getPayMethods(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getPayMethods(PayMethodParams params, AsyncCallback<LinkedList<PayMethod>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getPayMethods(params, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void savePayMethod(String domainName, int domain, String user, PayMethod payMethod, AsyncCallback<PayMethod> callback) throws AonCoreException {
		AON.start();
		serviceAsync.savePayMethod(domainName, domain, user, payMethod, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void deletePayMethod(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deletePayMethod(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void groupPayMethod(String domainName, int domain, String user, List<PayMethod> selectedPaymethodList, PayMethod groupedPaymthod, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.groupPayMethod(domainName, domain, user, selectedPaymethodList, groupedPaymthod, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// **************************************** [INVOICE]
	// **************************************************
	@Override
	public void getInvoiceProducts(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<OldProduct>> callback) {
		AON.start();
		serviceAsync.getInvoiceProducts(domainName, domain, user, query, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// *************************************** [REGISTRY]
	// **************************************************		
	
	@Override
	public void getCustomers(String domainName, int domain, String user, Integer account, AsyncCallback<List<Customer>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomers(domainName, domain, user, account, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSuppliers(String domainName, int domain, String user, Integer account, AsyncCallback<List<Supplier>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSuppliers(domainName, domain, user, account, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCreditors(String domainName, int domain, String user, Integer account, AsyncCallback<List<Creditor>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCreditors(domainName, domain, user, account, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getCustomer(String domainName, int domain, String user, Integer registry, AsyncCallback<CustomerFull> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomer(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSupplier(String domainName, int domain, String user, Integer registry, AsyncCallback<SupplierFull> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSupplier(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCreditor(String domainName, int domain, String user, Integer registry, AsyncCallback<CreditorFull> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCreditor(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ************************************ [COST CENTER]
	// **************************************************	

	@Override
	public void getCostCenters(String domainName, int domain, String user, AsyncCallback<List<ApplicationParameter>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCostCenters(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveCostCenter(String domainName, int domain, String user, ApplicationParameter costCenter, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveCostCenter(domainName, domain, user, costCenter, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void deleteCostCenter(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteCostCenter(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// *********************************** [INVEST ASSET]
	// **************************************************

	@Override
	public void getInvestAssets(InvestAssetParams params, AsyncCallback<List<InvestAsset>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getInvestAssets(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteInvestAsset(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteInvestAsset(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveInvestAsset(String domainName, int domain, String user, InvestAsset investAsset, AsyncCallback<InvestAsset> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveInvestAsset(domainName, domain, user, investAsset, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getActivities(String domainName, int domain, String user, AsyncCallback<List<Activity>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getActivities(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getInvestAsset(String domainName, int domain, String user, Integer id, AsyncCallback<InvestAsset> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getInvestAsset(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}
  
	// **************************************************
	// ********************************* [LOAD PDF MODEL]
	// **************************************************

	@Override
	public void savePDFModel(Occam occam, IFiscalModel model, String data, AsyncCallback<Void> callback) {		
		AON.start();		
		serviceAsync.savePDFModel(occam, model, data, new AsyncCallbackWrapper<Void>(callback));		
	}

	// **************************************************
	// *************************************** [QUESTION]
	// **************************************************
	
	@Override
	public void getQuestions(QuestionParams params, AsyncCallback<List<Question>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getQuestions(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteQuestion(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteQuestion(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveQuestion(String domainName, int domain, String user, Question question, AsyncCallback<Question> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveQuestion(domainName, domain, user, question, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getQuestion(String domainName, int domain, String user, Integer id, AsyncCallback<Question> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getQuestion(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// **************************** [REMESA VENCIMIENTOS]
	// **************************************************

	@Override
	public void getFBatch(String domainName, Integer domain, String user, Integer fbatchId, AsyncCallback<FBatch> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getFBatch(domainName, domain, user, fbatchId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void createUpdateFBatch(String domainName, Integer domain, String user, FBatch fbatch, AsyncCallback<FBatch> callback) throws AonCoreException {
		AON.start();
		serviceAsync.createUpdateFBatch(domainName, domain, user, fbatch, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, Integer domain, String user, AsyncCallback<LinkedList<RegistryBank>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCompanyBanks(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// ***************************** [MARKETING CAMPAIGN]
	// **************************************************
	
	@Override
	public void getMarketingCampaigns(MarketingCompaignParams params, AsyncCallback<List<MarketingCampaign>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getMarketingCampaigns(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteMarketingCampaign(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteMarketingCampaign(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveMarketingCampaign(String domainName, int domain, String user, MarketingCampaign marketingCampaign, AsyncCallback<MarketingCampaign> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveMarketingCampaign(domainName, domain, user, marketingCampaign, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMarketingCampaign(String domainName, int domain, String user, Integer id, AsyncCallback<MarketingCampaign> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getMarketingCampaign(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ******************************* [MARKETING ACTION]
	// **************************************************

	@Override
	public void getMarketingActions(MarketingActionParams params, AsyncCallback<List<MarketingAction>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getMarketingActions(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMarketingAction(String domainName, int domain, String user, Integer id, AsyncCallback<MarketingAction> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getMarketingAction(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteMarketingAction(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteMarketingAction(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveMarketingAction(String domainName, int domain, String user, MarketingAction marketingAction, AsyncCallback<MarketingAction> callback) {
		AON.start();
		serviceAsync.saveMarketingAction(domainName, domain, user, marketingAction, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getNewsSuggestion(String domainName, int domain, String user, AsyncCallback<List<News>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getNewsSuggestion(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getNewsletterSuggestion(String domainName, int domain, String user, AsyncCallback<List<Newsletter>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getNewsletterSuggestion(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSurveySuggestion(String domainName, int domain, String user, AsyncCallback<List<Survey>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSurveySuggestion(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getTagSuggestion(String domainName, int domain, String user, TagType tagType, AsyncCallback<List<Tag>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTagSuggestion(domainName, domain, user, tagType, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ************************ [MARKETING ACTION TARGET]
	// **************************************************


	@Override
	public void getMarketingActionTargets(MarketingActionTargetParams params, AsyncCallback<List<MarketingActionTarget>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getMarketingActionTargets(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMarketingActionTargets(MarketingActionTargetMassiveParams params, AsyncCallback<List<MarketingActionTarget>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getMarketingActionTargets(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteMarketingActionTarget(String domainName, int domain, String user, Integer actionTargetId, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteMarketingActionTarget(domainName, domain, user, actionTargetId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveMarketingActionTarget(String domainName, int domain, String user, MarketingActionTarget marketingActionTarget, AsyncCallback<MarketingActionTarget> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveMarketingActionTarget(domainName, domain, user, marketingActionTarget, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTargetSuggestion(String domainName, int domain, String user, AsyncCallback<List<Target>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTargetSuggestion(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableProjectTypes(String domainName, int domain, String user, int domainSearch, AsyncCallback<List<ProjectType>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableProjectTypes(domainName, domain, user, domainSearch, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableWorkgroups(String domainName, int domain, String user, Integer domainSearch, AsyncCallback<List<Workgroup>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableWorkgroups(domainName, domain, user, domainSearch, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableTaskHolders(String domainName, int domain, String user, Integer workgroup, AsyncCallback<List<TaskHolder>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableTaskHolders(domainName, domain, user, workgroup, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableServiceUsers(String domainName, int domain, String user, AsyncCallback<List<User>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableServiceUsers(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSellerByTaskHolder(String domainName, int domain, String user, int taskHolder, AsyncCallback<Seller> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSellerByTaskHolder(domainName, domain, user, taskHolder, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getNextLinealSellerByWorkgroup(String domainName, int domain, String user, int workgroup, AsyncCallback<Seller> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getNextLinealSellerByWorkgroup(domainName, domain, user, workgroup, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveProjectCommercial(String domainName, int domain, String user, ProjectCommercial projectCommercial, AsyncCallback<ProjectCommercial> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveProjectCommercial(domainName, domain, user, projectCommercial, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteProjectCommercial(String domainName, int domain, String user, Integer projectCommercial, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteProjectCommercial(domainName, domain, user, projectCommercial, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableProjectActivity(String domainName, int domain, String user, AsyncCallback<List<ProjectActivity>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableProjectActivity(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableProjectType(String domainName, int domain, String user, Integer domainSearch, AsyncCallback<List<ProjectType>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableProjectType(domainName, domain, user, domainSearch, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// ***************************************** [SELLER]
	// **************************************************
	
	@Override
	public void getSellers(SellerParams params, AsyncCallback<List<Seller>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSellers(params, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getSellersCount(SellerParams params, AsyncCallback<Integer> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSellersCount(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSeller(String domainName, int domain, String user, Integer id, AsyncCallback<Seller> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSeller(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveSeller(String domainName, int domain, String user, Seller seller, AsyncCallback<Seller> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveSeller(domainName, domain, user, seller, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteSeller(String domainName, int domain, String user, Integer sellerId, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteSeller(domainName, domain, user, sellerId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableCommisionTypes(String domainName, int domain, String user, AsyncCallback<List<CommissionType>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableCommisionTypes(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableSellerTaskHolders(String domainName, int domain, String user, AsyncCallback<List<TaskHolder>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableSellerTaskHolders(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTaskHolderWorkgroups(String domainName, int domain, String user, Integer taskHolderId, AsyncCallback<List<Workgroup>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHolderWorkgroups(domainName, domain, user, taskHolderId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTaskHolderUser(String domainName, int domain, String user, Integer userId, AsyncCallback<User> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHolderUser(domainName, domain, user, userId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryAddresses(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RegistryAddress>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryAddresses(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryAddress(String domainName, Integer domain, String user, Integer id, AsyncCallback<RegistryAddress> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryAddress(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveRegistryAddress(String domainName, Integer domain, String user, RegistryAddress registryAddress, AsyncCallback<RegistryAddress> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveRegistryAddress(domainName, domain, user, registryAddress, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteRegistryAddress(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteRegistryAddress(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryMedias(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RegistryMedia>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryMedias(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryMedia(String domainName, Integer domain, String user, Integer id, AsyncCallback<RegistryMedia> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryMedia(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveRegistryMedia(String domainName, Integer domain, String user, RegistryMedia registryMedia, AsyncCallback<RegistryMedia> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveRegistryMedia(domainName, domain, user, registryMedia, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteRegistryMedia(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteRegistryMedia(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryAddInfos(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RegistryAddInfo>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryAddInfos(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryAddInfo(String domainName, Integer domain, String user, Integer id, AsyncCallback<RegistryAddInfo> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryAddInfo(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveRegistryAddInfo(String domainName, Integer domain, String user, RegistryAddInfo registryAddInfo, AsyncCallback<RegistryAddInfo> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveRegistryAddInfo(domainName, domain, user, registryAddInfo, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteRegistryAddInfo(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteRegistryAddInfo(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRAddInfoAviableAttributes(String domainName, Integer domain, String user, AsyncCallback<List<String>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRAddInfoAviableAttributes(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryAttaches(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<Attach>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryAttaches(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryAttach(String domainName, Integer domain, String user, Integer id, AsyncCallback<Attach> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryAttach(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveRegistryAttach(String domainName, Integer domain, String user, Attach attach, AsyncCallback<Attach> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveRegistryAttach(domainName, domain, user, attach, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteRegistryAttach(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteRegistryAttach(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableCategories(String domainName, Integer domain, String user, AsyncCallback<List<Category>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableCategories(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// ******************************** [SELLER WORKLOAD]
	// **************************************************
	
	@Override
	public void getSellersWorkload(SellerWorkloadParams params, AsyncCallback<List<SellerWorkload>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSellersWorkload(params, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getSellersWorkloadCount(SellerWorkloadParams params, AsyncCallback<Integer> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSellersWorkloadCount(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSellersWorkloadContent(SellerWorkloadParams params, AsyncCallback<SellerWorkloadContent> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSellersWorkloadContent(params, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// **************************************** [PRODUCT]
	// **************************************************

	@Override
	public void getProducts(ProductParams params, AsyncCallback<List<Product>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProducts(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getProductsBooking(ProductParams params, AsyncCallback<List<ProductBooking>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProductsBooking(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getProduct(String domainName, Integer domain, String user, Integer id, AsyncCallback<Product> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProduct(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getProductBooking(String domainName, Integer domain, String user, Integer id, AsyncCallback<ProductBooking> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProductBooking(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteProduct(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteProduct(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveProduct(String domainName, Integer domain, String user, Product product, AsyncCallback<Product> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveProduct(domainName, domain, user, product, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveProductBooking(String domainName, Integer domain, String user, ProductBooking product, AsyncCallback<ProductBooking> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveProductBooking(domainName, domain, user, product, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void createProduct(String domainName, Integer domain, String user, Product product, List<ProductTag> productTags, Item item, AsyncCallback<Product> callback) throws AonCoreException {
		AON.start();
		serviceAsync.createProduct(domainName, domain, user, product, productTags, item, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void createProductBooking(String domainName, Integer domain, String user, ProductBooking product, List<ProductTag> productTags, Item item, AsyncCallback<ProductBooking> callback) throws AonCoreException {
		AON.start();
		serviceAsync.createProductBooking(domainName, domain, user, product, productTags, item, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getItem(String domainName, Integer domain, String user, Integer productId, AsyncCallback<Item> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getItem(domainName, domain, user, productId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveItem(String domainName, int domain, String user, Item item, AsyncCallback<Item> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveItem(domainName, domain, user, item, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getProductCategories(String domainName, Integer domain, String user, AsyncCallback<List<ProductCategory>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProductCategories(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTaxTypes(String domainName, Integer domain, String user, TaxType taxType, AsyncCallback<List<Tax>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaxTypes(domainName, domain, user, taxType, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getProductTags(String domainName, Integer domain, String user, Integer productId, AsyncCallback<List<ProductTag>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProductTags(domainName, domain, user, productId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTags(String domainName, Integer domain, String user, AsyncCallback<List<Tag>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTags(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveProductTags(String domainName, int domain, String user, Integer productId, List<ProductTag> productTags, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveProductTags(domainName, domain, user, productId, productTags, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTariffs(String domainName, int domain, String user, AsyncCallback<List<Tariff>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTariffs(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getItemTariffs(String domainName, int domain, String user, Integer id, AsyncCallback<List<ItemTariff>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getItemTariffs(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteItemTariff(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteItemTariff(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveItemTariff(String domainName, int domain, String user, ItemTariff itemTariff, AsyncCallback<ItemTariff> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveItemTariff(domainName, domain, user, itemTariff, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getItems(String domainName, int domain, String user, ProductType productType, AsyncCallback<List<Item>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getItems(domainName, domain, user, productType, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getItemCompositions(String domainName, int domain, String user, Integer itemId, AsyncCallback<List<ItemComposition>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getItemCompositions(domainName, domain, user, itemId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteItemComposition(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteItemComposition(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void deleteItemCompositions(String domainName, int domain, String user, List<Integer> itemCompositions, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteItemCompositions(domainName, domain, user, itemCompositions, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveItemComposition(String domainName, int domain, String user, ItemComposition itemComposition, AsyncCallback<ItemComposition> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveItemComposition(domainName, domain, user, itemComposition, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveItemCompositions(String domainName, int domain, String user, List<ItemComposition> itemCompositions, AsyncCallback<List<ItemComposition>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveItemCompositions(domainName, domain, user, itemCompositions, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getItemAddInfos(String domainName, int domain, String user, Integer itemId, AsyncCallback<List<ItemAddInfo>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getItemAddInfos(domainName, domain, user, itemId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveItemAddInfos(String domainName, int domain, String user, List<ItemAddInfo> itemAddInfoList, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveItemAddInfos(domainName, domain, user, itemAddInfoList, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// ***************************************** [TARIFF]
	// **************************************************

	@Override
	public void getTariffs(TariffParams params, AsyncCallback<List<Tariff>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTariffs(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTariff(String domainName, int domain, String user, Integer tariffId, AsyncCallback<Tariff> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTariff(domainName, domain, user, tariffId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteTariff(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteTariff(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveTariff(String domainName, int domain, String user, Tariff tariff, AsyncCallback<Tariff> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveTariff(domainName, domain, user, tariff, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTariffAddInfoList(String domainName, int domain, String user, Integer tariffId, AsyncCallback<List<TariffAddInfo>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTariffAddInfoList(domainName, domain, user, tariffId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveTariffAddInfo(String domainName, int domain, String user, TariffAddInfo tariffAddInfo, AsyncCallback<TariffAddInfo> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveTariffAddInfo(domainName, domain, user, tariffAddInfo, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteTariffAddInfo(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteTariffAddInfo(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTariffCatalgueList(String domainName, int domain, String user, Integer tariffId, AsyncCallback<List<TariffCatalogue>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTariffCatalgueList(domainName, domain, user, tariffId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveTariffCatalogue(String domainName, int domain, String user, TariffCatalogue tariffCatalogue, AsyncCallback<TariffCatalogue> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveTariffCatalogue(domainName, domain, user, tariffCatalogue, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteTariffCatalogue(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteTariffCatalogue(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCatalogueList(String domainName, int domain, String user, AsyncCallback<List<Catalogue>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCatalogueList(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getOfficeSibling(String domainName, int domain, String user, AsyncCallback<Domain> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getOfficeSibling(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ****************************************** [SALES]
	// **************************************************

	@Override
	public void getSales(SalesParams params, AsyncCallback<List<Sales>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSales(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSale(String domainName, int domain, String user, Integer saleId, AsyncCallback<Sales> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSale(domainName,domain, user, saleId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTaskHolderUsers(String domainName, int domain, String user, AsyncCallback<List<Seller>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHolderUsers(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getWorkplaces(String domainName, int domain, String user, AsyncCallback<List<Workplace>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getWorkplaces(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ******************* [TARGET - ENTERPRISE CREATION]
	// **************************************************

	@Override
	public void getTargetNotUserFull(TargetParams params, AsyncCallback<Map<TargetFull, List<RegistrySeller>>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTargetNotUserFull(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCustomerByDocument(String domainName, int domain, String user, String document, AsyncCallback<Customer> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomerByDocument(domainName, domain, user, document, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTargetFull(String domainName, int domain, String user, Integer registry, AsyncCallback<TargetFull> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTargetFull(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCompanyByDocument(String domainName, int domain, String user, String document, AsyncCallback<Company> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCompanyByDocument(domainName, domain, user, document, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSellerByUserLogin(String domainName, int domain, String user, Integer targetId, AsyncCallback<Seller> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSellerByUserLogin(domainName, domain, user, targetId, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// **************************************** [PROJECT]
	// **************************************************

	@Override
	public void getProjects(ProjectParams params, AsyncCallback<List<Project>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProjects(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getProject(String domainName, int domain, String user, Integer projectId, AsyncCallback<Project> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProject(domainName, domain, user, projectId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteProject(String domainName, int domain, String user, Integer projectId, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteProject(domainName, domain, user, projectId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveProject(String domainName, int domain, String user, Project project, AsyncCallback<Project> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveProject(domainName, domain, user, project, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getProjectsCount(ProjectParams params, AsyncCallback<Integer> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProjectsCount(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getProjectHolders(String domainName, int domain, String user, Integer projectId, AsyncCallback<List<ProjectHolder>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProjectHolders(domainName, domain, user, projectId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveProjectHolder(String domainName, int domain, String user, ProjectHolder projectHolder, AsyncCallback<ProjectHolder> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveProjectHolder(domainName, domain, user, projectHolder, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteProjectHolder(String domainName, int domain, String user, Integer projectHolderId, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteProjectHolder(domainName, domain, user, projectHolderId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTaskHolders(String domainName, Integer domain, String user, Integer domainSearch, AsyncCallback<List<TaskHolder>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHolders(domainName, domain, user, domainSearch, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getActivityTypes(String domainName, int domain, String user, Integer domainSearch, AsyncCallback<List<ActivityType>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getActivityTypes(domainName, domain, user, domainSearch, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ********************************* [CUSTOMER NOTES]
	// **************************************************

	@Override
	public void getRegistryNotes(String domainName, int domain, String user, Integer customerId, AsyncCallback<List<RegistryNote>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryNotes(domainName, domain, user, customerId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveNote(String domainName, int domain, String user, RegistryNote note, AsyncCallback<RegistryNote> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveNote(domainName, domain, user, note, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteNote(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteNote(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ****************************** [CUSTOMER INVOICES]
	// **************************************************

	@Override
	public void getCustomerInvoices(String domainName, int domain, String user, Integer customerId, AsyncCallback<List<Invoice>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomerInvoices(domainName, domain, user, customerId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getInvoicePDF(String domainName, int domain, String user, Integer officeDomain, Integer invoiceId, AsyncCallback<String> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getInvoicePDF(domainName, domain, user, officeDomain, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ************************************ [TASK HOLDER]
	// **************************************************

	@Override
	public void getTaskHolderList(TaskHolderParams params, AsyncCallback<List<TaskHolder>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHolderList(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTaskHolder(String domainName, int domain, String user, Integer taskHolderId, AsyncCallback<TaskHolder> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHolder(domainName, domain, user, taskHolderId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteTaskHolder(String domainName, int domain, String user, Integer taskHolderId, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteTaskHolder(domainName, domain, user, taskHolderId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveTaskHolder(String domainName, int domain, String user, TaskHolder taskHolder, AsyncCallback<TaskHolder> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveTaskHolder(domainName, domain, user, taskHolder, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTaskHoldersCount(TaskHolderParams params, AsyncCallback<Integer> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHoldersCount(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveTaskHolderWorkgroup(String domainName, Integer domain, String user, TaskHolderWorkgroup taskHolderWorkgroup, AsyncCallback<TaskHolderWorkgroup> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveTaskHolderWorkgroup(domainName, domain, user, taskHolderWorkgroup, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTaskHolderWorkgroup(String domainName, Integer domain, String user, Integer taskHolderWorkgroupId, AsyncCallback<TaskHolderWorkgroup> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHolderWorkgroup(domainName, domain, user, taskHolderWorkgroupId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteTaskHolderWorkgroup(String domainName, Integer domain, String user, Integer taskHolderWorkgroupId, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteTaskHolderWorkgroup(domainName, domain, user, taskHolderWorkgroupId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTaskHolderWorkgroupList(String domainName, Integer domain, String user, Integer taskHolderId, AsyncCallback<List<TaskHolderWorkgroup>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHolderWorkgroupList(domainName, domain, user, taskHolderId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getUsersForTaskHolder(String domainName, Integer domain, String user, boolean all, AsyncCallback<List<User>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getUsersForTaskHolder(domainName, domain, user, all, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getUser(String domainName, Integer domainId, String user, Integer userId, AsyncCallback<User> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getUser(domainName, domainId, user, userId, new AsyncCallbackWrapper<>(callback));
	}

	// **************************************************
	// ****************************************** [SCOPE]
	// **************************************************
	
	@Override
	public void getScopeList(ScopeParams params, AsyncCallback<List<Scope>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getScopeList(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getScopesCount(ScopeParams params, AsyncCallback<Integer> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getScopesCount(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getScope(String domainName, int domain, String user, Integer scopeId, AsyncCallback<Scope> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getScope(domainName, domain, user, scopeId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveScope(String domainName, int domain, String user, Scope scope, AsyncCallback<Scope> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveScope(domainName, domain, user, scope, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteScope(String domainName, int domain, String user, Integer scopeId, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteScope(domainName, domain, user, scopeId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getUserScopeList(String domainName, Integer domain, String user, Integer scopeId, AsyncCallback<List<UserScopeFull>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getUserScopeList(domainName, domain, user, scopeId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveUserScope(String domainName, Integer domainId, String user, UserScope userScope, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveUserScope(domainName, domainId, user, userScope, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteUserScope(String domainName, Integer domain, String user, Integer userScopeId, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteUserScope(domainName, domain, user, userScopeId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getDomainScopeList(String domainName, Integer domain, String user, Integer scopeId, AsyncCallback<List<Domain>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getDomainScopeList(domainName, domain, user, scopeId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveDomainScope(String domainName, Integer domainId, String user, Integer domainChange, Integer scopeId, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveDomainScope(domainName, domainId, user, domainChange, scopeId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteDomainScope(String domainName, Integer domainId, String user, Domain domain, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteDomainScope(domainName, domainId, user, domain, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getDomains(String domainName, Integer domainId, String user, AsyncCallback<List<Domain>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getDomains(domainName, domainId, user, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ******************************************** [TAG]
	// **************************************************

	@Override
	public void getTagList(TagParams params, AsyncCallback<List<Tag>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTagList(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveTag(String domainName, Integer domainId, String user, Tag tag, AsyncCallback<Tag> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveTag(domainName, domainId, user, tag, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteTag(String domainName, int domainId, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteTag(domainName, domainId, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSchemas(AsyncCallback<List<String>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSchemas(new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// *********************** [CUSTOMER LINKED ACTIVITY]
	// **************************************************

	@Override
	public void getCustomers(CustomersLinkedParams params, AsyncCallback<List<Customer>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomers(params, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getCustomersLinked(CustomersLinkedParams params, AsyncCallback<List<Customer>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomersLinked(params, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getCustomersNotLinked(CustomersLinkedParams params, AsyncCallback<List<Customer>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomersNotLinked(params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCustomersDomain(String domainName, Integer domainId, String user, ArrayList<Integer> customerIds,
			AsyncCallback<HashMap<Integer, Domain>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomersDomain(domainName, domainId, user, customerIds, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getActivitySummary(String domainName, String userLogin, ActivitySummaryParams params, AsyncCallback<List<ActivitySummaryObject>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getActivitySummary(domainName, userLogin, params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAviableSyncDomains(CustomersDomainSyncParams paramsDomains, AsyncCallback<List<DomainCompany>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableSyncDomains(paramsDomains, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void syncCustomer(String domainName, Integer domainId, String user, Integer customerId, DomainCompany domainCompany, boolean isSig, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.syncCustomer(domainName, domainId, user, customerId, domainCompany, isSig, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getDomainSigAddInfo(String domainName, Integer domainId, String user, Integer customerId, AsyncCallback<List<DomainSigAddInfo>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getDomainSigAddInfo(domainName, domainId, user, customerId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCustomerFeesRelatedRegistry(String domainName, int domainId, String user, Integer customerRelatedRegistry, AsyncCallback<LinkedList<Fee>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomerFeesRelatedRegistry(domainName, domainId, user, customerRelatedRegistry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryPayMethods(String domainName, Integer domainId, String user, Integer registry, AsyncCallback<List<RegistryPayMethod>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryPayMethods(domainName, domainId, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveRegistryPayMethod(String domainName, Integer domainId, String user, RegistryPayMethod registryPayMethod, AsyncCallback<RegistryPayMethod> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveRegistryPayMethod(domainName, domainId, user, registryPayMethod, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryRelationshipsByRelated(String domainName, Integer domainId, String user, Integer customerRelatedRegistry, AsyncCallback<List<RegistryRelationship>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryRelationshipsByRelated(domainName, domainId, user, customerRelatedRegistry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveCustomer(String domainName, Integer domainId, String user, Customer customer, AsyncCallback<Customer> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveCustomer(domainName, domainId, user, customer, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRegistryRelationships(String domainName, int domain, String user, AsyncCallback<List<RegistryRelationship>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRegistryRelationships(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void createBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, ProductBooking product, Fee newFee, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.createBookingProduct(domainName, domain, user, customerRelatedRegistry, product, newFee, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void updateBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Fee feeItem, ProductBooking product, Fee newFee, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.updateBookingProduct(domainName, domain, user, customerRelatedRegistry, feeItem, product, newFee, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void removeBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Fee oldFee, ProductBooking product, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.removeBookingProduct(domainName, domain, user, customerRelatedRegistry, oldFee, product, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void requestBookingInfo(String domainName, int domain, String user, ProductBooking product, Integer customerRegistry, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.requestBookingInfo(domainName, domain, user, product, customerRegistry, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// *********************** [CERTIFICATES]
	// **************************************************

	@Override
	public void getDomainUserRoles(String domainName, Integer domainId, String user, AsyncCallback<DomainUserRoles> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getDomainUserRoles(domainName, domainId, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCertificates(String domainName, Integer domainId, String user, boolean withParent, AsyncCallback<List<Certificate>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCertificates(domainName, domainId, user, withParent, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteCertificate(String domainName, Integer domainId, String user, Certificate certificate, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteCertificate(domainName, domainId, user, certificate, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void downloadCertificate(String domainName, Integer domainId, String user, Integer certificateId, String filePath, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.downloadCertificate(domainName, domainId, user, certificateId, filePath, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void verifyCertificate(String domainName, Integer domainId, String user, Integer certificateId, List<CertificateType> tags, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.verifyCertificate(domainName, domainId, user, certificateId, tags, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCertificateInfo(String domainName, Integer domainId, String user, Integer certificateId, AsyncCallback<CertificateInfo> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCertificateInfo(domainName, domainId, user, certificateId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSecondaryUsers(String domainName, Integer domainId, String user, Integer rattachId, AsyncCallback<List<SecondaryUserCertificate>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSecondaryUsers(domainName, domainId, user, rattachId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSecondaryUsersPDF(String domainName, Integer domainId, String user, Integer rattachId, AsyncCallback<String> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSecondaryUsersPDF(domainName, domainId, user, rattachId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAssignedCCCsPDF(String domainName, Integer domainId, String user, Integer rattachId, AsyncCallback<String> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAssignedCCCsPDF(domainName, domainId, user, rattachId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getIpfxNaf(String domainName, Integer domainId, String user, ArrayList<String> nssList, AsyncCallback<EmployeeSegSocial> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getIpfxNaf(domainName, domainId, user, nssList, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteSecondaryUser(String domainName, Integer domainId, String user, Integer rattachId, String ipfType, String ipf, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteSecondaryUser(domainName, domainId, user, rattachId, ipfType, ipf, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void createSecondaryUser(String domainName, Integer domainId, String user, Integer rattachId, String ipfType, String ipf, String naf, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.createSecondaryUser(domainName, domainId, user, rattachId, ipfType, ipf, naf, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// *********************** [REGISTRY ENTRY]
	// **************************************************

	@Override
	public void getCompanyFull(String domainName, int domain, String user, AsyncCallback<CompanyFull> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCompanyFull(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveCompanyFull(String domainName, int domain, String user, CompanyFull companyFull, AsyncCallback<CompanyFull> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveCompanyFull(domainName, domain, user, companyFull, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCompanyLogo(String domainName, Integer domain, String user, Integer registry, AsyncCallback<Attach> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCompanyLogo(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCompanySignature(String domainName, Integer domain, String user, Integer registry, AsyncCallback<Attach> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCompanySignature(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCalendars(String domainName, Integer domain, String user, AsyncCallback<List<Calendar>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCalendars(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAcgreements(String domainName, Integer domain, String user, AsyncCallback<List<Agreement>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAcgreements(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getWorkplaces(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<Workplace>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getWorkplaces(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteWrokplace(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteWrokplace(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveWorkplace(String domainName, Integer domainId, String user, Workplace workplace, AsyncCallback<Workplace> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveWorkplace(domainName, domainId, user, workplace, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getEnterpriseActivities(String domainName, Integer domainId, String user, Integer registry, AsyncCallback<List<Activity>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getEnterpriseActivities(domainName, domainId, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveEnterpriseActivity(String domainName, Integer domainId, String user, Activity enterpriseActivity, AsyncCallback<Activity> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveEnterpriseActivity(domainName, domainId, user, enterpriseActivity, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteEnterpriseActivity(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteEnterpriseActivity(domainName, domainId, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCnae2009List(String domainName, Integer domainId, String user, AsyncCallback<List<Cnae2009>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCnae2009List(domainName, domainId, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCnae2025List(String domainName, Integer domainId, String user, AsyncCallback<List<Cnae>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCnae2025List(domainName, domainId, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getIaeList(String domainName, Integer domainId, String user, AsyncCallback<List<Iae>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getIaeList(domainName, domainId, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRDirStaffs(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RDirStaff>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRDirStaffs(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveRDirStaff(String domainName, Integer domain, String user, RDirStaff rDirStaff, AsyncCallback<RDirStaff> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveRDirStaff(domainName, domain, user, rDirStaff, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteRDirStaff(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteRDirStaff(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRecordDatas(String domainName, Integer domain, String user, Integer registry, boolean witdhData, AsyncCallback<List<RecordData>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRecordDatas(domainName, domain, user, registry, witdhData, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRecordData(String domainName, Integer domain, String user, Integer recordDataId, AsyncCallback<RecordData> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRecordData(domainName, domain, user, recordDataId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteRecordData(String domainName, Integer domain, String user, Integer id, boolean deleteData, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteRecordData(domainName, domain, user, id, deleteData, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveRecordData(String domainName, Integer domain, String user, RecordData recordData, AsyncCallback<RecordData> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveRecordData(domainName, domain, user, recordData, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteRecordDataAttach(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteRecordDataAttach(domainName, domainId, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getRregistryBanks(String domainName, Integer domain, String user, Integer registry, AsyncCallback<List<RegistryBank>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getRregistryBanks(domainName, domain, user, registry, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveRregistryBank(String domainName, Integer domain, String user, RegistryBank registryBank, AsyncCallback<RegistryBank> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveRregistryBank(domainName, domain, user, registryBank, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteRregistryBank(String domainName, Integer domain, String user, Integer id, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteRregistryBank(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAccountsForBank(String domainName, Integer domain, String user, AsyncCallback<List<Account>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAccountsForBank(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAccountsForRegistry(String domainName, Integer domain, String user, RegistrySource registrySource, String pattern, AsyncCallback<List<Account>> callback) {
		AON.start();
		serviceAsync.getAccountsForRegistry(domainName, domain, user, registrySource, pattern, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getSignatures(String domainName, Integer domain, String user, AsyncCallback<LinkedList<Signature>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getSignatures(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteSignature(String domainName, Integer domain, String user, Integer id,	AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.deleteSignature(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveSignature(String domainName, Integer domain, String user, Signature signature, AsyncCallback<Signature> callback) throws AonCoreException {
		AON.start();
		serviceAsync.saveSignature(domainName, domain, user, signature, new AsyncCallbackWrapper<>(callback));
	}
	

	// *********************** [AMORTIZATION TYPE]
	@Override
	public void getAmortizationTypes(Occam occam, int domain, AsyncCallback<List<AmortizationType>> callback) {
		AON.start();
		serviceAsync.getAmortizationTypes(occam, domain, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void reassignScope(String domainName, Integer domainId, String user, int originScope, int finalScope, boolean deleteOrigin, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.reassignScope(domainName, domainId, user, originScope, finalScope, deleteOrigin, new AsyncCallbackWrapper<>(callback));
	}
}
