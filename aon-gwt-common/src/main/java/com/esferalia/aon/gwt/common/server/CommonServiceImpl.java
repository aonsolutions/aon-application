package com.esferalia.aon.gwt.common.server;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
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
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Aon Common Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Common", "/aon_gwt_mod200/ms/Common", "/aon_gwt_aio/ms/Common", "/aon_gwt_marketing/ms/Common"})
public class CommonServiceImpl extends AonStatelessRemoteServiceServlet implements CommonService {

	private static final long serialVersionUID = -6555645829679341214L;

	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	@Override
	public AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain,String user) {
		return AON.getConfiguration(currentDomainName, currentDomain,user, null);
	}
	@Override
	public AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain,String user, Date atDate) {
		return AON.getConfiguration(currentDomainName, currentDomain,user, atDate);
	}
	@Override
	public AonConfiguration getAonConfiguration(Occam occam) throws AonCoreException {
		return AON.getConfiguration(occam);
	}
	@Override
	public AonConfiguration getAonConfiguration(Occam occam, ConfigParams params) throws AonCoreException {
		return AON.getConfiguration(occam, params);
	}
	// **************************************************
	// *************************************** [SECURITY]
	// **************************************************
	@Override
	public User getCurrentUser(String domainName, int domain,String user) throws AonCoreException {
		return AON.getUser(domainName,domain,user); 		
	}
	
	// **************************************************
	// ************************************* [ENTERPRISE]
	// **************************************************
