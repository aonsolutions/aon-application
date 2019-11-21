package com.esferalia.aon.occam.api.model.registry;

import java.util.Date;

public class RegistryAddInfo {

	private Integer id;
	private Integer domain;
	private Integer registry;
	private String attribute;
	private String value;
	private Date date;
	
	public RegistryAddInfo() {
	
	}

	public Integer getId() {
		return id;
	}

	public RegistryAddInfo setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public RegistryAddInfo setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public RegistryAddInfo setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public String getAttribute() {
		return attribute;
	}

	public RegistryAddInfo setAttribute(String attribute) {
		this.attribute = attribute;
		return this;
	}

	public String getValue() {
		return value;
	}

	public RegistryAddInfo setValue(String value) {
		this.value = value;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public RegistryAddInfo setDate(Date date) {
		this.date = date;
		return this;
	}
	
}
