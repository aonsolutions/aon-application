package com.esferalia.aon.gwt.common.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetParams;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.occam.api.model.Newsletter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
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
	public void getPayMethods(String domainName, int domain, String user, AsyncCallback<LinkedList<PayMethod>> callback) {
		AON.start();
		serviceAsync.getPayMethods(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void savePayMethod(String domainName, int domain, String user, PayMethod payMethod, AsyncCallback<PayMethod> callback) {
		AON.start();
		serviceAsync.savePayMethod(domainName, domain, user, payMethod, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void deletePayMethod(String domainName, int domain, String user, Integer id, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.deletePayMethod(domainName, domain, user, id, new AsyncCallbackWrapper<>(callback));
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
	
	// **************************************************
	// ************************ [MARKETING ACTION TARGET]
	// **************************************************


	@Override
	public void getMarketingActionTargets(MarketingActionTargetParams params, AsyncCallback<List<MarketingActionTarget>> callback) throws AonCoreException {
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

}