//	@Override
//	public LinkedList<Enterprise> getParentEnterprises(String domainName, int domain,String user,String query) throws AonCoreException {
//		return AON.getParentEnterprises(domainName, domain,user, query);		
//	}

	@Override
	public Enterprise getEnterprise(String domainName, int domain,String user, int id) throws AonCoreException {
		return AON.getEnterprise(domainName, domain,user, id);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain,String user, int enterprise) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,user, enterprise);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain,String user) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,user);
	}

	// **************************************************
	// **************************************** [ACCOUNT]
	// **************************************************
	@Override
	public LinkedList<Account> getAccounts(String domainName, int domain,String user,String query) throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
		 	?((AonStringUtils.isNumeric(query)? AonStringUtils.EMPTY:AonStringUtils.PERCENT) 
		 			+ query 
		 			+ AonStringUtils.PERCENT)
			:(query);
		return ACCOUNTING.getAccounts(domainName, domain,user,
				p ->  p.getActiveProperty().eq((byte) 1)
					.and(p.getEntryEnabledProperty().eq((byte) 1))
					.and(p.getCodeProperty().like(q)
					 .or(p.getDescriptionProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				,0,50).collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public Account getAccount(String domainName, int domain,String user,Integer id) throws AonCoreException {
		return ACCOUNTING.getAccount(domainName, domain,user, id);
	}
	@Override
	public Account getAccount(String domainName, int domain,String user,String code) throws AonCoreException {
		return ACCOUNTING.getAccount(domainName, domain,user, code);
	}
	
	@Override
	public Account save(String domainName, int domain,String user, Account account) throws AonCoreException {
		return ACCOUNTING.save(domainName, domain,user, account);
	}
	@Override
	public Account delete(String domainName, int domain,String user, Account account) throws AonCoreException {
		return ACCOUNTING.delete(domainName, domain,user, account);
	}
	@Override
	public String getAccountNextCode(String domainName, int domain,String user, String prefix) {
		return ACCOUNTING.getAccountNextCode(domainName, domain,user, prefix);
	}

	// **************************************************
	// ************************************* [PAY_METHOD]
	// **************************************************
	
	@Override
	public LinkedList<PayMethod> getPayMethods(String domainName, int domain, String user) {
		return AON.getPayMethods(domainName, domain, user);
	}
	@Override
	public PayMethod savePayMethod(String domainName, int domain, String user, PayMethod payMethod) throws AonCoreException {
		return AON.savePayMethod(domainName, domain, user, payMethod);
	}
	@Override
	public void deletePayMethod(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deletePayMethod(domainName, domain, user, id);
	}

	// **************************************************
	// **************************************** [INVOICE]
	// **************************************************
	@Override
	public LinkedList<InvoiceRegistry> getInvoiceRegistries(String domainName, int domain, String user, String query)
			throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getInvoiceRegistries(domainName, domain,user,
				p -> p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q))
				).collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public LinkedList<OldProduct> getInvoiceProducts(String domainName, int domain, String user, String query)
			throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getInvoiceProducts(domainName, domain,user
					,p -> p.getNameProperty().like(q)
						.or(p.getCodeProperty().like(q))
				).collect(Collectors.toCollection(LinkedList::new));
	}
	
	// **************************************************
	// *************************************** [REGISTRY]
	// **************************************************
	
	@Override
	public List<Customer> getCustomers(String domainName, int domain, String user, Integer account) throws AonCoreException {
		return AON.getCustomerList(domainName, account, user, f -> f.getAccountProperty().eq(account));
	}
	
	@Override
	public CustomerFull getCustomer(String domainName, int domain, String user, Integer registry) throws AonCoreException {
		return AON.getCustomerFull(domainName, domain, user, registry);
	}
	
	@Override
	public List<Supplier> getSuppliers(String domainName, int domain, String user, Integer account) throws AonCoreException {
		return AON.getSupplierList(domainName, account, user, f -> f.getAccountProperty().eq(account));
	}
	
	@Override
	public SupplierFull getSupplier(String domainName, int domain, String user, Integer registry) throws AonCoreException {
		return AON.getSupplierFull(domainName, domain, user, registry);
	}
	
	@Override
	public List<Creditor> getCreditors(String domainName, int domain, String user, Integer account) throws AonCoreException {
		return AON.getCreditorList(domainName, account, user, f -> f.getAccountProperty().eq(account));
	}
	
	@Override
	public CreditorFull getCreditor(String domainName, int domain, String user, Integer registry) throws AonCoreException {
		return AON.getCreditorFull(domainName, domain, user, registry);
	}
	
	// **************************************************
	// ************************************ [COST CENTER]
	// **************************************************
	
	@Override
	public List<ApplicationParameter> getCostCenters(String domainName, int domain, String user) throws AonCoreException {
		return AON.getCostCenters(domainName, domain, user);
	}
	
	@Override
	public void saveCostCenter(String domainName, int domain, String user, ApplicationParameter costCenter) throws AonCoreException {
		AON.saveCostCenter(domainName, domain, user, costCenter);
	}
	
	@Override
	public void deleteCostCenter(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteCostCenter(domainName, domain, user, id);
	}
	
	// **************************************************
	// *********************************** [INVEST ASSET]
	// **************************************************
	
	@Override
	public List<InvestAsset> getInvestAssets(InvestAssetParams params) throws AonCoreException {
		return AON.getInvestAssetList(params);
	}
	
	@Override
	public void deleteInvestAsset(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteInvestAsset(domainName, domain, user, id);
	}
	
	@Override
	public InvestAsset saveInvestAsset(String domainName, int domain, String user, InvestAsset investAsset) throws AonCoreException {
		return AON.saveInvestAsset(domainName, domain, user, investAsset);
	}
	
	@Override
	public List<Activity> getActivities(String domainName, int domain, String user) throws AonCoreException {
		return PAYROLL.getActivities(domainName, domain, user, f -> f.getDomainProperty().eq(domain));
	}
	@Override
	public InvestAsset getInvestAsset(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getInvestAsset(domainName, domain, user, id);
	}

	// **************************************************
	// ********************************* [LOAD PDF MODEL]
	// **************************************************
	
	@Override
	public void savePDFModel(Occam occam, IFiscalModel model, String data) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			DataResponseDAO.insertPDFModel(ctx, model, data);
		} 
	}
	
	// **************************************************
	// *************************************** [QUESTION]
	// **************************************************

	@Override
	public List<Question> getQuestions(QuestionParams params) throws AonCoreException {
		return AON.getQuestionList(params);
	}
	
	@Override
	public void deleteQuestion(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteQuestion(domainName, domain, user, id);
	}
	
	@Override
	public Question saveQuestion(String domainName, int domain, String user, Question question) throws AonCoreException {
		return AON.saveQuestion(domainName, domain, user, question);
	}
	
	@Override
	public Question getQuestion(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getQuestion(domainName, domain, user, id);
	}
	
	// **************************************************
	// **************************** [REMESA VENCIMIENTOS]
	// **************************************************

	@Override
	public FBatch getFBatch(String domainName, Integer domainId, String user, Integer fbatchId) throws AonCoreException {
		return AON.getFBatch(domainName, domainId, user, fbatchId);
	}
	
	@Override
	public FBatch createUpdateFBatch(String domainName, Integer domainId, String user, FBatch fbatch) throws AonCoreException {
		return AON.createUpdateFBatch(domainName, domainId, user, fbatch);
	}
	
	@Override
	public LinkedList<RegistryBank> getCompanyBanks(String domainName, Integer domainId, String user) throws AonCoreException {
		return AON.getCompanyRegistryBanks(domainName, domainId, user);
	}
	
	// **************************************************
	// ***************************** [MARKETING CAMPAIGN]
	// **************************************************
	
	@Override
	public List<MarketingCampaign> getMarketingCampaigns(MarketingCompaignParams params) throws AonCoreException {
		return AON.getMarketingCampaignlist(params);
	}
	
	@Override
	public void deleteMarketingCampaign(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteMarketingCampaign(domainName, domain, user, id);
	}
	
	@Override
	public MarketingCampaign saveMarketingCampaign(String domainName, int domain, String user, MarketingCampaign marketingCampaign) throws AonCoreException {
		return AON.saveMarketingCampaign(domainName, domain, user, marketingCampaign);
	}
	
	@Override
	public MarketingCampaign getMarketingCampaign(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getMarketingCampaign(domainName, domain, user, id);
	}
	
	// **************************************************
	// ******************************* [MARKETING ACTION]
	// **************************************************
	
	@Override
	public List<MarketingAction> getMarketingActions(MarketingActionParams params) throws AonCoreException {
		return AON.getMarketingActions(params);
	}
	@Override
	public MarketingAction getMarketingAction(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getMarketingAction(domainName, domain, user, id);
	}
	
	@Override
	public void deleteMarketingAction(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteMarketingAction(domainName, domain, user, id);
	}
	
	@Override
	public MarketingAction saveMarketingAction(String domainName, int domain, String user, MarketingAction marketingAction) throws AonCoreException {
		return AON.saveMarketingAction(domainName, domain, user, marketingAction);
	}
	
	@Override
	public List<News> getNewsSuggestion(String domainName, int domain, String user) throws AonCoreException {
		return AON.getNewsStream(domainName, domain, user);
	}
	
	@Override
	public List<Newsletter> getNewsletterSuggestion(String domainName, int domain, String user) throws AonCoreException {
		return AON.getNewsletterStream(domainName, domain, user);
	}
	
	@Override
	public List<Survey> getSurveySuggestion(String domainName, int domain, String user) throws AonCoreException {
		return AON.getSurveyStream(domainName, domain, user);
	}
	
	// **************************************************
	// ************************ [MARKETING ACTION TARGET]
	// **************************************************
	
	@Override
	public List<MarketingActionTarget> getMarketingActionTargets(MarketingActionTargetParams params) throws AonCoreException {
		return AON.getMarketingActionTargets(params);
	}
	
	@Override
	public void deleteMarketingActionTarget(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteMarketingActionTarget(domainName, domain, user, id);
	}
	
	@Override
	public MarketingActionTarget saveMarketingActionTarget(String domainName, int domain, String user, MarketingActionTarget marketingActionTarget) throws AonCoreException {
		return AON.saveMarketingActionTarget(domainName, domain, user, marketingActionTarget);
	}
	
	@Override
	public List<Target> getTargetSuggestion(String domainName, int domain, String user) throws AonCoreException {
		return AON.getTargetSuggestion(domainName, domain, user);
	}
	
	@Override
	public List<Workgroup> getAviableWorkgroups(String domainName, int domain, String user) throws AonCoreException {
		List<Workgroup> workgroups = AON.getWorkgroupStream(domainName, domain, user, f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
		return workgroups;
	}
	
	@Override
	public List<TaskHolder> getAviableTaskHolders(String domainName, int domain, String user, Integer workgroup) throws AonCoreException {
		List<TaskHolder> taskHolders = AON.getTaskHolderWorkgroupStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getDomainProperty().eq(domain), workgroup).collect(Collectors.toList());
		return taskHolders;
	}
	
	@Override
	public List<User> getAviableServiceUsers(String domainName, int domainId, String user) throws AonCoreException {
		List<User> usersList = AON.getDomainUserStream(domainName, domainId, user, f -> f.getTypeProperty().eq((byte)3)).collect(Collectors.toList());
		return usersList;
	}
	
	// **************************************************
	// ***************************** [PROJECT COMMERCIAL]
	// **************************************************

	@Override
	public Seller getSellerByTaskHolder(String domainName, int domain, String user, int taskHolderId) throws AonCoreException {
		TaskHolder taskHolder = AON.getTaskHolder(domainName, domain, user, f -> f.getIdProperty().eq(taskHolderId));
		Seller seller = AON.getSeller(domainName, domain, user, f -> f.getDocumentProperty().eq(taskHolder.getDocument()));
		return seller;
	}
	
	@Override
	public ProjectCommercial saveProjectCommercial(String domainName, int domain, String user, ProjectCommercial projectCommercial) throws AonCoreException {
		return AON.saveProjectCommercial(domainName, domain, user, projectCommercial);
	}
	
	@Override
	public void deleteProjectCommercial(String domainName, int domain, String user, Integer projectCommercial) throws AonCoreException {
		AON.deleteProject(new Domain().setName(domainName).setId(domain), new User().setLogin(user), projectCommercial);
	}
	
}
