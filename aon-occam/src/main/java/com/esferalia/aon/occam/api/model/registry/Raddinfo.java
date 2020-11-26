package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

public class Raddinfo implements Serializable {
	
	private static final long serialVersionUID = -405022432153694637L;
	private Integer id;
	private Integer domain;
	private Integer registry;
	private String attribute;
	private String value;
	private Date valueDate;

	public Integer getId() {
		return id;
	}
	public Raddinfo setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Raddinfo setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public Raddinfo setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public String getAttribute() {
		return attribute;
	}
	public Raddinfo setAttribute(String attribute) {
		this.attribute = attribute;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	public Raddinfo setValue(String value) {
		this.value = value;
		return this;
	}
	
	public Date getValueDate() {
		return valueDate;
	}
	public Raddinfo setValueDate(Date valueDate) {
		this.valueDate = valueDate;
		return this;
	}

	
}