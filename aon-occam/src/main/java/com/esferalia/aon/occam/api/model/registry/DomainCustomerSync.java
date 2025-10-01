package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class DomainCustomerSync implements Serializable {

	private static final long serialVersionUID = 2671479070994363679L;

	private String schema;
	private String domainName;
	private Integer domainId;
	private String type;
	
	private Integer aonCustomer;
	
	private Integer registry;
	
	private boolean hasAonCustomer;
	private boolean hasRaddInfo;

	public DomainCustomerSync() {
		super();
	}

	public String getSchema() {
		return schema;
	}

	public DomainCustomerSync setSchema(String schema) {
		this.schema = schema;
		return this;
	}

	public String getDomainName() {
		return domainName;
	}

	public DomainCustomerSync setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public DomainCustomerSync setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}

	public String getType() {
		return type;
	}

	public DomainCustomerSync setType(String type) {
		this.type = type;
		return this;
	}

	public Integer getAonCustomer() {
		return aonCustomer;
	}

	public DomainCustomerSync setAonCustomer(Integer aonCustomer) {
		this.aonCustomer = aonCustomer;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}

	public DomainCustomerSync setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public boolean isHasAonCustomer() {
		return hasAonCustomer;
	}

	public DomainCustomerSync setHasAonCustomer(boolean hasAonCustomer) {
		this.hasAonCustomer = hasAonCustomer;
		return this;
	}

	public boolean isHasRaddInfo() {
		return hasRaddInfo;
	}

	public DomainCustomerSync setHasRaddInfo(boolean hasRaddInfo) {
		this.hasRaddInfo = hasRaddInfo;
		return this;
	}
	
	
}
