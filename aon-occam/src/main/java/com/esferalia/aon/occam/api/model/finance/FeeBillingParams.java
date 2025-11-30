package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.Month;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class FeeBillingParams implements Serializable {
	public enum CompareType {
		EQUAL("="),
		LESS_EQUAL("\u2264"),
		GREATER_EQUAL("\u2265")
		;

		private String description;

		private CompareType(String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
		public static Optional<CompareType> safeValueOf(Integer i) {
		   	if (i == null) return Optional.empty();
	    	if (i<0 || i >= CompareType.values().length) return Optional.empty();
	    	return Optional.of(CompareType.values()[i]);
	 	}
	}

	private static final long serialVersionUID = 5882953115444185170L;
	
	private Integer domainId;
	private boolean notDryRun;
	private boolean saveAsProforma;
	private boolean communicable;
	private Company company;
	private Integer certId;
	
	private Integer invoicingGroup;
	private Integer customer;
	private Integer item;
	private Integer productCategory;
	private Month month;
	private Integer year;
	private SecurityLevel securityLevel;
	private BillingPeriod period;
	private Integer workplace;
	private Integer[] scopes;
	private Integer[] segments;
	
	// Parameters for invoice generation
	private Integer invoiceActivity;
	private String  invoiceSeries;
	private Date invoiceDate;
	private String invoiceComments;
	
	public Integer getDomainId() {
		return domainId;
	}
	public FeeBillingParams setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}
	
	public boolean isDryRun() {
		return !notDryRun;
	}
	public boolean isNotDryRun() {
		return notDryRun;
	}
	public FeeBillingParams setDryRun(boolean dryRun) {
		this.notDryRun = !dryRun;
		return this;
	}
	
	public boolean mustSaveAsProforma() {
		return saveAsProforma;
	}
	public FeeBillingParams setSaveAsProforma(boolean saveAsProforma) {
		this.saveAsProforma = saveAsProforma;
		return this;
	}
	
	public boolean isCommunicable() {
		return communicable;
	}
	public FeeBillingParams setCommunicable(boolean communicable) {
		this.communicable = communicable;
		return this;
	}
	
	public Company getCompany() {
		return company;
	}
	public FeeBillingParams setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	public Integer getCertId() {
		return certId;
	}
	public FeeBillingParams setCertId(Integer certId) {
		this.certId = certId;
		return this;
	}
	
	public Integer getInvoicingGroup() {
		return invoicingGroup;
	}
	public FeeBillingParams setInvoicingGroup(Integer invoicingGroup) {
		this.invoicingGroup = invoicingGroup;
		return this;
	}
	
	public Integer getCustomer() {
		return customer;
	}
	public FeeBillingParams setCustomer(Integer customer) {
		this.customer = customer;
		return this;
	}
	
	public Integer getItem() {
		return item;
	}
	public FeeBillingParams setItem(Integer item) {
		this.item = item;
		return this;
	}

	public Integer getProductCategory() {
		return productCategory;
	}
	public FeeBillingParams setProductCategory(Integer productCategory) {
		this.productCategory = productCategory;
		return this;
	}
	
	public Month getMonth() {
		return month;
	}
	public FeeBillingParams setMonth(Month month) {
		this.month = month;
		return this;
	}

	public Integer getYear() {
		return year;
	}
	public FeeBillingParams setYear(Integer year) {
		this.year = year;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public FeeBillingParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public BillingPeriod getPeriod() {
		return period;
	}
	public FeeBillingParams setPeriod(BillingPeriod period) {
		this.period = period;
		return this;
	}
	
	public Integer getWorkplace() {
		return workplace;
	}
	public FeeBillingParams setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}

	public Integer[] getScopes() {
		return scopes;
	}
	public FeeBillingParams setScopes(Integer[] scopes) {
		this.scopes = scopes;
		return this;
	}

	public Integer[] getSegments() {
		return segments;
	}
	public FeeBillingParams setSegments(Integer[] segments) {
		this.segments = segments;
		return this;
	}
	
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	public FeeBillingParams setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
		return this;
	}

	public Integer getInvoiceActivity() {
		return invoiceActivity;
	}
	public FeeBillingParams setInvoiceActivity(Integer invoiceActivity) {
		this.invoiceActivity = invoiceActivity;
		return this;
	}
	
	public String getInvoiceSeries() {
		return invoiceSeries;
	}
	public FeeBillingParams setInvoiceSeries(String invoiceSeries) {
		this.invoiceSeries = invoiceSeries;
		return this;
	}

	public String getInvoiceComments() {
		return invoiceComments;
	}
	public FeeBillingParams setInvoiceComments(String invoiceComments) {
		this.invoiceComments = invoiceComments;
		return this;
	}
}