package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AgreementLevelCategory implements Serializable{

	Integer id;
	Integer domain;
	Integer agreementLevel;
	String description;
		
	public AgreementLevelCategory() {
	
	}

	public Integer getId() {
		return id;
	}

	public AgreementLevelCategory setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public AgreementLevelCategory setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getAgreementLevel() {
		return agreementLevel;
	}

	public AgreementLevelCategory setAgreementLevel(Integer agreementLevel) {
		this.agreementLevel = agreementLevel;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AgreementLevelCategory setDescription(String description) {
		this.description = description;
		return this;
	}

}
