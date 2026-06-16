package com.esferalia.aon.gwt.common.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.SECURITY;
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
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.Filter.ProductTagFilter;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetParams;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetMassiveParams;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.occam.api.model.Newsletter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.PayMethodParams;
import com.esferalia.aon.occam.api.model.ProjectParams;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.api.model.Relationship;
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
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.occam.api.model.customer.CustomersDomainSyncParams;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
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
import com.esferalia.aon.occam.api.model.project.ProjectTas;
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
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.in.pdf.maker.PdfMaker;
import solutions.aon.aws.ses.SES;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.sepe.Sepe;

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
	@Override
	public Domain getParentDomain(String domainName, int domain, String user, Integer domainId) throws AonCoreException {
		Domain doamin = AON.getDomain(domainName, domain, user, f -> f.getIdProperty().eq(domainId));
		return AON.getDomain(domainName, domain, user, f -> f.getIdProperty().eq(doamin.getParentId()));
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
	public LinkedList<PayMethod> getPayMethods(String domainName, int domain, String user) throws AonCoreException {
		return AON.getPayMethods(domainName, domain, user);
	}
	@Override
	public LinkedList<PayMethod> getPayMethods(PayMethodParams params) throws AonCoreException {
		return AON.getPayMethods(params);
	}
	@Override
	public PayMethod savePayMethod(String domainName, int domain, String user, PayMethod payMethod) throws AonCoreException {
		return AON.savePayMethod(domainName, domain, user, payMethod);
	}
	@Override
	public void deletePayMethod(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deletePayMethod(domainName, domain, user, id);
	}
	@Override
	public void groupPayMethod(String domainName, int domain, String user, List<PayMethod> selectedPaymethodList, PayMethod groupedPaymthod) throws AonCoreException {
		AON.groupPayMethod(domainName, domain, user, selectedPaymethodList, groupedPaymthod);
	}

	// **************************************************
	// **************************************** [INVOICE]
	// **************************************************
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
	

	@Override
	public List<Tag> getTagSuggestion(String domainName, int domain, String user, TagType tagType) throws AonCoreException {
		return AON.getTagStream(domainName, domain, user, f -> f.getDomainProperty().eq(domain).and(f.getTypeProperty().eq((byte) tagType.ordinal()))).collect(Collectors.toList());
	}
	
	// **************************************************
	// ************************ [MARKETING ACTION TARGET]
	// **************************************************
	
	@Override
	public List<MarketingActionTarget> getMarketingActionTargets(MarketingActionTargetParams params) throws AonCoreException {
		return AON.getMarketingActionTargets(params);
	}
	

	@Override
	public List<MarketingActionTarget> getMarketingActionTargets(MarketingActionTargetMassiveParams params) throws AonCoreException {
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
	public List<ProjectType> getAviableProjectTypes(String domainName, int domain, String user, int domainSearch) throws AonCoreException {
		return AON.getProjectTypeStream(
				new Domain().setName(domainName).setId(domain), 
				new User().setLogin(user), 
				f -> f.getDomainProperty().eq(domainSearch).and(f.getActiveProperty().eq((byte)1))
			).collect(Collectors.toList());
	}
	
	@Override
	public List<Workgroup> getAviableWorkgroups(String domainName, int domain, String user, Integer domainSearch) throws AonCoreException {
		List<Workgroup> workgroups = AON.getWorkgroupStream(domainName, domain, user, f -> f.getDomainProperty().eq(domainSearch)).collect(Collectors.toList());
		return workgroups;
	}
	
	@Override
	public List<TaskHolder> getAviableTaskHolders(String domainName, int domain, String user, Integer workgroup) throws AonCoreException {
		List<TaskHolder> taskHolders = AON.getTaskHolderWorkgroupStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getDomainProperty().eq(domain), workgroup, 0, Integer.MAX_VALUE).filter(taskHolder -> taskHolder.isActive()).collect(Collectors.toList());
//		return taskHolders;
		Integer[] taskHolderIds = new Integer[taskHolders.size()];
		taskHolders.stream().map(taskHolder -> taskHolder.getId()).collect(Collectors.toList()).toArray(taskHolderIds);
		LinkedList<Seller> sellerList = AON.getSellerList(domainName, domain, user, f -> f.getStatusProperty().eq((byte)0).and(f.getTaskHolderProperty().in(taskHolderIds)));
		
		return sellerList.stream().map(seller -> seller.getTaskHolder()).collect(Collectors.toList());
	}
	
	@Override
	public List<User> getAviableServiceUsers(String domainName, int domainId, String user) throws AonCoreException {
		List<User> usersList = AON.getDomainUserStream(domainName, domainId, user, f -> f.getTypeProperty().eq((byte)3)).collect(Collectors.toList());
		return usersList;
	}
	
	@Override
	public List<ProjectActivity> getAviableProjectActivity(String domainName, int domain, String user) throws AonCoreException {
		return AON.getProjectActivityStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
	}
	
	@Override
	public List<ProjectType> getAviableProjectType(String domainName, int domain, String user, Integer domainSearch) throws AonCoreException {
		return AON.getProjectTypeStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getDomainProperty().eq(domainSearch)).collect(Collectors.toList());
	}
	
	// **************************************************
	// ***************************** [PROJECT COMMERCIAL]
	// **************************************************

	@Override
	public Seller getSellerByTaskHolder(String domainName, int domain, String user, int taskHolderId) throws AonCoreException {
		Seller seller = AON.getSeller(domainName, domain, user, f -> f.getTaskHolderProperty().eq(taskHolderId));
		return seller;
	}
	
	@Override
	public Seller getNextLinealSellerByWorkgroup(String domainName, int domain, String user, int workgroup) throws AonCoreException {
		List<TaskHolder> taskHolders = AON.getTaskHolderWorkgroupStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getDomainProperty().eq(domain), workgroup, 0, Integer.MAX_VALUE).filter(taskHolder -> taskHolder.isActive()).collect(Collectors.toList());
		Integer[] taskHolderIds = new Integer[taskHolders.size()];
		taskHolders.stream().map(taskHolder -> taskHolder.getId()).collect(Collectors.toList()).toArray(taskHolderIds);
		LinkedList<Seller> sellerList = AON.getSellerList(domainName, domain, user, f -> f.getStatusProperty().eq((byte)0).and(f.getTaskHolderProperty().in(taskHolderIds)));
		Map<Seller, Date> sellerProjects = new HashMap<Seller, Date>();
		
		for(Seller seller : sellerList) {
			LinkedList<ProjectCommercial> projectCommercials = AON.getProjectCommercialList(domainName, domain, user, f -> f.getSellerProperty().eq(seller.getId()));
			
			// Si esta activo y no tiene ninguna operacion comercial se devuelve este
			if(projectCommercials.isEmpty()) return seller;
			
			projectCommercials.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
			sellerProjects.put(seller, projectCommercials.get(0).getDate());
		}
		
		Optional<Date> oldestDate = sellerProjects.values().stream().sorted((d1, d2) -> d1.compareTo(d2)).findFirst();
		if(oldestDate.isEmpty()) return null;
		else {
			for(Entry<Seller, Date> entry : sellerProjects.entrySet()) {
				if(entry.getValue().equals(oldestDate.get())) return entry.getKey();
			}
			return null;
		}
	}
	
	@Override
	public ProjectCommercial saveProjectCommercial(String domainName, int domain, String user, ProjectCommercial projectCommercial) throws AonCoreException {
		return AON.saveProjectCommercial(domainName, domain, user, projectCommercial);
	}
	
	@Override
	public void deleteProjectCommercial(String domainName, int domain, String user, Integer projectCommercial) throws AonCoreException {
		AON.deleteProject(new Domain().setName(domainName).setId(domain), new User().setLogin(user), projectCommercial);
	}
	
	// **************************************************
	// ***************************************** [SELLER]
	// **************************************************

	@Override
	public List<Seller> getSellers(SellerParams params) throws AonCoreException {
		List<Seller> sellers =  AON.getSellerList(params);
		return sellers;
	}
	
	@Override
	public Integer getSellersCount(SellerParams params) throws AonCoreException {
		return AON.getSellerListCount(params);
	}
	
	@Override
	public Seller getSeller(String domainName, int domain, String user, Integer id) throws AonCoreException {
		Seller seller = AON.getSeller(domainName, domain, user, id);
		return seller;
	}
	
	@Override
	public Seller saveSeller(String domainName, int domain, String user, Seller seller) throws AonCoreException {
		return AON.saveSeller(domainName, domain, user, seller);
	}
	
	@Override
	public void deleteSeller(String domainName, int domain, String user, Integer sellerId) throws AonCoreException {
		AON.deleteSeller(domainName, domain, user, sellerId);
	}
	
	@Override
	public List<CommissionType> getAviableCommisionTypes(String domainName, int domain, String user) throws AonCoreException {
		return AON.getCommissionTypeStream(domainName, domain, user, f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
	}
	
	@Override
	public List<TaskHolder> getAviableSellerTaskHolders(String domainName, int domain, String user) throws AonCoreException {
		List<TaskHolder> taskHolders = AON.getAviableSellerTaskHolders(domainName, domain, user);
		return taskHolders;
	}
	
	@Override
	public List<Workgroup> getTaskHolderWorkgroups(String domainName, int domain, String user, Integer taskHolderId) throws AonCoreException {
		return AON.getWorkgroupByTaskHolderStream(domainName, domain, user, f -> f.getDomainProperty().eq(domain), taskHolderId).collect(Collectors.toList());
	}
	
	@Override
	public User getTaskHolderUser(String domainName, int domain, String user, Integer userId) throws AonCoreException {
		return AON.getUser(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getIdProperty().eq(userId));
	}
	
	@Override
	public List<RegistryAddress> getRegistryAddresses(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		return AON.getRegistryAddressStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getRegistryProperty().eq(registry)).collect(Collectors.toList());
	}
	
	@Override
	public RegistryAddress getRegistryAddress(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		return AON.getRegistryAddress(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getIdProperty().eq(id));
	}
	
	@Override
	public RegistryAddress saveRegistryAddress(String domainName, Integer domain, String user, RegistryAddress registryAddress) throws AonCoreException {
		return AON.save(new Domain().setName(domainName).setId(domain), new User().setLogin(user), registryAddress);
	}
	 
	@Override
	public void deleteRegistryAddress(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteRegistryAddress(domainName, domain, user, id);
	}
	
	@Override
	public List<RegistryMedia> getRegistryMedias(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		return AON.getRegistryMediaStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getRegistryProperty().eq(registry)).collect(Collectors.toList());
	}
	
	@Override
	public RegistryMedia getRegistryMedia(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		return AON.getRegistryMedia(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getIdProperty().eq(id));
	}
	
	@Override
	public RegistryMedia saveRegistryMedia(String domainName, Integer domain, String user, RegistryMedia registryMedia) throws AonCoreException {
		return AON.save(new Domain().setName(domainName).setId(domain), new User().setLogin(user), registryMedia);
	}
	
	@Override
	public void deleteRegistryMedia(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteRegistryMedia(domainName, domain, user, id);
	}
	
	@Override
	public List<RegistryAddInfo> getRegistryAddInfos(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		return AON.getRegistryAddInfoStream(domainName, domain, user, f -> f.getRegistryProperty().eq(registry)).collect(Collectors.toList());
	}
	
	@Override
	public RegistryAddInfo getRegistryAddInfo(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		return AON.getRegistryAddInfo(domainName, domain, user, f -> f.getIdProperty().eq(id)).get();
	}
	
	@Override
	public RegistryAddInfo saveRegistryAddInfo(String domainName, Integer domain, String user, RegistryAddInfo registryAddInfo) throws AonCoreException {
		return AON.save(new Domain().setName(domainName).setId(domain), new User().setLogin(user), registryAddInfo);
	}
	
	@Override
	public void deleteRegistryAddInfo(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteRegistryAddInfo(new Domain().setName(domainName).setId(domain), user, id);
	}

	@Override
	public List<String> getRAddInfoAviableAttributes(String domainName, Integer domain, String user) throws AonCoreException {
		return AON.getRAddInfoAviableAttributes(domainName, domain, user);
	}
	
	@Override
	public List<Attach> getRegistryAttaches(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		return AON.getAttachStream(domainName, domain, user, f -> f.getAttachModuleProperty().eq(registry), AttachType.REGISTRY, false).collect(Collectors.toList());
	}
	
	@Override
	public Attach getRegistryAttach(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		return AON.getAttach(domainName, domain, user, f -> f.getIdProperty().eq(id), AttachType.REGISTRY);
	}
	
	@Override
	public Attach saveRegistryAttach(String domainName, Integer domain, String user, Attach attach) throws AonCoreException {
		return AON.save(new Domain().setName(domainName).setId(domain), new User().setLogin(user), attach);
	}
	
	@Override
	public void deleteRegistryAttach(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteAttach(domainName, domain, user, f -> f.getIdProperty().eq(id), AttachType.REGISTRY);
	}
	
	@Override
	public List<Category> getAviableCategories(String domainName, Integer domain, String user) throws AonCoreException {
		return AON_SOLUTIONS.getCategoryStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
	}
	
	// **************************************************
	// ********************************* [SELLER WORKLOAD]
	// **************************************************

	@Override
	public List<SellerWorkload> getSellersWorkload(SellerWorkloadParams params) throws AonCoreException {
		List<SellerWorkload> sellersWorkload =  AON.getSellerWorkloadList(params);
		return sellersWorkload;
	}
	
	@Override
	public Integer getSellersWorkloadCount(SellerWorkloadParams params) throws AonCoreException {
		return AON.getSellerWorkloadListCount(params);
	}
	@Override
	public SellerWorkloadContent getSellersWorkloadContent(SellerWorkloadParams params) throws AonCoreException {
		SellerWorkloadContent sellersWorkloadFees =  AON.getSellersWorkloadContent(params);
		return sellersWorkloadFees;
	}
	
	
	// **************************************************
	// **************************************** [PRODUCT]
	// **************************************************

	@Override
	public List<Product> getProducts(ProductParams params) throws AonCoreException {
		List<Product> products = AON.getProductList(new Domain().setName(params.getDomainName()).setId(params.getDomain()), params.getUser(), params);
		return products;
	}
	
	@Override
	public List<ProductBooking> getProductsBooking(ProductParams params) throws AonCoreException {
		List<ProductBooking> products = AON.getProductBookingList(new Domain().setName(params.getDomainName()).setId(params.getDomain()), params.getUser(), params);
		return products;
	}
	
	@Override
	public Product getProduct(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		return AON.getProduct(new Domain().setName(domainName).setId(domain), user, f -> f.getIdProperty().eq(id));
	}
	
	@Override
	public ProductBooking getProductBooking(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		return AON.getProductBooking(new Domain().setName(domainName).setId(domain), user, f -> f.getIdProperty().eq(id));
	}
	
	@Override
	public void deleteProduct(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteProduct(new Domain().setName(domainName).setId(domain), user, id);
	}
	
	@Override
	public Product saveProduct(String domainName, Integer domain, String user, Product product) throws AonCoreException {
		return AON.saveProduct(new Domain().setName(domainName).setId(domain), user, product);
	}
	
	@Override
	public ProductBooking saveProductBooking(String domainName, Integer domain, String user, ProductBooking product) throws AonCoreException {
		return AON.saveProductBooking(new Domain().setName(domainName).setId(domain), user, product);
	}
	
	@Override
	public Product createProduct(String domainName, Integer domain, String user, Product product, List<ProductTag> productTags, Item item) throws AonCoreException {
		return AON.createProduct(new Domain().setName(domainName).setId(domain), user, product, productTags, item);
	}
	
	@Override
	public ProductBooking createProductBooking(String domainName, Integer domain, String user, ProductBooking product, List<ProductTag> productTags, Item item) throws AonCoreException {
		return AON.createProductBooking(new Domain().setName(domainName).setId(domain), user, product, productTags, item);
	}

	@Override
	public Item getItem(String domainName, Integer domain, String user, Integer productId) throws AonCoreException {
		return AON.getItem(new Domain().setName(domainName).setId(domain), user, f -> f.getProductProperty().eq(productId), new Options().setFull(true));
	}
	
	@Override
	public Item saveItem(String domainName, int domain, String user, Item item) throws AonCoreException {
		return AON.saveItem(new Domain().setName(domainName).setId(domain), user, item);
	}
	
	@Override
	public List<ProductCategory> getProductCategories(String domainName, Integer domain, String user) throws AonCoreException {
		return AON.getProductCategoryList(domainName, domain, user, f -> f.getDomainProperty().eq(domain));
	}
	
	@Override
	public List<Tax> getTaxTypes(String domainName, Integer domain, String user, TaxType taxType) throws AonCoreException {
		Occam occam = new Occam().setDomainName(domainName).setDomain(domain).setUser(user);
		TaxFilter filter = f -> f.getTaxTypeProperty().eq(taxType.value());
		return AON.getTaxStream(occam,domain,filter).collect(Collectors.toList());
	}
	
	@Override
	public List<ProductTag> getProductTags(String domainName, Integer domain, String user, Integer productId) throws AonCoreException {
		ProductTagFilter filter = f -> f.getDomainProperty().eq(domain);
		if(null != productId) filter = f -> f.getDomainProperty().eq(domain).and(f.getProductProperty().eq(productId));
		List<ProductTag> productTags = AON.getProductTagStream(domainName, domain, user, filter).collect(Collectors.toList());
		return productTags;
	}
	
	@Override
	public List<Tag> getTags(String domainName, Integer domain, String user) throws AonCoreException {
		List<Tag> tags = AON.getTagList(domainName, domain, user, f -> f.getDomainProperty().eq(domain).and(f.getTypeProperty().eq((byte)1)));;
		return tags;
	}
	
	@Override
	public void saveProductTags(String domainName, int domain, String user, Integer productId, List<ProductTag> productTags) throws AonCoreException {
		AON.deleteProductTag(
				new Domain().setName(domainName).setId(domain), 
				user,
				AON.getProductTagStream(domainName, domain, user, f -> f.getDomainProperty().eq(domain).and(f.getProductProperty().eq(productId)))
		);
		
		AON.insertProductTag(new Domain().setName(domainName).setId(domain), user, productTags.stream());
		
	}
	
	@Override
	public List<Tariff> getTariffs(String domainName, int domain, String user) throws AonCoreException {
		return AON.getTariffStream(new Domain().setName(domainName).setId(domain), user, f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
	}
	
	@Override
	public List<ItemTariff> getItemTariffs(String domainName, int domain, String user, Integer id) throws AonCoreException {
		List<ItemTariff> itemTariffs = AON.getItemTariffStream(new Domain().setName(domainName).setId(domain), user, null != id ? f -> f.getDomainProperty().eq(domain).and(f.getItemProperty().eq(id)) : f -> f.getDomainProperty().eq(domain)).collect(Collectors.toList());
		return itemTariffs;
	}
	
	@Override
	public void deleteItemTariff(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteItemTariff(new Domain().setName(domainName).setId(domain), user, id);
	}
	
	@Override
	public ItemTariff saveItemTariff(String domainName, int domain, String user, ItemTariff itemTariff) throws AonCoreException {
		return AON.saveItemTariff(new Domain().setName(domainName).setId(domain), user, itemTariff);
	}
	
	@Override
	public List<Item> getItems(String domainName, int domain, String user, ProductType productType) throws AonCoreException {
		return AON.getItemStream(
				new Domain().setName(domainName).setId(domain), 
				user, 
				f -> f.getDomainProperty().eq(domain).and(null == productType ? f.getProductTypeProperty().isNotNull() : f.getProductTypeProperty().eq((byte)productType.ordinal()))				
		).collect(Collectors.toList());
	}
	
	@Override
	public List<ItemComposition> getItemCompositions(String domainName, int domain, String user, Integer itemId) throws AonCoreException {
		return AON.getItemCompositionStream(new Domain().setName(domainName).setId(domain), user, f -> f.getDomainProperty().eq(domain).and(f.getItemProperty().eq(itemId))).collect(Collectors.toList());
	}
	
	@Override
	public void deleteItemComposition(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deletItemComposition(new Domain().setName(domainName).setId(domain), user, id);
	}
	
	@Override
	public void deleteItemCompositions(String domainName, int domain, String user, List<Integer> itemCompositions)throws AonCoreException {
		AON.deletItemCompositions(new Domain().setName(domainName).setId(domain), user, itemCompositions);
	}

	@Override
	public ItemComposition saveItemComposition(String domainName, int domain, String user, ItemComposition itemComposition) throws AonCoreException {
		return AON.saveItemComposition(new Domain().setName(domainName).setId(domain), user, itemComposition);
	}
	
	@Override
	public List<ItemComposition> saveItemCompositions(String domainName, int domain, String user, List<ItemComposition> itemCompositions) throws AonCoreException {
		return AON.saveItemCompositions(new Domain().setName(domainName).setId(domain), user, itemCompositions);
	}
	
	@Override
	public List<ItemAddInfo> getItemAddInfos(String domainName, int domain, String user, Integer itemId) throws AonCoreException {
		List<ItemAddInfo> addInfoList = AON.getItemAddInfoStream(domainName, domain, user, f -> f.getDomainProperty().eq(domain).and(f.getItemProperty().eq(itemId))).collect(Collectors.toList());
		return addInfoList;
	}
	
	@Override
	public void saveItemAddInfos(String domainName, int domain, String user, List<ItemAddInfo> itemAddInfoList) throws AonCoreException {
		itemAddInfoList.forEach(itemAddInfo -> {
			if(AonStringUtils.isBlank(itemAddInfo.getValue()) && null != itemAddInfo.getId())
				AON.deleteItemAddInfo(domainName, domain, user, itemAddInfo.getId());
			else
				AON.saveItemAddInfo(domainName, domain, user, itemAddInfo);
		});
	}
	
	// **************************************************
	// ***************************************** [TARIFF]
	// **************************************************

	@Override
	public List<Tariff> getTariffs(TariffParams params) throws AonCoreException {
		List<Tariff> tariffs = AON.getTariffList(new Domain().setName(params.getDomainName()).setId(params.getDomain()), params.getUser(), params);
		return tariffs;
	}
	
	@Override
	public Tariff getTariff(String domainName, int domain, String user, Integer tariffId) throws AonCoreException {
		return AON.getTariffStream(new Domain().setName(domainName).setId(domain), user, f -> f.getIdProperty().eq(tariffId)).findFirst().get();
	}
	
	@Override
	public void deleteTariff(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteTariff(new Domain().setName(domainName).setId(domain), user, id);	
	}
	
	@Override
	public Tariff saveTariff(String domainName, int domain, String user, Tariff tariff) throws AonCoreException {
		return AON.saveTariff(new Domain().setName(domainName).setId(domain), user, tariff);
	}
	
	@Override
	public List<TariffAddInfo> getTariffAddInfoList(String domainName, int domain, String user, Integer tariffId) throws AonCoreException {
		return AON.getTariffAddInfoList(new Domain().setName(domainName).setId(domain), user, tariffId);
	}
	
	@Override
	public TariffAddInfo saveTariffAddInfo(String domainName, int domain, String user, TariffAddInfo tariffAddInfo) throws AonCoreException {
		return AON.saveTariffAddInfo(new Domain().setName(domainName).setId(domain), user, tariffAddInfo);
	}
	
	@Override
	public void deleteTariffAddInfo(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteTariffAddInfo(new Domain().setName(domainName).setId(domain), user, id);
	}
	
	@Override
	public List<TariffCatalogue> getTariffCatalgueList(String domainName, int domain, String user, Integer tariffId) throws AonCoreException {
		return AON.getTariffCatalgueList(new Domain().setName(domainName).setId(domain), user, tariffId);
	}
	
	@Override
	public TariffCatalogue saveTariffCatalogue(String domainName, int domain, String user, TariffCatalogue tariffCatalogue) throws AonCoreException {
		return AON.saveTariffCatalogue(new Domain().setName(domainName).setId(domain), user, tariffCatalogue);
	}
	
	@Override
	public void deleteTariffCatalogue(String domainName, int domain, String user, Integer id) throws AonCoreException {
		AON.deleteTariffCatalogue(new Domain().setName(domainName).setId(domain), user, id);
	}
	
	@Override
	public List<Catalogue> getCatalogueList(String domainName, int domain, String user) throws AonCoreException {
		return AON.getCatalogueList(new Domain().setName(domainName).setId(domain), user, f -> f.getDomainProperty().eq(domain));
	}

	@Override
	public Domain getOfficeSibling(String domainName, int domain, String user) throws AonCoreException {
		Domain currentDomain = AON.getDomain(domainName, domain, user);
		Optional<RegistryRelationship> rrletationShip = AON_SOLUTIONS.getRegistryRelationship(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getCommentsProperty().eq(currentDomain.getName()));
		
		Domain officeSiblingDomain = null;
		if(rrletationShip.isPresent())
			officeSiblingDomain = AON.getDomain(domainName, domain, user, f -> f.getIdProperty().eq(rrletationShip.get().getDomain().getId()));
		else 
			officeSiblingDomain = AON.getDomain(domainName, domain, user, f -> f.getTypeProperty().eq(DomainType.OFFICE.value()).and(f.getActiveProperty().eq((byte)1)));
		
		return officeSiblingDomain;
	}
	
	// **************************************************
	// ****************************************** [SALES]
	// **************************************************
	
	@Override
	public List<Sales> getSales(SalesParams params) throws AonCoreException {
		return AON.getSales(params);
	}
	
	@Override
	public Sales getSale(String domainName, int domain, String user, Integer saleId) throws AonCoreException {
		return AON.getSales(new Domain().setName(domainName).setId(domain), user, f -> f.getIdProperty().eq(saleId), new Options().setFull(true));
	}
	
	@Override
	public List<Workplace> getWorkplaces(String domainName, int domain, String user) throws AonCoreException {
		return AON.getWorkplaceList(domainName, domain, user, f -> f.getDomainProperty().eq(domain).and(f.getActiveProperty().eq((byte)1)));
	}
	
	@Override
	public List<Seller> getTaskHolderUsers(String domainName, int domain, String user) throws AonCoreException {
		List<Seller> list = AON.getTaskHolderSellerStream(
				new Domain().setName(domainName).setId(domain), user);
		
		// Filter user from parent domain
		list = list.stream()
			.filter(seller -> !seller.getTaskHolder().getDomain().getId().equals(seller.getTaskHolder().getUser().getDomain().getId()))
			.collect(Collectors.toList());
		
		return list;
	}
	
	@Override
	public Map<TargetFull, List<RegistrySeller>> getTargetNotUserFull(TargetParams params) throws AonCoreException {
		List<TargetFull> targetFullList = AON.getTargetNotUserFull(new Domain().setName(params.getDomainName()).setId(params.getDomain()), params.getUser(), params);
		
		Map<TargetFull, List<RegistrySeller>> resultMap = targetFullList.stream().collect(
		    Collectors.toMap(
		        Function.identity(),
		        targetFull -> AON.getRegistrySellerStream(
		            new Domain().setName(params.getDomainName()).setId(params.getDomain()), 
		            params.getUser(),
		            f -> f.getDomainProperty().eq(params.getDomain())
		                 .and(f.getRegistryProperty().eq(targetFull.getId()))
		        ).collect(Collectors.toList()),
		        (v1, v2) -> v1,
		        LinkedHashMap::new
		    )
		);

		
		return resultMap;
	}
	
	@Override
	public Customer getCustomerByDocument(String domainName, int domain, String user, String document) throws AonCoreException {
		return AON.getCustomer(domainName, domain, user, f -> f.getDocumentProperty().eq(document).and(f.getDomainProperty().eq(domain)));
	}
	
	@Override
	public TargetFull getTargetFull(String domainName, int domain, String user, Integer registry) throws AonCoreException {
		return AON.getTargetFull(domainName, registry, user, registry);
	}
	@Override
	public Company getCompanyByDocument(String domainName, int domainId, String user, String document) throws AonCoreException {
		Domain domain = AON.getDomain(domainName, domainId, user, f -> f.getIdProperty().eq(domainId));
		List<Integer> silbingDomains = AON.getDomainList(domainName, domainId, user, 
				f -> f.getParentProperty().eq(domain.getParentId()))
			.stream()
			.map(domainIt -> domainIt.getId())
			.collect(Collectors.toList());
		
		return AON.getCompany(
				new Domain().setName(domainName).setId(domainId), 
				new User().setLogin(user).setName(user), 
				f -> f.getDocumentProperty().eq(document).and(f.getDomainProperty().in(silbingDomains.toArray(new Integer[0]))));
	}
	
	@Override
	public Seller getSellerByUserLogin(String domainName, int domainId, String user, Integer targetId) throws AonCoreException {
		Domain domain = AON.getDomain(domainName, domainId, user, f -> f.getIdProperty().eq(domainId));
	    Integer parentDomain = domain.isParent() ? domain.getId() : domain.getParentId();
	    Integer[] domains = new Integer[]{domain.getId(), parentDomain};

	    AtomicReference<Seller> sellerResult = new AtomicReference<>();

	    Optional<Target> target = AON.getTarget(domainName, domainId, user, targetId);
	    if (target.isPresent() && target.get().getCreationUser() != null) {
	        Stream<User> userCreators = AON.getUserStream(domainName, domainId, user,
	            f -> f.getLoginProperty().eq(target.get().getCreationUser()).and(f.getDomainProperty().in(domains)),
	            new Options().setFull(false));

	        userCreators
	            .sorted((o1, o2) -> o2.getDomain().getId().compareTo(o1.getDomain().getId()))
	            .anyMatch(userCreator -> {
	                if (userCreator != null && userCreator.getId() != null) {
	                    TaskHolder taskHolder = AON.getTaskHolder(domainName, domainId, user, f -> f.getUserIdProperty().eq(userCreator.getId()));
	                    if (taskHolder != null && taskHolder.getId() != null) {
	                        Seller seller = AON.getSeller(domainName, domainId, user, f -> f.getTaskHolderProperty().eq(taskHolder.getId()));
	                        if (seller != null && seller.getId() != null) {
	                            sellerResult.set(seller);
	                            return true; // salir del anyMatch
	                        }
	                    }
	                }
	                return false;
	            });
	    }
	    
	    return sellerResult.get();
	}
	
	// **************************************************
	// **************************************** [PROJECT]
	// **************************************************
	
	@Override
	public List<Project> getProjects(ProjectParams params) throws AonCoreException {
		List<Project>  projects = AON.getProjectList(params);
		return projects;
	}
	
	@Override
	public Project getProject(String domainName, int domain, String user, Integer projectId) throws AonCoreException {
		return AON.getProjectFull(domainName, domain, user, f -> f.getIdProperty().eq(projectId));
	}
	
	@Override
	public void deleteProject(String domainName, int domain, String user, Integer projectId) throws AonCoreException {
		AON.deleteProject(new Domain().setName(domainName).setId(domain), new User().setName(user).setLogin(user), projectId);
	}
	
	@Override
	public Project saveProject(String domainName, int domain, String user, Project project) throws AonCoreException {
		return AON.saveProject(new Domain().setName(domainName).setId(domain), new User().setName(user).setLogin(user), project);
	}
	
	@Override
	public Integer getProjectsCount(ProjectParams params) throws AonCoreException {
		return AON.getProjectsCount(params);
	}
	
	@Override
	public List<ProjectHolder> getProjectHolders(String domainName, int domain, String user, Integer projectId)throws AonCoreException {
		List<ProjectHolder> projectHolders = AON.getProjectHolderList(new Domain().setName(domainName).setId(domain), new User().setName(user).setLogin(user), f -> f.getProjectProperty().eq(projectId));
		return projectHolders;
	}
	
	@Override
	public ProjectHolder saveProjectHolder(String domainName, int domain, String user, ProjectHolder projectHolder)	throws AonCoreException {
		return AON.saveProjectHolder(new Domain().setName(domainName).setId(domain), new User().setName(user).setLogin(user), projectHolder);
	}
	
	@Override
	public void deleteProjectHolder(String domainName, int domain, String user, Integer projectHolderId) throws AonCoreException {
		AON.deleteProjectHolder(new Domain().setName(domainName).setId(domain), new User().setName(user).setLogin(user), projectHolderId);
	}

	@Override
	public List<TaskHolder> getTaskHolders(String domainName, Integer domain, String user, Integer domainSearch) throws AonCoreException {
		return AON.getTaskHolderStream(
				new Domain().setName(domainName).setId(domain), 
				new User().setName(user).setLogin(user), 
				f -> f.getDomainProperty().eq(domainSearch).and(f.getActiveProperty().eq((byte)1)), 
				new Options().setFull(true))
				.collect(Collectors.toList());
	}
	@Override
	public List<ActivityType> getActivityTypes(String domainName, int domain, String user, Integer domainSearch) throws AonCoreException {
		return AON.getActivityTypeStream(
				new Domain().setName(domainName).setId(domain), 
				new User().setName(user).setLogin(user), 
				f -> f.getDomainProperty().eq(domainSearch).and(f.getActiveProperty().eq((byte)1)))
				.collect(Collectors.toList());
	}

	// **************************************************
	// ********************************* [CUSTOMER NOTES]
	// **************************************************

	@Override
	public List<RegistryNote> getRegistryNotes(String domainName, int domain, String user, Integer customerId) throws AonCoreException {
		return AON.getRegistryNoteStream(
				new Domain().setName(domainName).setId(domain), 
				new User().setName(user).setLogin(user), 
				f -> f.getRegistryProperty().eq(customerId))
				.collect(Collectors.toList());
	}
	@Override
	public RegistryNote saveNote(String domainName, int domain, String currentUser, RegistryNote note) throws AonCoreException {
		return AON.saveRegistryNote(new Domain().setName(domainName).setId(domain), currentUser, note);
	}
	@Override
	public void deleteNote(String domainName, int domain, String currentUser, Integer id) throws AonCoreException {
		AON.deleteRegistryNote(new Domain().setName(domainName).setId(domain), currentUser, id);
	}
	
	// **************************************************
	// ****************************** [CUSTOMER INVOICES]
	// **************************************************

	@Override
	public List<Invoice> getCustomerInvoices(String domainName, int domain, String user, Integer customerId) throws AonCoreException {
		List<Invoice> invoices = AON.getInvoiceList(domainName, domain, user, f -> f.getRegistryProperty().eq(customerId));
		invoices.forEach(invoice -> {
			LinkedList<Finance> finances = AON.getFinanceList(domainName, domain, user, f -> f.getInvoiceProperty().eq(invoice.getId()));
			finances.forEach(finance -> invoice.addFinance(finance));
		});
		return invoices;
	}
	
	@Override
	public String getInvoicePDF(String domainName, int domainId, String login, Integer officeDomain, Integer invoiceId) throws AonCoreException {
		Occam occam = new Occam().setDomainName(domainName).setDomain(domainId).setUser(login);
		InvoiceCommunicationConfiguration icc = AON.getInvoiceCommunicationConfiguration(occam);

		try (ByteArrayOutputStream os = new ByteArrayOutputStream(30 * 1024)){	
			PrintInvoiceConfiguration config;
			if(null == officeDomain)
				config = AON_SOLUTIONS.getPrintInvoiceConfiguration(domainName, domainId, login, true);
			else
				config = AON_SOLUTIONS.getPrintInvoiceConfiguration(domainName, domainId, login, officeDomain, true);
			
			Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, domainId, login, invoiceId);
			
			CompanyFull company;
			if(null == officeDomain)
				company = AON.getCompanyFull(domainName, domainId, login);
			else
				company = AON.getCompanyFull(domainName, domainId, login, officeDomain);
			
			Attach logo = new Attach();
			
			if(config.isLogo()) {
				Integer logoId = company.getRegistry().getId();
				logo = AON.getAttach(domainName, domainId, login, f-> f.getAttachModuleProperty().eq(logoId)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
			}

			String qrUrl = "https://" + domainName + "/dip?d=" + company.getRegistry().getDocument() 
						+ "&f=" + AonDateUtils.simpleFormat(invoice.getIssueDate())
						+ "&s=" + invoice.getSeries()
						+ "&n=" + invoice.getNumber()
						+ "&t=" + invoice.getTotal();  
			
			if(company.getRegistry().getDomain().isGarage()) {	
				invoice.detailStream().forEach(d -> {
					ProjectTas pt = AON.getProjectTas(occam, f -> f.getDomainProperty().eq(invoice.getDomain())
							.and(f.getIdProperty().eq(d.getProject()))).orElse(null);
					if(pt != null)	d.setProjectName(d.getProjectName() + " - KMS. " + d.getProject());	
				});
			}
			
			PdfMaker.printInvoice(os, company, icc, invoice, config, qrUrl, logo.getData(), null);
			
			byte[] bytes = os.toByteArray();
			
			String base64Pdf = Base64.getEncoder().encodeToString(bytes);
			
			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	// **************************************************
	// ************************************ [TASK HOLDER]
	// **************************************************
	
	@Override
	public List<TaskHolder> getTaskHolderList(TaskHolderParams params) throws AonCoreException {
		List<TaskHolder> taskHolders = AON.getTaskHolderList(params);
		return new ArrayList<>(taskHolders);
	}
	
	@Override
	public TaskHolder getTaskHolder(String domainName, int domain, String user, Integer taskHolderId) throws AonCoreException {
		return AON.getTaskHolder(new Domain().setName(domainName).setId(domain), user, f -> f.getIdProperty().eq(taskHolderId));
	}
	
	@Override
	public void deleteTaskHolder(String domainName, int domain, String user, Integer taskHolderId) throws AonCoreException {
		AON.deleteTaskHolder(domainName, taskHolderId, user, taskHolderId);
	}
	
	@Override
	public TaskHolder saveTaskHolder(String domainName, int domain, String user, TaskHolder taskHolder) throws AonCoreException {
		return AON.save(domainName, domain, user, taskHolder);
	}
	
	@Override
	public Integer getTaskHoldersCount(TaskHolderParams params) throws AonCoreException {
		Integer count = AON.getTaskHoldersCount(params);
		return count;
	}
	
	@Override
	public TaskHolderWorkgroup getTaskHolderWorkgroup(String domainName, Integer domain, String user, Integer taskHolderWorkgroupId) throws AonCoreException {
		return AON.getTaskHolderWorkgroupsList(new Domain().setName(domainName).setId(domain), new User().setLogin(user).setName(user), f -> f.getIdProperty().eq(taskHolderWorkgroupId)).get(0);
	}
	
	@Override
	public TaskHolderWorkgroup saveTaskHolderWorkgroup(String domainName, Integer domain, String user, TaskHolderWorkgroup taskHolderWorkgroup) throws AonCoreException {
		return AON.saveTaskHolderWorkgroup(new Domain().setName(domainName).setId(domain), new User().setLogin(user).setName(user), taskHolderWorkgroup);
	}
	
	@Override
	public void deleteTaskHolderWorkgroup(String domainName, Integer domain, String user, Integer taskHolderWorkgroupId) throws AonCoreException {
		AON.deleteTaskHolderWorkgroup(new Domain().setName(domainName).setId(domain), new User().setLogin(user).setName(user), f -> f.getIdProperty().eq(taskHolderWorkgroupId));
	}
	
	@Override
	public List<TaskHolderWorkgroup> getTaskHolderWorkgroupList(String domainName, Integer domain, String user, Integer taskHolderId) throws AonCoreException {
		return AON.getTaskHolderWorkgroupsList(new Domain().setName(domainName).setId(domain), new User().setLogin(user).setName(user), f -> f.getTaskHolderProperty().eq(taskHolderId));
	}
	
	@Override
	public List<User> getUsersForTaskHolder(String domainName, Integer domainId, String user, boolean all) throws AonCoreException {
		Domain domain = AON.getDomain(domainName, domainId, user);
		Domain parentDomain = domain.getParent();
		
		List<User> users;
		if( 
			(domain.getDomainType().equals(DomainType.OFFICE) && (null != parentDomain && null != parentDomain.getId() && parentDomain.getDomainType().equals(DomainType.CONSULTANCY))) || 
			(domain.getDomainType().equals(DomainType.ENTERPRISE) && (null != parentDomain && null != parentDomain.getId() && parentDomain.getDomainType().equals(DomainType.ENTERPRISE)))
		) {
			users = AON.getUserStream(new Domain().setName(domainName).setId(domainId), user, f -> f.getDomainProperty().eq(domainId).or(f.getDomainProperty().eq(domain.getParentId())), new Options().setFull(true)).collect(Collectors.toList());
		} else
			users = AON.getUserStream(new Domain().setName(domainName).setId(domainId), user, f -> f.getDomainProperty().eq(domainId), new Options().setFull(true)).collect(Collectors.toList());
		
		users.sort(Comparator.comparing(userIt -> normalizeName(userIt.getName())));
		
		// Filter already asigned users
		if(!all) {
			List<TaskHolder> taskHolders = AON.getTaskHolderList(new TaskHolderParams().setDomainName(domainName).setDomain(domainId).setUser(user).setOffset(0).setLimit(Integer.MAX_VALUE));
			List<Integer> taskHolderUsers = taskHolders.stream().map(th -> th.getUserId()).filter(Objects::nonNull).collect(Collectors.toList());
			
			users = users.stream().filter(usr -> !taskHolderUsers.contains(usr.getId())).collect(Collectors.toList());
		}
		
		return users;
	}
	
	private static String normalizeName(String name) {
		if(AonStringUtils.isBlank(name)) return AonStringUtils.EMPTY;

		name = name.trim();

	    String noAccents = Normalizer.normalize(name, Normalizer.Form.NFD)
	            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

	    return noAccents.toLowerCase();
	}
	
	@Override
	public User getUser(String domainName, Integer domainId, String user, Integer userId) throws AonCoreException {
		return AON.getUserStream(new Domain().setName(domainName).setId(domainId), user, f -> f.getIdProperty().eq(userId), new Options().setFull(true)).collect(Collectors.toList()).get(0);
	}
	
	// **************************************************
	// ****************************************** [SCOPE]
	// **************************************************
	
	@Override
	public List<Scope> getScopeList(ScopeParams params) throws AonCoreException {
		return AON.getScopeList(params);
	}
	
	@Override
	public Integer getScopesCount(ScopeParams params) throws AonCoreException {
		return AON.getScopesCount(params);
	}
	
	@Override
	public Scope getScope(String domainName, int domain, String user, Integer scopeId) throws AonCoreException {
		return AON.getScope(domainName, domain, user, scopeId);
	}
	
	@Override
	public Scope saveScope(String domainName, int domain, String user, Scope scope) throws AonCoreException {
		return AON.saveScope(domainName, domain, user, scope);
	}
	
	@Override
	public Scope saveScopeAndAssign(String domainName, int domain, String user, Scope scope, boolean assignAllUsers, ArrayList<User> selectedUsers) throws AonCoreException {
		return AON.saveScopeAndAssign(domainName, domain, user, scope, assignAllUsers, selectedUsers);
	}
	
	@Override
	public void deleteScope(String domainName, int domain, String user, Integer scopeId) throws AonCoreException {
		AON.deleteScope(domainName, domain, user, scopeId);
	}

	@Override
	public List<UserScopeFull> getUserScopeList(String domainName, Integer domain, String user, Integer scopeId) throws AonCoreException {
		List<UserScopeFull> list = AON.getUserScopeFullList(domainName, domain, user, scopeId);
		return list;
	}

	@Override
	public void saveUserScope(String domainName, Integer domainId, String user, UserScope userScope) throws AonCoreException {
		AON.insertUserScope(domainName, domainId, user, userScope);
	}
	
	@Override
	public void deleteUserScope(String domainName, Integer domain, String user, Integer userScopeId) throws AonCoreException {
		AON.deleteUserScope(domainName, domain, user, f -> f.getIdProperty().eq(userScopeId));	
	}
	
	@Override
	public List<Domain> getDomainScopeList(String domainName, Integer domain, String user, Integer scopeId) throws AonCoreException {
		return AON.getDomainList(domainName, domain, user, f -> f.getScopeProperty().eq(scopeId));
	}
	
	@Override
	public void saveDomainScope(String domainName, Integer domainId, String user, Integer domainChange, Integer scopeId) throws AonCoreException {
		Domain domain = AON.getDomain(domainName, domainId, user, f -> f.getIdProperty().eq(domainChange));
		AON.updateDomainScopeValue(domain.getName(), domain.getId(), user, scopeId);
	}
	
	@Override
	public void deleteDomainScope(String domainName, Integer domainId, String user, Domain domain) throws AonCoreException {
		AON.updateDomainScopeValue(domain.getName(), domain.getId(), user, null);
	}
	
	@Override
	public List<Domain> getDomains(String domainName, Integer domainId, String user) throws AonCoreException {
		Domain domain = AON.getDomain(domainName, domainId, user, f -> f.getIdProperty().eq(domainId));
		
		List<Integer> searchDomains = new ArrayList<Integer>();
		searchDomains.add(domain.getId());
		if(null != domain.getParentId()) searchDomains.add(domain.getParentId());
		
		Integer[] domainParentIds = new Integer[searchDomains.size()];
		searchDomains.stream().collect(Collectors.toList()).toArray(domainParentIds);
		
		
		return AON.getDomainList(domainName, domainId, user, f -> f.getParentProperty().in(domainParentIds));
	}
	

	@Override
	public ArrayList<User> getUsers(String domainName, int domainId, String user) throws AonCoreException {
		return AON.getUserStream(domainName, domainId, user, f -> f.getDomainProperty().eq(domainId).and(f.getActiveProperty().eq((byte)1))).collect(Collectors.toCollection(ArrayList::new));
	}
	
	// **************************************************
	// ******************************************** [TAG]
	// **************************************************
	
	@Override
	public List<Tag> getTagList(TagParams params) throws AonCoreException {
		return AON.getTagList(params);
	}
	
	@Override
	public Tag saveTag(String domainName, Integer domainId, String user, Tag tag) throws AonCoreException {
		return AON.saveTag(domainName, domainId, user, tag);
	}
	
	@Override
	public void deleteTag(String domainName, int domainId, String user, Integer id) throws AonCoreException {
		AON.deleteTag(domainName, domainId, user, f -> f.getIdProperty().eq(id));
	}
	@Override
	public List<String> getSchemas() throws AonCoreException {
		return AON_SOLUTIONS.getSchemas();
	}
	
	// **************************************************
	// *********************** [CUSTOMER LINKED ACTIVITY]
	// **************************************************
	
	@Override
	public List<Customer> getCustomers(CustomersLinkedParams params) throws AonCoreException {
		return AON.getCustomerStream(params.getDomainName(), params.getDomainId(), params.getUser(), 
					f -> f.getDomainProperty().eq(params.getDomainId())
					.and(AonStringUtils.isBlank(params.getQuery())
							? f.getIdProperty().isNotNull()
							: f.getDocumentProperty().like("%" + params.getQuery() + "%")
								.or(f.getNameProperty().like("%" + params.getQuery() + "%"))
								.or(f.getAliasProperty().like("%" + params.getQuery() + "%"))
					)
					.and(f.getStatusProperty().in(params.getCustomerStatus())), 
					params.getOffset(), params.getLimit())
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Customer> getCustomersLinked(CustomersLinkedParams params) throws AonCoreException {
		if(params.isSig())
			return AON.getSigCustomerStream(params.getDomainName(), params.getDomainId(), params.getUser(), 
					f -> f.getDomainProperty().eq(params.getDomainId())
					.and(AonStringUtils.isBlank(params.getQuery())
							? f.getIdProperty().isNotNull()
							: f.getDocumentProperty().like("%" + params.getQuery() + "%")
								.or(f.getNameProperty().like("%" + params.getQuery() + "%"))
								.or(f.getAliasProperty().like("%" + params.getQuery() + "%"))
					)
					.and(f.getStatusProperty().in(params.getCustomerStatus())), 
					params.getOffset(), params.getLimit())
				.collect(Collectors.toList());
		else
			return AON.getCustomerStream(params.getDomainName(), params.getDomainId(), params.getUser(), 
					f -> f.getDomainProperty().eq(params.getDomainId())
					.and(f.getRegistryRelationProperty().isNotNull())
					.and(AonStringUtils.isBlank(params.getQuery())
							? f.getIdProperty().isNotNull()
							: f.getDocumentProperty().like("%" + params.getQuery() + "%")
								.or(f.getNameProperty().like("%" + params.getQuery() + "%"))
								.or(f.getAliasProperty().like("%" + params.getQuery() + "%"))
					)
					.and(f.getStatusProperty().in(params.getCustomerStatus())), 
					params.getOffset(), params.getLimit())
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Customer> getCustomersNotLinked(CustomersLinkedParams params) throws AonCoreException {
		if(params.isSig())
			return AON.getSigCustomerNotLinkedStream(params.getDomainName(), params.getDomainId(), params.getUser(), 
					f -> f.getDomainProperty().eq(params.getDomainId())
					.and(AonStringUtils.isBlank(params.getQuery())
							? f.getIdProperty().isNotNull()
							: f.getDocumentProperty().like("%" + params.getQuery() + "%")
								.or(f.getNameProperty().like("%" + params.getQuery() + "%"))
								.or(f.getAliasProperty().like("%" + params.getQuery() + "%"))
					)
					.and(f.getStatusProperty().in(params.getCustomerStatus())), 
					params.getOffset(), params.getLimit())
				.collect(Collectors.toList());
		else
			return AON.getCustomerStream(params.getDomainName(), params.getDomainId(), params.getUser(), 
					f -> f.getDomainProperty().eq(params.getDomainId())
					.and(f.getRegistryRelationProperty().isNull())
					.and(AonStringUtils.isBlank(params.getQuery())
							? f.getIdProperty().isNotNull()
							: f.getDocumentProperty().like("%" + params.getQuery() + "%")
								.or(f.getNameProperty().like("%" + params.getQuery() + "%"))
								.or(f.getAliasProperty().like("%" + params.getQuery() + "%"))
					)
					.and(f.getStatusProperty().in(params.getCustomerStatus())), 
					params.getOffset(), params.getLimit())
				.collect(Collectors.toList());
	}
	
	@Override
	public HashMap<Integer, Domain> getCustomersDomain(String domainName, Integer domainId, String user, ArrayList<Integer> customerIds) throws AonCoreException {
		HashMap<Integer, Domain> customers = new HashMap<Integer, Domain>();
		
		customerIds.forEach(customerId -> {
			RegistryRelationship registryRelationship = AON_SOLUTIONS.getRegistryRelationship(
					new Domain().setName(domainName).setId(domainId), 
					new User().setLogin(user), 
					f -> f.getRegistryProperty().eq(customerId)).get();
			
			if(null != registryRelationship.getRelatedRegistry()) {
				Enterprise enterprise = AON.getEnterprise(domainName, domainId, user, registryRelationship.getRelatedRegistry());
				if(null != enterprise) {
					Domain domain = AON.getDomain(domainName, domainId, user, f -> f.getIdProperty().eq(enterprise.getDomain()));
					customers.put(customerId, domain);
				}
				
			}	
		});
		
		return customers;
	}
	
	@Override
	public List<ActivitySummaryObject> getActivitySummary(String domainName, String userLogin, ActivitySummaryParams params) throws AonCoreException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Domain domain = AON_SOLUTIONS.getDomain(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domain.getId(), domain.getParentId());
			
			List<ActivitySummaryObject> list = AON.getActivitySummary(domain.getName(), domain.getId(), userLogin, domain.getParentId(), userId, params);
			
			return list;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public List<DomainCompany> getAviableSyncDomains(CustomersDomainSyncParams paramsDomains) throws AonCoreException {
		Domain currentDomain = AON.getDomain(paramsDomains.getDomainName(), paramsDomains.getDomainId(), paramsDomains.getUser());
		
		return AON.getAviableDomainsForSync(paramsDomains.getDomainName(), paramsDomains.getDomainId(), paramsDomains.getUser(), false, 
				f -> f.getParentProperty().eq(currentDomain.getParentId() == null ? currentDomain.getId() : currentDomain.getParentId())
				.and(
					f.getNameProperty().like("%" + paramsDomains.getQuery() + "%")
					.or(f.getDescriptionProperty().like("%" + paramsDomains.getQuery() + "%"))
				)).collect(Collectors.toList());
	}
	
	@Override
	public void syncCustomer(String domainName, Integer domainId, String user, Integer customerId, DomainCompany domainCompany, boolean isSig) throws AonCoreException {
		if(isSig) {
			List<RegistryAddInfo> raddInfoList = AON.getRegistryAddInfoStream(domainName, domainId, user, f -> f.getRegistryProperty().eq(customerId).and(f.getAttributeProperty().like("AON_DOMAIN%_NAME"))).collect(Collectors.toList());
			
			Optional<RegistryAddInfo> raddInfoSchema = AON.getRegistryAddInfo(domainName, domainId, user, f -> f.getRegistryProperty().eq(customerId).and(f.getAttributeProperty().like("AON_DOMAIN%_SCHEMA")).and(f.getValueProperty().eq(domainCompany.getSchema())));
			Optional<RegistryAddInfo> raddInfoName = AON.getRegistryAddInfo(domainName, domainId, user, f -> f.getRegistryProperty().eq(customerId).and(f.getAttributeProperty().like("AON_DOMAIN%_NAME")).and(f.getValueProperty().eq(domainCompany.getDomain().getName())));
			Optional<RegistryAddInfo> raddInfoId = AON.getRegistryAddInfo(domainName, domainId, user, f -> f.getRegistryProperty().eq(customerId).and(f.getAttributeProperty().like("AON_DOMAIN%_ID")).and(f.getValueProperty().eq(domainCompany.getDomain().getId().toString())));
			Optional<RegistryAddInfo> raddInfoType = AON.getRegistryAddInfo(domainName, domainId, user, f -> f.getRegistryProperty().eq(customerId).and(f.getAttributeProperty().like("AON_DOMAIN%_TYPE")).and(f.getValueProperty().eq(domainCompany.getDomain().getDomainType().getName())));
			
			if(raddInfoName.isEmpty())
				AON.insertRegistryAddInfo(domainName, domainId, user, 
						new RegistryAddInfo()
							.setDomain(domainId)
							.setRegistry(customerId)
							.setAttribute("AON_DOMAIN" + (raddInfoList.size() + 1) + "_NAME")
							.setValue(domainCompany.getDomain().getName())
							.setDate(new Date())
					);
			
			if(raddInfoId.isEmpty())
				AON.insertRegistryAddInfo(domainName, domainId, user, 
					new RegistryAddInfo()
						.setDomain(domainId)
						.setRegistry(customerId)
						.setAttribute("AON_DOMAIN" + (raddInfoList.size() + 1) + "_ID")
						.setValue(domainCompany.getDomain().getId().toString())
						.setDate(new Date())
				);
			
			if(raddInfoSchema.isEmpty())
				AON.insertRegistryAddInfo(domainName, domainId, user, 
					new RegistryAddInfo()
						.setDomain(domainId)
						.setRegistry(customerId)
						.setAttribute("AON_DOMAIN" + (raddInfoList.size() + 1) + "_SCHEMA")
						.setValue(domainCompany.getSchema())
						.setDate(new Date())
				);
			
			if(raddInfoType.isEmpty())
				AON.insertRegistryAddInfo(domainName, domainId, user, 
					new RegistryAddInfo()
						.setDomain(domainId)
						.setRegistry(customerId)
						.setAttribute("AON_DOMAIN" + (raddInfoList.size() + 1) + "_TYPE")
						.setValue(domainCompany.getDomain().getDomainType().getName())
						.setDate(new Date())
				);
		} else {
			Optional<RegistryRelationship> existRelationShip = AON_SOLUTIONS.getRegistryRelationship(new Domain().setName(domainName).setId(domainId), new User().setLogin(user), f -> f.getRegistryProperty().eq(customerId).and(f.getDomainProperty().eq(domainId)).and(f.getRelationshipProperty().eq(-1)));
			if(existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya está vinculado al dominio " + existRelationShip.get().getComments());
			
			AON_SOLUTIONS.saveRegistryRelationship(
					new Domain().setName(domainName).setId(domainId), 
					new User().setLogin(user), 
					new RegistryRelationship()
						.setDomain(new Domain().setId(domainId))
						.setRegistry(customerId)
						.setRelatedRegistry(domainCompany.getCompany().getId())
						.setRelationship(new Relationship().setId(-1))
						.setComments(domainCompany.getDomain().getName())
					);
		}
		
	}
	@Override
	public List<DomainSigAddInfo> getDomainSigAddInfo(String domainName, Integer domainId, String user, Integer customerId) throws AonCoreException {
		List<DomainSigAddInfo> result = AON.getDomainSigAddInfo(domainName, domainId, user, customerId);
		return result;
	}
	
	@Override
	public LinkedList<Fee> getCustomerFeesRelatedRegistry(String domainName, int domainId, String user, Integer customerRelatedRegistry) throws AonCoreException {
		Optional<RegistryRelationship> existRelationShip = AON_SOLUTIONS.getRegistryRelationship(new Domain().setName(domainName).setId(domainId), new User().setLogin(user), f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getDomainProperty().eq(domainId)).and(f.getRelationshipProperty().eq(-1)));
		if(!existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya no está vinculado al dominio");
		
		LinkedList<Fee> fees = AON.getFeeList(domainName, domainId, user, f -> f.getDomainProperty().eq(domainId).and(f.getCustomerProperty().eq(existRelationShip.get().getRegistry())));
		return fees;
	}
	

	@Override
	public void createBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, ProductBooking product, Fee newFee) throws AonCoreException {
		AON.createBookingProduct(domainName, domain, user, customerRelatedRegistry, product, newFee);
	}
	
	@Override
	public void updateBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Fee oldFee, ProductBooking product, Fee newFee) throws AonCoreException {
		AON.updateBookingProduct(domainName, domain, user, customerRelatedRegistry, Optional.of(oldFee), product, newFee);
	}
	
	@Override
	public void removeBookingProduct(String domainName, int domain, String user, Integer customerRelatedRegistry, Fee oldFee, ProductBooking product) throws AonCoreException {
		AON.removeBookingProduct(domainName, domain, user, customerRelatedRegistry, Optional.of(oldFee), product);
	}
	
	@Override
	public void requestBookingInfo(String domainName, int domain, String user, ProductBooking product, Integer customerRegistry) throws AonCoreException {
		AON.requestBookingInfo(domainName, domain, user, product, customerRegistry);
	}
	
	@Override
	public List<RegistryPayMethod> getRegistryPayMethods(String domainName, Integer domainId, String user, Integer registry) throws AonCoreException {
		List<RegistryPayMethod> registryPayMethods = AON.getRegistryPayMethodStream(domainName, domainId, user, f -> f.getDomainProperty().eq(domainId).and(f.getRegistryProperty().eq(registry))).collect(Collectors.toList());
		registryPayMethods.forEach(rp -> {
			if(null != rp.getRbank() && null != rp.getRbank().getId())
				rp.setRbank(AON.getRegistryBank(new Domain().setName(domainName).setId(domainId), user, f -> f.getIdProperty().eq(rp.getRbank().getId())));
		});
		return registryPayMethods;
	}
	
	@Override
	public RegistryPayMethod saveRegistryPayMethod(String domainName, Integer domainId, String user, RegistryPayMethod registryPayMethod) throws AonCoreException {
		if(null == registryPayMethod.getRbank().getId())
			AON.saveRegistryBank(new Domain().setName(domainName).setId(domainId), 
					user, registryPayMethod.getRbank());
		
		return AON.saveRegistryPayMethod(
				new Domain().setName(domainName).setId(domainId), 
				new User().setLogin(user), 
				registryPayMethod);
	}
	
	@Override
	public List<RegistryRelationship> getRegistryRelationshipsByRelated(String domainName, Integer domainId, String user, Integer customerRelatedRegistry) throws AonCoreException {
		List<RegistryRelationship> registryRelationships = AON_SOLUTIONS.getRegistryRelationshipStream(
				new Domain().setName(domainName).setId(domainId), 
				new User().setLogin(user), 
				f -> f.getDomainProperty().eq(domainId).and(f.getRelatedRegistryProperty().eq(customerRelatedRegistry))).collect(Collectors.toList());
		
		return registryRelationships;
	}
	
	@Override
	public Customer saveCustomer(String domainName, Integer domainId, String user, Customer customer) throws AonCoreException {
		return AON.saveCustomer(domainName, domainId, user, customer);
	}
	
	@Override
	public List<RegistryRelationship> getRegistryRelationships(String domainName, int domain, String user) throws AonCoreException {
		return AON.getRegistryRelationships(domainName, domain, user);
	}
	
	// **************************************************
	// *********************** [CERTIFICATES]
	// **************************************************


	@Override
	public DomainUserRoles getDomainUserRoles(String domainName, Integer domainId, String userLogin) throws AonCoreException {
		User user = AON.getUser(domainName, domainId, userLogin);
		return SECURITY.getDomainUserRoles(domainName, domainId, userLogin, user.getId());
	}
	
	@Override
	public List<Certificate> getCertificates(String domainName, Integer domainId, String userLogin, boolean withParent) throws AonCoreException {
		try {
			User user = AON.getUser(domainName, domainId, userLogin);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			
			List<com.esferalia.aon.occam.api.model.Certificate> certificates = withParent 
					? AON.getCertificatesWithParent(domainName, domainId, parentDomainId, userLogin, user.getId()) 
					: AON.getCertificates(domainName, domainId, userLogin, user.getId());
			
			return certificates;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
	}
	
	@Override
	public void deleteCertificate(String domainName, Integer domainId, String userLogin, Certificate certificate) throws AonCoreException {
		AON.deleteCertificate(domainName, domainId, userLogin, 
				certificate.getId(),
				f -> f.getIdProperty().eq(certificate.getId()), null);
	}
	
	@Override
	public void downloadCertificate(String domainName, Integer domainId, String userLogin, Integer certificateId, String filePath) throws AonCoreException {
		Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, f -> f.getIdProperty().eq(certificateId));
		
		File f = new File(filePath);
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(certificate.getData());
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Archivo no encontrado");
		} catch (IOException e) {
			System.err.println("Error al escribir");
		}
	}
	
	@Override
	public void verifyCertificate(String domainName, Integer domainId, String userLogin, Integer rattachId, List<CertificateType> tags) throws AonCoreException {
		try {
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, f -> f.getIdProperty().eq(rattachId));
			
			for(CertificateType tag : tags) {
				if(tag == CertificateType.TGSS) {
					InputStream certificateIS = new ByteArrayInputStream(certificate.getData());
					SistemaRED.validateCert(certificateIS, certificate.getPassword(), certificate.getType());
				}
	
				if(tag == CertificateType.SEPE) {
					InputStream certificateIS = new ByteArrayInputStream(certificate.getData());
					Sepe.validateCert(certificateIS, certificate.getPassword(), certificate.getType());
				}
			}
		} catch (Exception e) {
			if(AonStringUtils.equalsIgnoreCase(e.getMessage(), "java.io.IOException: keystore password was incorrect"))
				throw new AonCoreException("Contrase\u00F1a incorrecta");
			
			throw new AonCoreException(e.getMessage());
		} 
	}
	
	@Override
	public CertificateInfo getCertificateInfo(String domainName, Integer domainId, String userLogin, Integer certificateId) throws AonCoreException {
		return AON.getCertificateInfo(domainName, domainId, userLogin, f -> f.getIdProperty().eq(certificateId));
	}
	
	@Override
	public List<SecondaryUserCertificate> getSecondaryUsers(String domainName, Integer domainId, String userLogin, Integer rattachId) throws AonCoreException {
		try {
			User user = AON.getUser(domainName, domainId, userLogin);
			Certificate certificate = null;
			
			if(rattachId == null) {	
				
				certificate = AON.getCertificate(domainName, domainId, userLogin, user.getId(), "TGSS");
			} else
				certificate = parseCertificate(AON.getCertificate(domainName, domainId, userLogin, f -> f.getIdProperty().eq(rattachId)));
			
			InputStream is = new ByteArrayInputStream(certificate.getData());
			Collection<SecondaryUser> secondaryUsersCollection = SistemaRED.getSecondaryUsers(is, certificate.getPassword(), certificate.getType());
			
			List<SecondaryUser> secondaryUsers = new ArrayList<>(secondaryUsersCollection);
			List<SecondaryUserCertificate> secondaryUsersCertificate = new ArrayList<>();
			
			for(SecondaryUser secondaryUser : secondaryUsers) {
				secondaryUsersCertificate.add(new SecondaryUserCertificate(
						secondaryUser.getAuthoritation(),
						secondaryUser.getAuthoritationEntity(),
						secondaryUser.getMainUserName(),
						secondaryUser.getMainUserIpf(),
						secondaryUser.getMainUserNaf(),
						secondaryUser.getName(),
						secondaryUser.getProvince(),
						secondaryUser.getIpf(),
						secondaryUser.getNaf(),
						secondaryUser.getSituation(),
						secondaryUser.getSituationDate(),
						secondaryUser.getTelephone(),
						secondaryUser.getFax(),
						secondaryUser.getMobile(),
						secondaryUser.getMail()
				));
			}
			
			return secondaryUsersCertificate;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
	}
	
	private Certificate parseCertificate(Certificate certificate) {
		return new Certificate()
				.setData(certificate.getData())
				.setPassword(certificate.getPassword())
				.setType(certificate.getType());
	}
	
	@Override
	public String getSecondaryUsersPDF(String domainName, Integer domainId, String userLogin, Integer rattachId) throws AonCoreException {
		try {
			
			Certificate certificate = null;
			User user = AON.getUser(domainName, domainId, userLogin);
			
			Optional<ApplicationParameter> authParam = AON.getApplicationParameterStream(domainName, domainId, userLogin, f -> f.getDomainProperty().eq(domainId).and(f.getNameProperty().eq("PAY_authorization_key_PAY"))).findFirst();
			String authKey = null;
			if(authParam.isEmpty() || AonStringUtils.isBlank(authParam.get().getValue())) {
				EnterpriseData enterpriseData = AON.getEnterpriseData(new Domain().setName(domainName).setId(domainId), new User().setLogin(userLogin), f -> f.getDomainProperty().eq(domainId).and(f.getNameProperty().eq("PAY_authorization_key_PAY")));
				if(null != enterpriseData && null != enterpriseData.getId()) authKey = enterpriseData.getExpression();
			} else 
				authKey = authParam.get().getValue();
			
			if(rattachId == null) {	
				certificate = AON.getCertificate(domainName, domainId, userLogin, user.getId(), "TGSS");
			} else
				certificate = parseCertificate(AON.getCertificate(domainName, domainId, userLogin, f -> f.getIdProperty().eq(rattachId)));
			
			byte[] data = SistemaRED.getSecondaryUsersPDF(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), authKey);
			String base64Pdf = Base64.getEncoder().encodeToString(data);
					
			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);
	
			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();
	
			return dataUri;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
		
	}
	
	@Override
	public String getAssignedCCCsPDF(String domainName, Integer domainId, String userLogin, Integer rattachId) throws AonCoreException {
		try {
			Certificate certificate = null;
			User user = AON.getUser(domainName, domainId, userLogin);
			
			Optional<ApplicationParameter> authParam = AON.getApplicationParameterStream(domainName, domainId, userLogin, f -> f.getDomainProperty().eq(domainId).and(f.getNameProperty().eq("PAY_authorization_key_PAY"))).findFirst();
			String authKey = null;
			
			if(authParam.isEmpty() || AonStringUtils.isBlank(authParam.get().getValue())) {
				EnterpriseData enterpriseData = AON.getEnterpriseData(new Domain().setName(domainName).setId(domainId), new User().setLogin(userLogin), f -> f.getDomainProperty().eq(domainId).and(f.getNameProperty().eq("PAY_authorization_key_PAY")));
				if(null != enterpriseData && null != enterpriseData.getId()) authKey = enterpriseData.getExpression();
			} else 
				authKey = authParam.get().getValue();
			
			if(rattachId == null) {	
				certificate = AON.getCertificate(domainName, domainId, userLogin, user.getId(), "TGSS");
			} else
				certificate = parseCertificate(AON.getCertificate(domainName, domainId, userLogin, f -> f.getIdProperty().eq(rattachId)));
			
			byte[] data = SistemaRED.getAssignedCCCsPDF(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), authKey);
			String base64Pdf = Base64.getEncoder().encodeToString(data);
					
			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
	}
	
	@Override
	public EmployeeSegSocial getIpfxNaf(String domainName, Integer domainId, String userLogin, ArrayList<String> nssList) throws AonCoreException {
		try {	
			User user = AON.getUser(domainName, domainId, userLogin);
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, user.getId(), "TGSS");
			
			InputStream is = new ByteArrayInputStream(certificate.getData());
			
			Collection<Employee> employeeCollection = SistemaRED.ipfxnaf(is, certificate.getPassword(), certificate.getType(), nssList);
			Employee employee = (Employee) employeeCollection.toArray()[0];
			
			EmployeeSegSocial employeeSegSocial = new EmployeeSegSocial(
					employee.getNss(), 
					employee.getName().orElse(null), 
					employee.getBirthDate().orElse(null), 
					employee.getIpf());
			
			return employeeSegSocial;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
	}
	
	@Override
	public void deleteSecondaryUser(String domainName, Integer domainId, String userLogin, Integer rattachId, String ipfType, String ipf) throws AonCoreException {
		try {	
			Certificate certificate = null;
			User user = AON.getUser(domainName, domainId, userLogin);
			
			if(rattachId == null) {	
				certificate = AON.getCertificate(domainName, domainId, userLogin, user.getId(), "TGSS");
			} else
				certificate = parseCertificate(AON.getCertificate(domainName, domainId, userLogin, f -> f.getIdProperty().eq(rattachId)));
			
			
			InputStream is = new ByteArrayInputStream(certificate.getData());
			SistemaRED.deleteSecondaryUser(is, certificate.getPassword(), certificate.getType(), ipfType, ipf);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
	}
	
	@Override
	public void createSecondaryUser(String domainName, Integer domainId, String userLogin, Integer rattachId, String ipfType, String ipf, String naf) throws AonCoreException {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Certificate certificate = null;
			
			if(rattachId == null) {
				Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
				Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
				
				certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			} else
				certificate = parseCertificate(AON.getCertificate(domainName, domainId, userLogin, f -> f.getIdProperty().eq(rattachId)));
			
			InputStream is = new ByteArrayInputStream(certificate.getData());
			SistemaRED.registerSecondaryUserByNie(is, certificate.getPassword(), certificate.getType(), ipfType, ipf, naf);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
	}
	
	// **************************************************
	// *********************** [REGISTRY ENTRY]
	// **************************************************
	
	@Override
	public CompanyFull getCompanyFull(String domainName, int domain, String user) throws AonCoreException {
		return AON.getCompanyFull(domainName, domain, user);
	}
	
	@Override
	public CompanyFull saveCompanyFull(String domainName, int domain, String user, CompanyFull company) throws AonCoreException {
		AON.saveCompany(new Domain().setName(domainName).setId(domain), new User().setLogin(user), company.ensureCompany());
		return AON.getCompanyFull(domainName, domain, user);
	}
	
	@Override
	public Attach getCompanyLogo(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		Attach attach = AON.getAttach(domainName, domain, user, f -> f.getAttachModuleProperty().eq(registry).and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
		return attach;
	}
	
	@Override
	public Attach getCompanySignature(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		Attach attach = AON.getAttach(domainName, domain, user, f -> f.getAttachModuleProperty().eq(registry).and(f.getTypeProperty().eq(RegistryAttachmentType.SIGNATURE.value())), AttachType.REGISTRY);
		return attach;
	}
	
	@Override
	public List<Calendar> getCalendars(String domainName, Integer domain, String user) throws AonCoreException {
		return AON.getCalendars(domainName, domain, user);
	}
	
	@Override
	public List<Agreement> getAcgreements(String domainName, Integer domain, String user) throws AonCoreException {
		List<Agreement> agreements = AON.getAgreements(domainName, domain, user);
		return agreements;
	}
	
	@Override
	public List<Workplace> getWorkplaces(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		return AON.getWorkplaces(new Occam().setDomainName(domainName).setDomain(domain).setUser(user), domain).collect(Collectors.toList());
	}
	
	@Override
	public void deleteWrokplace(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteWrokplace(domainName, domain, user, id);
	}
	
	@Override
	public Workplace saveWorkplace(String domainName, Integer domainId, String user, Workplace workplace) throws AonCoreException {
		return AON.saveWorkplace(new Domain().setName(domainName).setId(domainId), user, workplace);
	}

	@Override
	public List<Activity> getEnterpriseActivities(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		return AON.getEnterpriseActivities(domainName, domain, user, registry);
	}
	
	@Override
	public Activity saveEnterpriseActivity(String domainName, Integer domain, String user, Activity enterpriseActivity) throws AonCoreException {
		return AON.saveEnterpriseActivity(domainName, domain, user, enterpriseActivity);
	}
	
	@Override
	public void deleteEnterpriseActivity(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteEnterpriseActivity(domainName, domain, user, id);
	}
	
	@Override
	public List<Cnae2009> getCnae2009List(String domainName, Integer domainId, String user) throws AonCoreException {
		return AON.getCnae2009List(domainName, domainId, user);
	}
	
	@Override
	public List<Cnae> getCnae2025List(String domainName, Integer domainId, String user) throws AonCoreException {
		return AON.getCnae2025List(domainName, domainId, user);
	}
	
	@Override
	public List<Iae> getIaeList(String domainName, Integer domainId, String user) throws AonCoreException {
		return AON.getIaeList(domainName, domainId, user);
	}
	
	@Override
	public List<RDirStaff> getRDirStaffs(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		return AON.getRDirStaffs(domainName, domain, user, registry);
	}
	
	@Override
	public RDirStaff saveRDirStaff(String domainName, Integer domain, String user, RDirStaff rDirStaff) throws AonCoreException {
		return AON.saveRDirStaff(domainName, domain, user, rDirStaff);
	}
	
	@Override
	public void deleteRDirStaff(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteRDirStaff(domainName, domain, user, id);
	}

	@Override
	public List<RecordData> getRecordDatas(String domainName, Integer domain, String user, Integer registry, boolean witdhData) throws AonCoreException {
		return AON.getRecordDatas(domainName, domain, user, registry, witdhData);
	}

	@Override
	public RecordData getRecordData(String domainName, Integer domain, String user, Integer recordDataId) throws AonCoreException {
		return AON.getFullRecordData(domainName, domain, user, recordDataId);
	}
	
	@Override
	public void deleteRecordData(String domainName, Integer domain, String user, Integer id, boolean deleteData) throws AonCoreException {
		AON.deleteRecordData(domainName, domain, user, id, deleteData);
	}
	
	@Override
	public RecordData saveRecordData(String domainName, Integer domain, String user, RecordData recordData) throws AonCoreException {
		return AON.saveRecordData(domainName, domain, user, recordData);
	}
	
	@Override
	public void deleteRecordDataAttach(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteRecordDataAttach(domainName, domain, user, id);
	}
	
	@Override
	public List<RegistryBank> getRregistryBanks(String domainName, Integer domain, String user, Integer registry) throws AonCoreException {
		return AON.getRegistryBanks(new Domain().setName(domainName).setId(domain), new User().setLogin(user), registry);
	}
	
	@Override
	public RegistryBank saveRregistryBank(String domainName, Integer domain, String user, RegistryBank registryBank) throws AonCoreException {
		return AON.saveRegistryBank(new Domain().setName(domainName).setId(domain), user, registryBank);
	}
	
	@Override
	public void deleteRregistryBank(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteRegistryBank(new Domain().setName(domainName).setId(domain), user, id);
	}

	@Override
	public List<Account> getAccountsForBank(String domainName, Integer domain, String user) throws AonCoreException {
		return AON.getAccountsForBank(domainName, domain, user);
	}
	
	@Override
	public List<Account> getAviablesAccountsForBank(String domainName, Integer domain, String user) throws AonCoreException {
		return AON.getAviablesAccountsForBank(domainName, domain, user);
	}
	
	@Override
	public Account createAccountsForBank(String domainName, Integer domain, String user, String alias, String suffixCode) throws AonCoreException {
		return AON.createAccountsForBank(domainName, domain, user, alias, suffixCode);
	}
	
	@Override
	public List<Account> getAccountsForRegistry(String domainName, Integer domain, String user, RegistrySource source, String pattern) throws AonCoreException {
		return AON.getAccountsForRegistry(domainName, domain, user, source, pattern);
	}
	
	@Override
	public LinkedList<Signature> getSignatures(String domainName, Integer domain, String user) throws AonCoreException {
		return AON.getSignatures(domainName, domain, user, f -> f.getDomainProperty().eq(domain).and(f.getUserIdProperty().isNull()));
	}
	@Override
	public void deleteSignature(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteSignature(domainName, domain, user, id);
	}
	@Override
	public Signature saveSignature(String domainName, Integer domain, String user, Signature signature) throws AonCoreException {
		return AON.saveSignature(domainName, domain, user, signature);
	}
	
	@Override
	public LinkedList<MailAccount> getMailAccounts(String domainName, Integer domain, String user) throws AonCoreException {
		return AON.getMailAccounts(domainName, domain, user, f -> f.getDomainProperty().eq(domain).and(f.getUserIdProperty().isNull()));
	}
	
	@Override
	public void deleteMailAccount(String domainName, Integer domain, String user, Integer id) throws AonCoreException {
		AON.deleteMailAccount(domainName, domain, user, id);
	}
	
	@Override
	public MailAccount saveMailAccount(String domainName, Integer domain, String user, MailAccount mailAccount) throws AonCoreException {
		return AON.saveMailAccount(domainName, domain, user, mailAccount);
	}
	
	@Override
	public HashMap<Integer, Boolean> checkMailAccounts(String domainName, Integer domain, String user) throws AonCoreException {
		LinkedList<MailAccount> mailAccounts = AON.getMailAccounts(domainName, domain, user, f -> f.getDomainProperty().eq(domain).and(f.getUserIdProperty().isNull()));
		HashMap<Integer, Boolean> result = new HashMap<Integer, Boolean>();
		mailAccounts.forEach(m -> {
			//String status = SES.verificationStatus(m.getEmail());
			boolean isVerified = SES.isVerifiedForSendingStatus(m.getEmail());
			//System.out.println(m.getId() + " : " + m.getName() + " ( " + isVerified + ") - " + status);
			
			result.put(m.getId(), isVerified);
		});
		return result;
	}

	@Override
	public HashMap<String, Boolean> getVerifiedHostEmails(String domainName, Integer domainId, String user) throws AonCoreException {
		HashMap<String, Boolean> result = new HashMap<String, Boolean>();
		HashSet<String> hosts = new HashSet<String>();
		
		Domain domain = AON.getDomain(domainName, domainId, user);
		
		Company domainCompany = AON.getCompany(domainName, domainId, user, f -> f.getDomainProperty().eq(domain.getId()));
		RegistryMedia domainCompanyWeb = AON.getRegistryMedia(new Domain().setName(domainName).setId(domainId), new User().setLogin(user), f -> f.getRegistryProperty().eq(domainCompany.getId()).and(f.getMediaProperty().eq(MediaType.WEB.value())));
		RegistryMedia domainCompanyEmail = AON.getRegistryMedia(new Domain().setName(domainName).setId(domainId), new User().setLogin(user), f -> f.getRegistryProperty().eq(domainCompany.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
		
		if(AonStringUtils.isNotBlank(domainCompanyWeb.getValue())) {
			String host = extractHost(domainCompanyWeb.getValue());
			hosts.add(host);
		}
		
		if(AonStringUtils.isNotBlank(domainCompanyEmail.getValue())) {
			String host = extractDomainFromEmail(domainCompanyEmail.getValue());
			hosts.add(host);
		}
		
		/*
		if(null != domain.getParentId()) {
			Company parentDomainCompany = AON.getCompany(domainName, domainId, user, f -> f.getDomainProperty().eq(domain.getParentId()));
			RegistryMedia parentDomainCompanyWeb = AON.getRegistryMedia(new Domain().setName(domainName).setId(domainId), new User().setLogin(user), f -> f.getRegistryProperty().eq(parentDomainCompany.getId()).and(f.getMediaProperty().eq(MediaType.WEB.value())));
			RegistryMedia parentDomainCompanyEmail = AON.getRegistryMedia(new Domain().setName(domainName).setId(domainId), new User().setLogin(user), f -> f.getRegistryProperty().eq(parentDomainCompany.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
			
			if(AonStringUtils.isNotBlank(parentDomainCompanyWeb.getValue())) {
				String host = extractHost(parentDomainCompanyWeb.getValue());
				hosts.add(host);
			}
			
			if(AonStringUtils.isNotBlank(parentDomainCompanyEmail.getValue())) {
				String host = extractDomainFromEmail(parentDomainCompanyEmail.getValue());
				hosts.add(host);
			}
		}
		*/
		
		hosts.forEach(h -> {
			boolean isVerifiedDomain = SES.isVerifiedForSendingStatus("aon.awsses@" + h);
			result.put(h, isVerifiedDomain);
		});
		
		return result;
	}
	
	private static String extractHost(String web) {
		if (AonStringUtils.isBlank(web)) return null;

	    String w = web.trim().toLowerCase();

	    // Quitar protocolo si existe
	    if (w.startsWith("http://")) {
	        w = w.substring(7);
	    } else if (w.startsWith("https://")) {
	        w = w.substring(8);
	    }

	    // Quitar path si existe
	    int slash = w.indexOf('/');
	    if (slash != -1) {
	        w = w.substring(0, slash);
	    }

	    // Quitar "www."
	    if (w.startsWith("www.")) {
	        w = w.substring(4);
	    }

	    return w;
	}

	
	private static String extractDomainFromEmail(String email) {
	    if (AonStringUtils.isBlank(email)) {
	        return null;
	    }

	    int at = email.indexOf('@');
	    if (at == -1 || at == email.length() - 1) {
	        return null;
	    }

	    return email.substring(at + 1).toLowerCase();
	}
	
	// *********************** [AMORTIZATION TYPE]
	@Override
	public List<AmortizationType> getAmortizationTypes(Occam occam, int domain) {
		return ACCOUNTING.getAmortizationTypeList(occam, domain);
	}
	
	@Override
	public void reassignScope(String domainName, Integer domainId, String user, int originScope, int finalScope, boolean deleteOrigin) throws AonCoreException {
		if(deleteOrigin) {
			AON.reassignAndDeleteScope(new Occam().setDomainName(domainName).setDomain(domainId).setUser(user), domainId, originScope, finalScope);
		} else {
			AON.reassignScope(new Occam().setDomainName(domainName).setDomain(domainId).setUser(user), domainId, originScope, finalScope);
		}
	}
	
	// *********************** [DOMAIN STATUS]
	@Override
	public Domain saveDomainStatus(String domainName, int domain, String user, RegistryStatus newStatus, Date newExpDate) throws AonCoreException {
		return AON.saveDomainStatus(domainName, domain, user, newStatus, newExpDate);
	}
	
}
