package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

public class InvoicingGroup {
	
	private Date creationDate;
	private String creationUser;
	private Integer Customer;
	private Byte customerGrouped;
	private String description;
	private Integer domain;
	private Integer id;
	private Date modificationDate;
	private String modificationUser;
	
	public Date getCreationDate() {
		return creationDate;
	}
	public InvoicingGroup setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public InvoicingGroup setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Integer getCustomer() {
		return Customer;
	}
	public InvoicingGroup setCustomer(Integer customer) {
		Customer = customer;
		return this;
	}
	public Byte getCustomerGrouped() {
		return customerGrouped;
	}
	public InvoicingGroup setCustomerGrouped(Byte customerGrouped) {
		this.customerGrouped = customerGrouped;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public InvoicingGroup setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public InvoicingGroup setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public InvoicingGroup setId(Integer id) {
		this.id = id;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public InvoicingGroup setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public InvoicingGroup setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}


}
