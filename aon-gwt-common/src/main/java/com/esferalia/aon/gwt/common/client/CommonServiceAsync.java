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

public interface CommonServiceAsync {

	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	void getAonConfiguration(String currentDomainName, int currentDomain, String user, AsyncCallback<AonConfiguration> callback);
	void getAonConfiguration(String currentDomainName, int currentDomain, String user, Date atDate, AsyncCallback<AonConfiguration> callback);
	void getAonConfiguration(Occam occam, AsyncCallback<AonConfiguration> asyncCallback);
	void getAonConfiguration(Occam occam, ConfigParams params, AsyncCallback<AonConfiguration> asyncCallback);

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
	
	void getPayMethods(String domainName, int domain, String user,AsyncCallback<LinkedList<PayMethod>> callback);
	void savePayMethod(String domainName,int domain, String user, PayMethod payMethod ,AsyncCallback<PayMethod> callback);
	void deletePayMethod(String domainName,int domain, String user, Integer id, AsyncCallback<Void> callback);

	// **************************************************
	// **************************************** [INVOICE]
	// **************************************************
	void getInvoiceRegistries(String domainName, int domain, String user, String query, AsyncCallback<LinkedList<InvoiceRegistry>> asyncCallback);
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
	
	// **************************************************
	// ************************ [MARKETING ACTION TARGET]
	// **************************************************
	
	void getMarketingActionTargets(MarketingActionTargetParams params, AsyncCallback<List<MarketingActionTarget>> asyncCallback) throws AonCoreException;
	void deleteMarketingActionTarget(String domainName, int domain, String user, Integer actionTargetId, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveMarketingActionTarget(String domainName, int domain, String user,MarketingActionTarget marketingActionTarget, AsyncCallback<MarketingActionTarget> asyncCallback) throws AonCoreException;
	
	void getTargetSuggestion(String domainName, int domain, String user, AsyncCallback<List<Target>> asyncCallback) throws AonCoreException;
	
	void getAviableWorkgroups(String domainName, int domain, String user, AsyncCallback<List<Workgroup>> asyncCallback) throws AonCoreException;
	void getAviableTaskHolders(String domainName, int domain, String user, Integer workgroup, AsyncCallback<List<TaskHolder>> asyncCallback) throws AonCoreException;

	void getAviableServiceUsers(String domainName, int domain, String user, AsyncCallback<List<User>> asyncCallback) throws AonCoreException;
	
}
