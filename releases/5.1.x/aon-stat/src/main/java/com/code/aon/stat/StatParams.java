package com.code.aon.stat;

import java.util.Date;
import java.util.Locale;

import com.code.aon.commercial.enumeration.OfferStatus;

public class StatParams {
	
	private Date fromDate;
	private Date toDate;
	private String statType;
	private Integer segmentId;
	private Integer category;
	private Integer product;
	private Integer customer;
	private Locale locale;
	private Integer invoiceType;
	private OfferStatus[] offerStatuses;
	
	
	public Integer getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}
	public Integer getProduct() {
		return product;
	}
	public void setProduct(Integer product) {
		this.product = product;
	}
	public Integer getCustomer() {
		return customer;
	}
	public void setCustomer(Integer customer) {
		this.customer = customer;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public Integer getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(Integer segmentId) {
		this.segmentId = segmentId;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getStatType() {
		return statType;
	}
	public void setStatType(String statType) {
		this.statType = statType;
	}

	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public OfferStatus[] getOfferStatuses() {
		return offerStatuses;
	}
	public void setOfferStatuses(OfferStatus[] offerStatuses) {
		this.offerStatuses = offerStatuses;
	}
	
	
}
