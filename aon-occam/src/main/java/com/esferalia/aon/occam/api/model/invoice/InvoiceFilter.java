package com.esferalia.aon.occam.api.model.invoice;

import java.util.Date;

public class InvoiceFilter {
	private String description;
	private String status;
	private String[] types;
	private Byte[] typesByte;
	private Integer page;
	private Integer perPage;
	private Byte recorded;
	private Date from;
	private Date to;
	private Integer registry;
	
	public String getDescription() {
		return description;
	}

	public InvoiceFilter setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public InvoiceFilter setStatus(String status) {
		this.status = status;
		return this;
	}

	public String[] getTypes() {
		return types;
	}

	public InvoiceFilter setTypes(String[] types) {
		this.types = types;
		return this;
	}
	
	public Byte[] getTypesByte() {
		return typesByte;
	}

	public InvoiceFilter setTypesByte(Byte[] typesByte) {
		this.typesByte = typesByte;
		return this;
	}

	public Integer getPage() {
		return page;
	}

	public InvoiceFilter setPage(Integer page) {
		this.page = page;
		return this;
	}

	public Integer getPerPage() {
		return perPage;
	}

	public InvoiceFilter setPerPage(Integer perPage) {
		this.perPage = perPage;
		return this;
	}
	
	public Date getFrom() {
		return from;
	}
	
	public InvoiceFilter setFrom(Date from) {
		this.from = from;
		return this;
	}
	
	public Date getTo() {
		return to;
	}
	
	public InvoiceFilter setTo(Date to) {
		this.to = to;
		return this;
	}
	
	public Byte getRecorded() {
		return recorded;
	}

	public InvoiceFilter setRecorded(Byte recorded) {
		this.recorded = recorded;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	
	public InvoiceFilter setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
}
