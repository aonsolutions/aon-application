package com.code.aon.stat;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.AonVersion;
import com.code.aon.company.WorkPlace;

public class StatParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
	private WorkPlace workPlace;
	private String domainName;
	
	public StatParams (String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
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
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	
}
