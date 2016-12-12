package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;

public class AonConfiguration implements Serializable {

	private static final long serialVersionUID = 7723888010939038114L;
	
	private Company company;
	private User user;
	private LinkedList<AccountPeriod> periods;
	private LinkedList<String> invoiceSalesSeries;
	private LinkedList<String> invoiceRectificationSeries;
	private LinkedList<EnterpriseActivity> enterpriseActivities;
	private LinkedList<InvestAsset> investAsset;
	private LinkedList<Workplace> workplaces;
	private LinkedList<Tax> vatTaxes;
	private LinkedList<GeoZone> geozones;
	private LinkedList<Scope> availableScopes;
	private LinkedList<PayMethod> payMethods;
	private Tax defaultVatPercent;
	private LinkedList<Tax> withholdingTaxes;
	private Tax defaultWithholdingPercent;
	private String defaultInvoiceSeries;
	private Account defaultSalesAccount;
	private Account defaultPurchaseAccount;
	private Account defaultChargedVatAccount;
	private Account defaultPaidVatAccount;
	private Account defaultChargedRetAccount;
	private Account defaultPaidRetAccount;
	private Account vatNegativeAdjustAccount;
	
	private Account defaultSalary;
	private Account defaultSalaryInKind;
	private Account defaultAllowance;
	private Account defaultCompensation;
	private Account defaultCompanySocIns;
	private Account salaryChargedRet;
	private Account salaryChargedRetInKind;
	private Account defaultSocialInsurance;
	private Account defaultPendingSalary;
	
	private Date	operationsDeadline;

	public Company getCompany() {
		return company;
	}
	
	public AonConfiguration setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	public User getUser() {
		return user;
	}
	public AonConfiguration setUser(User user) {
		this.user = user;
		return this;
	}
	
	public LinkedList<AccountPeriod> getPeriods() {
		return periods;
	}
	public AonConfiguration setPeriods(LinkedList<AccountPeriod> periods) {
		this.periods = periods;
		return this;
	}
	public AccountPeriod getDefaultAccountPeriod() {
		for (AccountPeriod period : periods) {
			if (period.isDefaultPeriod()) return period; 
		}
		return null;
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
	
	
	public LinkedList<InvestAsset> getInvestAssets() {
		return investAsset;
	}
	public AonConfiguration setInvestAsset(LinkedList<InvestAsset> investAsset) {
		this.investAsset = investAsset;
		return this;
	}

	public LinkedList<Workplace> getWorkplaces() {
		return workplaces;
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
	public LinkedList<Scope> getAvailableScopes() {
		return availableScopes;
	}
	public AonConfiguration setAvailableScopes(LinkedList<Scope> availableScopes) {
		this.availableScopes = availableScopes;
		return this;
	}
	public Tax getDefaultVatPercent() {
		return defaultVatPercent;
	}
	public AonConfiguration setDefaultVatPercent(Tax defaultVatPercent) {
		this.defaultVatPercent = defaultVatPercent;
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

	public Account getDefaultSalesAccount() {
		return defaultSalesAccount;
	}

	public AonConfiguration setDefaultSalesAccount(Account defaultSalesAccount) {
		this.defaultSalesAccount = defaultSalesAccount;
		return this;
	}

	public Account getDefaultPurchaseAccount() {
		return defaultPurchaseAccount;
	}

	public AonConfiguration setDefaultPurchaseAccount(Account defaultPurchaseAccount) {
		this.defaultPurchaseAccount = defaultPurchaseAccount;
		return this;
	}

	public Account getDefaultChargedVatAccount() {
		return defaultChargedVatAccount;
	}

	public AonConfiguration setDefaultChargedVatAccount(Account defaultChargedVatAccount) {
		this.defaultChargedVatAccount = defaultChargedVatAccount;
		return this;
	}

	public Account getDefaultPaidVatAccount() {
		return defaultPaidVatAccount;
	}

	public AonConfiguration setDefaultPaidVatAccount(Account defaultPaidVatAccount) {
		this.defaultPaidVatAccount = defaultPaidVatAccount;
		return this;
	}

	public Account getDefaultChargedRetAccount() {
		return defaultChargedRetAccount;
	}

	public AonConfiguration setDefaultChargedRetAccount(Account defaultChargedRetAccount) {
		this.defaultChargedRetAccount = defaultChargedRetAccount;
		return this;
	}

	public Account getDefaultPaidRetAccount() {
		return defaultPaidRetAccount;
	}

	public AonConfiguration setDefaultPaidRetAccount(Account defaultPaidRetAccount) {
		this.defaultPaidRetAccount = defaultPaidRetAccount;
		return this;
	}

	public boolean isInvestAssetsAvailable() {
		return (getInvestAssets() != null && getInvestAssets().size() > 0);
	}
	
	public Account getVatNegativeAdjustAccount() {
		return vatNegativeAdjustAccount;
	}
	public AonConfiguration setVatNegativeAdjustAccount(Account vatNegativeAdjustAccount) {
		this.vatNegativeAdjustAccount = vatNegativeAdjustAccount;
		return this;
	}

	public Date getOperationsDeadline() {
		return operationsDeadline;
	}

	public AonConfiguration setOperationsDeadline(Date operationsDeadline) {
		this.operationsDeadline = operationsDeadline;
		return this;
	}

	public Account getDefaultSalary() {
		return defaultSalary;
	}

	public AonConfiguration setDefaultSalary(Account defaultSalary) {
		this.defaultSalary = defaultSalary;
		return this;
	}

	public Account getDefaultSalaryInKind() {
		return defaultSalaryInKind;
	}

	public AonConfiguration setDefaultSalaryInKind(Account defaultSalaryInKind) {
		this.defaultSalaryInKind = defaultSalaryInKind;
		return this;
	}

	public Account getDefaultAllowance() {
		return defaultAllowance;
	}

	public AonConfiguration setDefaultAllowance(Account defaultAllowance) {
		this.defaultAllowance = defaultAllowance;
		return this;
	}

	public Account getDefaultCompensation() {
		return defaultCompensation;
	}

	public AonConfiguration setDefaultCompensation(Account defaultCompensation) {
		this.defaultCompensation = defaultCompensation;
		return this;
	}

	public Account getDefaultCompanySocIns() {
		return defaultCompanySocIns;
	}

	public AonConfiguration setDefaultCompanySocIns(Account defaultCompanySocIns) {
		this.defaultCompanySocIns = defaultCompanySocIns;
		return this;
	}

	public Account getSalaryChargedRet() {
		return salaryChargedRet;
	}

	public AonConfiguration setSalaryChargedRet(Account salaryChargedRet) {
		this.salaryChargedRet = salaryChargedRet;
		return this;
	}

	public Account getSalaryChargedRetInKind() {
		return salaryChargedRetInKind;
	}

	public AonConfiguration setSalaryChargedRetInKind(Account salaryChargedRetInKind) {
		this.salaryChargedRetInKind = salaryChargedRetInKind;
		return this;
	}

	public Account getDefaultSocialInsurance() {
		return defaultSocialInsurance;
	}

	public AonConfiguration setDefaultSocialInsurance(Account defaultSocialInsurance) {
		this.defaultSocialInsurance = defaultSocialInsurance;
		return this;
	}

	public Account getDefaultPendingSalary() {
		return defaultPendingSalary;
	}

	public AonConfiguration setDefaultPendingSalary(Account defaultPendingSalary) {
		this.defaultPendingSalary = defaultPendingSalary;
		return this;
	}


	
}
