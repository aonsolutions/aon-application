package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

public class CustomerFeeParams implements Serializable {

	private static final long serialVersionUID = -7078789958353911216L;
	
	private Integer domain;
	private Byte domainType;
	private Byte domainStatus;
	
	private Integer month;
	private Integer year;
	
	private Byte periodicity;
	
	private Integer customer;
	private Byte customerStatus;
	
	private Integer product;
	private Integer productCategory;
	private Integer productTag;
	private Byte productStatus;
	
	private String price;
	private String discount;
	
	private Byte startCompare;
	private Date startDate;
	private Byte endCompare;
	private Date endDate;
	
	private String quantity;
	
	private String workplace;
	private String seller;
	private String invoicingGroup;
	private Integer project;
	
	private Integer segment;

	private Integer limit;
	private Integer offset;
	
	public CustomerFeeParams() {
		super();
	}
	
	public Integer getDomain() {
		return domain;
	}
	public CustomerFeeParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Byte getDomainType() {
		return domainType;
	}
	public void setDomainType(Byte domainType) {
		this.domainType = domainType;
	}
	public Byte getDomainStatus() {
		return domainStatus;
	}
	public void setDomainStatus(Byte domainStatus) {
		this.domainStatus = domainStatus;
	}
	public Integer getMonth() {
		return month;
	}
	public CustomerFeeParams setMonth(Integer month) {
		this.month = month;
		return this;
	}
	public Integer getYear() {
		return year;
	}
	public CustomerFeeParams setYear(Integer year) {
		this.year = year;
		return this;
	}
	public Byte getPeriodicity() {
		return periodicity;
	}
	public CustomerFeeParams setPeriodicity(Byte periodicity) {
		this.periodicity = periodicity;
		return this;
	}
	public Integer getCustomer() {
		return customer;
	}
	public CustomerFeeParams setCustomer(Integer customer) {
		this.customer = customer;
		return this;
	}
	public Byte getCustomerStatus() {
		return customerStatus;
	}
	public CustomerFeeParams setCustomerStatus(Byte customerStatus) {
		this.customerStatus = customerStatus;
		return this;
	}
	public Integer getProduct() {
		return product;
	}
	public CustomerFeeParams setProduct(Integer product) {
		this.product = product;
		return this;
	}
	public Integer getProductCategory() {
		return productCategory;
	}
	public CustomerFeeParams setProductCategory(Integer productCategory) {
		this.productCategory = productCategory;
		return this;
	}
	public Integer getProductTag() {
		return productTag;
	}
	public CustomerFeeParams setProductTag(Integer productTag) {
		this.productTag = productTag;
		return this;
	}
	public Byte getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(Byte productStatus) {
		this.productStatus = productStatus;
	}
	public String getPrice() {
		return price;
	}
	public CustomerFeeParams setPrice(String price) {
		this.price = price;
		return this;
	}
	public String getDiscount() {
		return discount;
	}
	public CustomerFeeParams setDiscount(String discount) {
		this.discount = discount;
		return this;
	}
	public Byte getStartCompare() {
		return startCompare;
	}
	public CustomerFeeParams setStartCompare(Byte startCompare) {
		this.startCompare = startCompare;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public CustomerFeeParams setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public Byte getEndCompare() {
		return endCompare;
	}
	public CustomerFeeParams setEndCompare(Byte endCompare) {
		this.endCompare = endCompare;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public CustomerFeeParams setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public String getQuantity() {
		return quantity;
	}
	public CustomerFeeParams setQuantity(String quantity) {
		this.quantity = quantity;
		return this;
	}
	public String getWorkplace() {
		return workplace;
	}
	public CustomerFeeParams setWorkplace(String workplace) {
		this.workplace = workplace;
		return this;
	}
	public String getSeller() {
		return seller;
	}
	public CustomerFeeParams setSeller(String seller) {
		this.seller = seller;
		return this;
	}
	public String getInvoicingGroup() {
		return invoicingGroup;
	}
	public CustomerFeeParams setInvoicingGroup(String invoicingGroup) {
		this.invoicingGroup = invoicingGroup;
		return this;
	}
	public Integer getProject() {
		return project;
	}
	public CustomerFeeParams setProject(Integer project) {
		this.project = project;
		return this;
	}
	public Integer getLimit() {
		return limit;
	}
	public CustomerFeeParams setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}
	public Integer getOffset() {
		return offset;
	}
	public CustomerFeeParams setOffset(Integer offset) {
		this.offset = offset;
		return this;
	}

	public Integer getSegment() {
		return this.segment;
	}
	public CustomerFeeParams setSegment(Integer segment) {
		this.segment = segment;
		return this;
	}
}
