package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

public class InvoiceData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer invoice;
	private String name;
	private String value;
	private Date startDate;
	private Date endDate;

	public Integer getId() {
		return id;
	}

	public InvoiceData setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public InvoiceData setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getInvoice() {
		return invoice;
	}

	public InvoiceData setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public String getName() {
		return name;
	}

	public InvoiceData setName(String name) {
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}

	public InvoiceData setValue(String value) {
		this.value = value;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public InvoiceData setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public InvoiceData setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public boolean isEmpty() {
		return getId() == null && getDomain() == null && getInvoice() == null;		
	}
}
