package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AgreementLevel implements Serializable{

	Integer id;
	Integer domain;
	Integer agreement;
	String description;
		
	public AgreementLevel() {
	
	}

	public Integer getId() {
		return id;
	}

	public AgreementLevel setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public AgreementLevel setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getAgreement() {
		return agreement;
	}

	public AgreementLevel setAgreement(Integer agreement) {
		this.agreement = agreement;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AgreementLevel setDescription(String description) {
		this.description = description;
		return this;
	}

}
