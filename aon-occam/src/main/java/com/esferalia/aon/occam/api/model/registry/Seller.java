package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.commission.CommissionType;
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
	public Seller setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Seller setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public CommissionType getCommissionType() {
		return commissionType;
	}
	public Seller setCommissionType(CommissionType commissionType) {
		this.commissionType = commissionType;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Seller setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Seller setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Seller setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public Seller setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}
	public String getRegistryAlias() {
		return registryAlias;
	}
	public Seller setRegistryAlias(String registryAlias) {
		this.registryAlias = registryAlias;
		return this;
	}
	public boolean isRegistryNaturalPerson() {
		return registryNaturalPerson;
	}
	public Seller setRegistryNaturalPerson(boolean registryNaturalPerson) {
		this.registryNaturalPerson = registryNaturalPerson;
		return this;
	}
	public Country getRegistryNationality() {
		return registryNationality;
	}
	public Seller setRegistryNationality(Country registryNationality) {
		this.registryNationality = registryNationality;
		return this;
	}
	public boolean isRegistryConfidential() {
		return registryConfidential;
	}
	public Seller setRegistryConfidential(boolean registryConfidential) {
		this.registryConfidential = registryConfidential;
		return this;
	}
	public String getScope() {
		return scope;
	}
	public Seller setScope(String scope) {
		this.scope = scope;
		return this;
	}
	public boolean isActive() {
		return active;
	}
	public Seller setActive(boolean active) {
		this.active = active;
		return this;
	}
	
}
