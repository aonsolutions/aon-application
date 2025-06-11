package com.esferalia.aon.gwt.common.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
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
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
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
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.occam.api.model.registry.SellerWorkloadContent;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.registry.TargetFull;
import com.esferalia.aon.occam.api.model.sales.SalesParams;
import com.esferalia.aon.occam.api.model.security.User;
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
	public void getInvoiceRegistries(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<InvoiceRegistry>> callback) {
		AON.start();
		serviceAsync.getInvoiceRegistries(domainName, domain, user, query, new AsyncCallbackWrapper<>(callback));
	}

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
	public void getAviableWorkgroups(String domainName, int domain, String user, AsyncCallback<List<Workgroup>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableWorkgroups(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
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
	public void getAviableProjectType(String domainName, int domain, String user, AsyncCallback<List<ProjectType>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getAviableProjectType(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
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
	public void getProduct(String domainName, Integer domain, String user, Integer id, AsyncCallback<Product> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getProduct(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
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
	public void createProduct(String domainName, Integer domain, String user, Product product, List<ProductTag> productTags, Item item, AsyncCallback<Product> callback) throws AonCoreException {
		AON.start();
		serviceAsync.createProduct(domainName, domain, user, product, productTags, item, new AsyncCallbackWrapper<>(callback));
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
	public void getTaskHolders(String domainName, Integer domain, String user, AsyncCallback<List<TaskHolder>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getTaskHolders(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getActivityTypes(String domainName, int domain, String user, AsyncCallback<List<ActivityType>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getActivityTypes(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	// **************************************************
	// ********************************* [CUSTOMER NOTES]
	// **************************************************

	@Override
	public void getCustomerNotes(String domainName, int domain, String user, Integer customerId, AsyncCallback<List<RegistryNote>> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getCustomerNotes(domainName, domain, user, customerId, new AsyncCallbackWrapper<>(callback));
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
	public void getInvoicePDF(String domainName, int domain, String user, Integer invoiceId, AsyncCallback<String> callback) throws AonCoreException {
		AON.start();
		serviceAsync.getInvoicePDF(domainName, domain, user, invoiceId, new AsyncCallbackWrapper<>(callback));
	}

}
