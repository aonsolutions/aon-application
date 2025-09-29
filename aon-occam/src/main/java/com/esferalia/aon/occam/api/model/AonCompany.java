package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AonCompany implements Serializable {

	private static final long serialVersionUID = -4970548127101817530L;

	private Domain domain;
	private Domain parentDomain;
    private Company company;
    Administration administration;
	private boolean shared;
	private String schema;
	private String login;
	
	
	public AonCompany() {

	}

	public Domain getDomain() {
		return domain;
	}

	public AonCompany setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public Domain getParentDomain() {
		return parentDomain;
	}

	public AonCompany setParentDomain(Domain parentDomain) {
		this.parentDomain = parentDomain;
		return this;
	}

	public Company getCompany() {
		return company;
	}

	public AonCompany setCompany(Company company) {
		this.company = company;
		return this;
	}
	

	public String getSchema() {
		return schema;
	}

	public AonCompany setSchema(String schema) {
		this.schema = schema;
		return this;
	}

	public Administration getAdministration() {
		return administration;
	}

	public AonCompany setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	public boolean isShared() {
		return shared;
	}

	public AonCompany setShared(boolean shared) {
		this.shared = shared;
		return this;
	}
		
	public String getLogin() {
		return login;
	}
	
	public AonCompany setLogin(String login) {
		this.login = login;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject =  new JSONObject()
			.put("registry", getCompany().getId())
			.put("id", getDomain().getId())
			.put("domain", getDomain().getName())
			.put("name", getCompany().getName())
			.put("document", getCompany().getDocument())
			.put("active", getDomain().isActive()  )
			.put("expired", getCompany().getDomain().isExpired())
			.put("administration", getAdministration() != null ? getAdministration().name() : Administration.COMMON_TERRITORY.name())
			.put("type", getDomain().getDomainType().name())
			.put("domainManagement", getDomain().isDomainManagement())
			.put("parent",getDomain().isParent())
			.put("shared", isShared())
			.put("parentId",getDomain().getParentId())
			.put("maxDefinedUsers", getDomain().getMaxDefinedUsers())
			.put("withholding", getCompany().isWithholding())
			.put("login", getLogin())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, getCompany().isVatAccrualPayment())
			.put(IJsonNames.SURCHARGE, getCompany().isSurcharge())
			.put(IJsonNames.SCHEMA, getSchema())
			;
		
			if (getCompany().getDomain().getExpirationDate() != null) {
				jsonObject.put("expirationDate", AonDateUtils.format(getDomain().getExpirationDate(), AonDateUtils.SIMPLE_DATE_FORMAT4));
			}
		
		return jsonObject;
	}
}
