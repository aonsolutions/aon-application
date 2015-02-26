package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class Seller implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private int domain;
	private CommissionType commissionType;
	
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryDocument;
	private String registryName;
	private String registryAlias;
	private boolean registryNaturalPerson;
	private Country registryNationality;
	private boolean registryConfidential;
	
	private String scope;
	private boolean active;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public CommissionType getCommissionType() {
		return commissionType;
	}
	public void setCommissionType(CommissionType commissionType) {
		this.commissionType = commissionType;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public void setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public void setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public void setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
	}
	public String getRegistryName() {
		return registryName;
	}
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}
	public String getRegistryAlias() {
		return registryAlias;
	}
	public void setRegistryAlias(String registryAlias) {
		this.registryAlias = registryAlias;
	}
	public boolean isRegistryNaturalPerson() {
		return registryNaturalPerson;
	}
	public void setRegistryNaturalPerson(boolean registryNaturalPerson) {
		this.registryNaturalPerson = registryNaturalPerson;
	}
	public Country getRegistryNationality() {
		return registryNationality;
	}
	public void setRegistryNationality(Country registryNationality) {
		this.registryNationality = registryNationality;
	}
	public boolean isRegistryConfidential() {
		return registryConfidential;
	}
	public void setRegistryConfidential(boolean registryConfidential) {
		this.registryConfidential = registryConfidential;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
}
