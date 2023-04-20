package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

public class CustomerFeeParams implements Serializable {

	private static final long serialVersionUID = -7078789958353911216L;
	
	private Integer domain;
	
	private Date billingDate;
	
	private String customer;
	private Byte customerStatus;
	
	private String productCode;
	
	private String price;
	private String discount;
	
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
	public Date getBillingDate() {
		return billingDate;
	}
	public CustomerFeeParams setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
		return this;
	}
	public String getCustomer() {
		return customer;
	}
	public CustomerFeeParams setCustomer(String customer) {
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
	public String getProductCode() {
		return productCode;
	}
	public CustomerFeeParams setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
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
}
