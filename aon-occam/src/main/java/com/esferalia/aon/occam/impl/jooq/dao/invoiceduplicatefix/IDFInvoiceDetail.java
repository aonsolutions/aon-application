package com.esferalia.aon.occam.impl.jooq.dao.invoiceduplicatefix;

import java.io.Serializable;
import java.sql.Timestamp;

public class IDFInvoiceDetail implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer domain;
	private String description;
	private Timestamp date;
	private Integer item;
	private Integer workplace;
	
	private Integer invoice;
	
	public Integer getId() {
		return id;
	}
	
	public IDFInvoiceDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public IDFInvoiceDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Timestamp getDate() {
		return date;
	}
	
	public IDFInvoiceDetail setDate(Timestamp date) {
		this.date = date;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public IDFInvoiceDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Integer getItem() {
		return item;
	}
	
	public IDFInvoiceDetail setItem(Integer item) {
		this.item = item;
		return this;
	}
	
	public Integer getWorkplace() {
		return workplace;
	}
	
	public IDFInvoiceDetail setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	
	public Integer getInvoice() {
		return invoice;
	}
	
	public IDFInvoiceDetail setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}
}
