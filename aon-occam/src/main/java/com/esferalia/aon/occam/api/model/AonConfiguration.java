package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.config.AccountingConfig;
import com.esferalia.aon.occam.api.model.config.FiscalConfig;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PayMethodTypeDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class AonConfiguration implements Serializable {

	private static final long serialVersionUID = 7723888010939038114L;
	
	private Company company;
	private Domain domain;
	private Integer userOperator;
	private String md5;
	private boolean aonSolutions;
	private AccountingConfig accounting = new AccountingConfig();
	private FiscalConfig fiscal = new FiscalConfig();
	private DomainUserRoles dur;

	// 	------------------- Revisar	
	private User user;
	private LinkedList<String> invoiceSalesSeries;
	private LinkedList<String> invoiceRectificationSeries;
	private LinkedList<EnterpriseActivity> enterpriseActivities;
	private LinkedList<EnterpriseActivity> allEnterpriseActivities;
	private LinkedList<InvestAsset> investAsset;
	private LinkedList<Workplace> workplaces;
	private LinkedList<Tax> vatTaxes;
	private LinkedList<GeoZone> geozones;
	private LinkedList<Scope> availableScopes;
	private LinkedList<PayMethod> payMethods;
	private LinkedList<PayMethodTypeDetail> payMethodTypeDetails;
	private LinkedList<Tax> withholdingTaxes;
	private LinkedList<Segment> segments;
	
	private boolean ocrActive;
	private Item ocrDefaultItem;
	private boolean betaEnabled;
	private boolean alphaEnabled;

	private Tax defaultVatPercent;
	private Tax defaultWithholdingPercent;
	private String defaultInvoiceSeries;
	private Date	operationsDeadline;
	private AccountingRegistry defaultCreditor;
	private LinkedList<Domain> childDomains;
	// 	------------------- Revisar
	
	public DomainUserRoles getDur() {
		return dur;
	}
	
	public AonConfiguration setDur(DomainUserRoles dur) {
		this.dur = dur;
		return this;
	}
	
	public Company getCompany() {
		return company;
	}
	
	public AonConfiguration setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}
	public AonConfiguration setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public User getUser() {
		return user;
	}
	public AonConfiguration setUser(User user) {
		this.user = user;
		return this;
	}
	
	public Integer getUserOperator() {
		return userOperator;
	}
	public AonConfiguration setUserOperator(Integer userOperator) {
		this.userOperator = userOperator;
		return this;
	}
	
	public String getMd5() {
		return md5;
	}
	public AonConfiguration setMd5(String md5) {
		this.md5 = md5;
		return this;
	}
	
	public boolean isAonSolutions() {
		return aonSolutions;
	}
	public AonConfiguration setAonSolutions(boolean aonSolutions) {
		this.aonSolutions = aonSolutions;
		return this;
	}
	
	public LinkedList<String> getInvoiceSalesSeries() {
		return invoiceSalesSeries;
	}

	public AonConfiguration setInvoiceSalesSeries(LinkedList<String> invoiceSalesSeries) {
		this.invoiceSalesSeries = invoiceSalesSeries;
		return this;
	}
	public void addInvoiceSalesSeries(String serie) {
		if (this.invoiceSalesSeries == null) {
			this.invoiceSalesSeries = new LinkedList<String>();
		}
		this.invoiceSalesSeries.add(serie);
	}

	public LinkedList<String> getInvoiceRectificationSalesSeries() {
		return invoiceRectificationSeries;
	}

	public AonConfiguration setInvoiceRectificationSalesSeries(LinkedList<String> invoiceRectificationSeries) {
		this.invoiceRectificationSeries = invoiceRectificationSeries;
		return this;
	}
	public void addInvoiceRectificationSalesSeries(String serie) {
		if (this.invoiceRectificationSeries == null) {
			this.invoiceRectificationSeries = new LinkedList<String>();
		}
		this.invoiceRectificationSeries.add(serie);
	}

	public LinkedList<EnterpriseActivity> getAllActivities() {
		return allEnterpriseActivities;
	}
	public AonConfiguration setAllEnterpriseActivities(LinkedList<EnterpriseActivity> allEnterpriseActivities) {
		this.allEnterpriseActivities = allEnterpriseActivities;
		return this;
	}
	public boolean hasAllActivities() {
		return this.allEnterpriseActivities != null && this.allEnterpriseActivities.size() > 0;
	}
	public LinkedList<EnterpriseActivity> getActivities() {
		return enterpriseActivities;
	}

	public AonConfiguration setEnterpriseActivities(LinkedList<EnterpriseActivity> enterpriseActivities) {
		this.enterpriseActivities = enterpriseActivities;
		return this;
	}
	public boolean hasActivities() {
		return this.enterpriseActivities != null && this.enterpriseActivities.size() > 0;
	}
	
	public EnterpriseActivity getMainActivity() {
		if (hasActivities()) {
			for (EnterpriseActivity act : this.enterpriseActivities) {
				if (act.isPrincipal()) return act;
			}
		}
		return null;
	}
	public EnterpriseActivity getActivity( Integer id ) {
		if (hasActivities()) {
			for (EnterpriseActivity act : this.enterpriseActivities) {
				if (AonNumberUtils.equals(act.getId(),id)) return act;
			}
		}
		return null;
	}
	
	
	public LinkedList<InvestAsset> getInvestAssets() {
		return investAsset;
	}
	public AonConfiguration setInvestAsset(LinkedList<InvestAsset> investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	public boolean isInvestAssetsAvailable() {
		return (getInvestAssets() != null && getInvestAssets().size() > 0);
	}

	public LinkedList<Workplace> getWorkplaces() {
		return workplaces;
	}
	public Optional<Workplace> getFirstWorkplace() {
		return AonCollectionUtils.stream(getWorkplaces() )
			.findFirst();
	}
	public AonConfiguration setWorkplaces(LinkedList<Workplace> workplaces) {
		this.workplaces = workplaces;
		return this;
	}

	public LinkedList<Tax> getVatTaxes() {
		return vatTaxes;
	}
	public AonConfiguration setVatTaxes(LinkedList<Tax> vatTaxes) {
		this.vatTaxes = vatTaxes;
		return this;
	}
	public LinkedList<GeoZone> getGeozones() {
		return geozones;
	}
	public boolean hasGeozones() {
		return this.geozones != null && this.geozones.size() > 0;
	}
	public AonConfiguration setGeozones(LinkedList<GeoZone> geozones) {
		this.geozones = geozones;
		return this;
	}
	public LinkedList<PayMethod> getPayMethods() {
		return payMethods;
	}
	public AonConfiguration setPayMethods(LinkedList<PayMethod> payMethods) {
		this.payMethods = payMethods;
		return this;
	}
	public LinkedList<PayMethodTypeDetail> getPayMethodTypeDetails() {
		return payMethodTypeDetails;
	}
	public AonConfiguration setPayMethodTypeDetails(LinkedList<PayMethodTypeDetail> payMethodTypeDetails) {
		this.payMethodTypeDetails = payMethodTypeDetails;
		return this;
	}
	public LinkedList<Scope> getAvailableScopes() {
		return availableScopes;
	}
	public AonConfiguration setAvailableScopes(LinkedList<Scope> availableScopes) {
		this.availableScopes = availableScopes;
		return this;
	}
	public boolean hasAvailableScopes() {
		return getAvailableScopes() != null && !getAvailableScopes().isEmpty();
	}
			
	public Tax getDefaultVatPercent() {
		return defaultVatPercent;
	}
	public AonConfiguration setDefaultVatPercent(Tax defaultVatPercent) {
		this.defaultVatPercent = defaultVatPercent;
		return this;
	}
	
	public boolean hasSegments() {
		return segments != null && !segments.isEmpty();
	}
	public LinkedList<Segment> getSegments() {
		return segments;
	}
	public AonConfiguration setSegments(LinkedList<Segment> segments) {
		this.segments = segments;
		return this;
	}

	public LinkedList<Tax> getWithholdingTaxes() {
		return withholdingTaxes;
	}
	public AonConfiguration setWithholdingTaxes(LinkedList<Tax> withholdingTaxes) {
		this.withholdingTaxes = withholdingTaxes;
		return this;
	}
	public Tax getDefaultWithholdingPercent() {
		return defaultWithholdingPercent;
	}
	public AonConfiguration setDefaultWithholdingPercent(Tax defaultWithholdingPercent) {
		this.defaultWithholdingPercent = defaultWithholdingPercent;
		return this;
	}
	public String getDefaultInvoiceSeries() {
		return defaultInvoiceSeries;
	}

	public AonConfiguration setDefaultInvoiceSeries(String defaultInvoiceSeries) {
		this.defaultInvoiceSeries = defaultInvoiceSeries;
		return this;
	}

	public Date getOperationsDeadline() {
		return operationsDeadline;
	}

	public AonConfiguration setOperationsDeadline(Date operationsDeadline) {
		this.operationsDeadline = operationsDeadline;
		return this;
	}

	public LinkedList<Domain> getChildDomains() {
		return childDomains;
	}
	public AonConfiguration setChildDomains(LinkedList<Domain> childDomains) {
		this.childDomains = childDomains;
		return this;
	}

	public AccountingRegistry getDefaultCreditor() {
		return defaultCreditor;
	}
	public AonConfiguration setDefaultCreditor(AccountingRegistry defaultCreditor) {
		this.defaultCreditor = defaultCreditor;
		return this;
	}
	
	public boolean isBetaEnabled() {
		return betaEnabled;
	}
	public AonConfiguration setBetaEnabled(boolean betaEnabled) {
		this.betaEnabled = betaEnabled;
		return this;
	}
	
	public boolean isAlphaEnabled() {
		return alphaEnabled;
	}
	public AonConfiguration setAlphaEnabled(boolean alphaEnabled) {
		this.alphaEnabled = alphaEnabled;
		return this;
	}
	public boolean isOCRActive() {
		return ocrActive;
	}
	public AonConfiguration setOCRActive(boolean ocrActive) {
		this.ocrActive = ocrActive;
		return this;
	}
	
	public Item getOcrDefaultItem() {
		return ocrDefaultItem;
	}
	public AonConfiguration setOcrDefaultItem(Item ocrDefaultItem) {
		this.ocrDefaultItem = ocrDefaultItem;
		return this;
	}
	
	public FiscalConfig fiscal() {
		return fiscal;
	}
	public AccountingConfig accounting() {
		return accounting;
	}
	
}
