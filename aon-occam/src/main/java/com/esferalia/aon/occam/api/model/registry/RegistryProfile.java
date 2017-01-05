package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

public class RegistryProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private Date lastUpdate;
	private Integer question;
	private String valueText;
	private Date valueDate;
	private Double valueNumber;	
	
	public RegistryProfile() {
		
	}

	public Integer getId() {
		return id;
	}

	public RegistryProfile setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public RegistryProfile setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public RegistryProfile setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public RegistryProfile setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
		return this;
	}

	public Integer getQuestion() {
		return question;
	}

	public RegistryProfile setQuestion(Integer question) {
		this.question = question;
		return this;
	}

	public String getValueText() {
		return valueText;
	}

	public RegistryProfile setValueText(String valueText) {
		this.valueText = valueText;
		return this;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public RegistryProfile setValueDate(Date valueDate) {
		this.valueDate = valueDate;
		return this;
	}

	public Double getValueNumber() {
		return valueNumber;
	}

	public RegistryProfile setValueNumber(Double valueNumber) {
		this.valueNumber = valueNumber;
		return this;
	}

}
