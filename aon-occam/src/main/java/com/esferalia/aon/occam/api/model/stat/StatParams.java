package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import com.esferalia.aon.occam.api.model.type.InvoiceType;

public class StatParams implements Serializable {
	
	private static final long serialVersionUID = 8321751053437854437L;
	
	private String domainName;
	private int domain;
	private HashSet<InvoiceType> invoiceTypes;
	private Integer year;
	private Date from;
	private Date to;
	
	public HashSet<InvoiceType> getInvoiceTypes() {
		return invoiceTypes;
	}
	public StatParams setInvoiceType(HashSet<InvoiceType> invoiceTypes) {
		this.invoiceTypes = invoiceTypes;
		return this;
	}
	public StatParams addInvoiceType(InvoiceType invoiceType) {
		if (this.invoiceTypes == null) {
			this.invoiceTypes = new HashSet<InvoiceType>();
		}
		if (!getInvoiceTypes().contains(invoiceType)) {
			getInvoiceTypes().add(invoiceType);
		}
		return this;
	}
	public StatParams removeInvoiceType(InvoiceType type) {
		if (this.invoiceTypes != null && getInvoiceTypes().contains(type)) {
			getInvoiceTypes().remove(type);
		}
		return this;
	}

	public String getDomainName() {
		return domainName;
	}
	public StatParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public StatParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Date getFrom() {
		return from;
	}
	public StatParams setFrom(Date from) {
		this.from = from;
		return this;
	}
	public Date getTo() {
		return to;
	}
	public StatParams setTo(Date to) {
		this.to = to;
		return this;
	}
	public Integer getYear() {
		return year;
	}
	public StatParams setYear(Integer year) {
		this.year = year;
		return this;
	}
	
	
}
