package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.Month;

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
	private boolean dryRun;
	private Company company;
	
	private Integer invoicingGroup;
	private Integer customer;
	private Integer item;
	private Integer productCategory;
	private Month month;
	private Integer year;
	private boolean confidential;
	private BillingPeriod period;
	private Integer workplace;
	private Integer[] scopes;
	private Integer[] segments;

	
	// TODO: soprte en el DAO
	private CompareType startCompare;
	private Date startDate;
	private CompareType endCompare;
	private Date endDate;
	private Integer productTag;
	private ProductStatus productStatus;
	private Double price;
	private Double discount;
	private Double quantity;
	private Integer seller;
	private Integer project;
	private Integer limit;
	private Integer offset;
	private String orderBy;
	private boolean asc = true;
	
	// Parameters for invoice generation
	private Integer invoiceActivity;
	private String  invoiceSeries;
	private Integer invoiceNumber;
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
		return dryRun;
	}
	public boolean isNotDryRun() {
		return !dryRun;
	}
	public FeeBillingParams setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
		return this;
	}
	
	public Company getCompany() {
		return company;
	}
	public FeeBillingParams setCompany(Company company) {
		this.company = company;
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
	
	public boolean isConfidential() {
		return confidential;
	}
	public FeeBillingParams setConfidential(boolean confidential) {
		this.confidential = confidential;
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

	public Integer getInvoiceNumber() {
		return invoiceNumber;
	}
	public FeeBillingParams setInvoiceNumber(Integer invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
		return this;
	}
	
	public String getInvoiceComments() {
		return invoiceComments;
	}
	public FeeBillingParams setInvoiceComments(String invoiceComments) {
		this.invoiceComments = invoiceComments;
		return this;
	}
	
	public CompareType getStartCompare() {
		return startCompare;
	}
	public FeeBillingParams setStartCompare(CompareType startCompare) {
		this.startCompare = startCompare;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}
	public FeeBillingParams setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public CompareType getEndCompare() {
		return endCompare;
	}
	public FeeBillingParams setEndCompare(CompareType endCompare) {
		this.endCompare = endCompare;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	public FeeBillingParams setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public Integer getProductTag() {
		return productTag;
	}
	public FeeBillingParams setProductTag(Integer productTag) {
		this.productTag = productTag;
		return this;
	}
	
	public ProductStatus getProductStatus() {
		return productStatus;
	}
	public FeeBillingParams setProductStatus(ProductStatus productStatus) {
		this.productStatus = productStatus;
		return this;
	}
	
	public Double getPrice() {
		return price;
	}
	public FeeBillingParams setPrice(Double price) {
		this.price = price;
		return this;
	}

	public Double getDiscount() {
		return discount;
	}
	public FeeBillingParams setDiscount(Double discount) {	
		this.discount = discount;
		return this;
	}

	public Double getQuantity() {
		return quantity;
	}
	public FeeBillingParams setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	
	public Integer getSeller() {
		return seller;
	}
	public FeeBillingParams setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}
	
	public Integer getProject() {
		return project;
	}
	public FeeBillingParams setProject(Integer project) {	
		this.project = project;
		return this;
	}
	
	public Integer getLimit() {
		return limit;
	}
	public FeeBillingParams setLimit(Integer limit) {
		this.limit = limit;	
		return this;
	}
	
	public Integer getOffset() {
		return offset;
	}
	public FeeBillingParams setOffset(Integer offset) {
		this.offset = offset;
		return this;	
	}

	public String getOrderBy() {
		return orderBy;
	}
	public FeeBillingParams setOrderBy(String orderBy) {	
		this.orderBy = orderBy;
		return this;
	}
	
	public boolean isAsc() {
		return asc;
	}
	public FeeBillingParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
		
}