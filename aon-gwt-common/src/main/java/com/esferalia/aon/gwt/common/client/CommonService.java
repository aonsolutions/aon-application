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
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	LinkedList<PayMethod> getPayMethods(String domainName, int domain, String user);
	PayMethod savePayMethod(String domainName,int domain, String user, PayMethod payMethod) throws AonCoreException;
	void deletePayMethod(String domainName,int domain, String user, Integer id) throws AonCoreException;

	// **************************************************
	// **************************************** [INVOICE]
	// **************************************************
	LinkedList<InvoiceRegistry> getInvoiceRegistries(String domainName,int domain, String user,String query) throws AonCoreException;
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
	
	// **************************************************
	// ************************ [MARKETING ACTION TARGET]
	// **************************************************
	
	List<MarketingActionTarget> getMarketingActionTargets(MarketingActionTargetParams params) throws AonCoreException;
	void deleteMarketingActionTarget(String domainName, int domain, String user, Integer actionTargetId) throws AonCoreException;
	MarketingActionTarget saveMarketingActionTarget(String domainName, int domain, String user,MarketingActionTarget marketingActionTarget) throws AonCoreException;
	
	List<Target> getTargetSuggestion(String domainName, int domain, String user) throws AonCoreException;
	
	List<Workgroup> getAviableWorkgroups(String domainName, int domain, String user) throws AonCoreException;
	List<TaskHolder> getAviableTaskHolders(String domainName, int domain, String user, Integer workgroup) throws AonCoreException;

	List<User> getAviableServiceUsers(String domainName, int domain, String user) throws AonCoreException;
	
	// **************************************************
	// ***************************** [PROJECT COMMERCIAL]
	// **************************************************
	
	Seller getSellerByTaskHolder(String domainName, int domain, String user, int taskHolder) throws AonCoreException;
	ProjectCommercial saveProjectCommercial(String domainName, int domain, String user, ProjectCommercial projectCommercial) throws AonCoreException;
	void deleteProjectCommercial(String domainName, int domain, String user, Integer projectCommercial) throws AonCoreException;

	// **************************************************
	// ***************************************** [SELLER]
	// **************************************************
	
	List<Seller> getSellers(SellerParams params) throws AonCoreException;
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

}
