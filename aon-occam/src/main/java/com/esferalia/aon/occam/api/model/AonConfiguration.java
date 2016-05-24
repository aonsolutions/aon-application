package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.security.User;

public class AonConfiguration implements Serializable {

	private static final long serialVersionUID = 7723888010939038114L;
	
	private Company company;
	private User user;
	private LinkedList<AccountPeriod> periods;
	private LinkedList<String> invoiceSalesSeries;
	private LinkedList<EnterpriseActivity> enterpriseActivities;
	private LinkedList<Workplace> workplaces;
	private LinkedList<Tax> vatTaxes;
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

	public LinkedList<String> getInvoiceSalesSeries() {
		return invoiceSalesSeries;
	}

	public AonConfiguration setInvoiceSalesSeries(LinkedList<String> invoiceSalesSeries) {
		this.invoiceSalesSeries = invoiceSalesSeries;
		return this;
	}

	public LinkedList<EnterpriseActivity> getActivities() {
		return enterpriseActivities;
	}

	public AonConfiguration setEnterpriseActivities(LinkedList<EnterpriseActivity> enterpriseActivities) {
		this.enterpriseActivities = enterpriseActivities;
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

	

}
