package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Customer;

public class InvoicingGroup implements Serializable{
	
	private static final long serialVersionUID = -1155988586174019199L;
	
	private Integer id;
	private Integer domain;
	private Customer customer;
	private String description;
	private boolean customerGrouped;
	
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	public Integer getId() {
		return id;
	}
	public InvoicingGroup setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public InvoicingGroup setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Customer getCustomer() {
		return customer;
	}
	public InvoicingGroup setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public InvoicingGroup setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isCustomerGrouped() {
		return customerGrouped;
	}
	public InvoicingGroup setCustomerGrouped(boolean customerGrouped) {
		this.customerGrouped = customerGrouped;
		return this;
	}

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
