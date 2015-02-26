package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class Project implements Serializable {

	private static final long serialVersionUID = -5945149996376642583L;
	
	private Integer id;
	private int domain;
	private Integer projectTypeId;
	private Integer projectTypeName;
	
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryDocument;
	private String registryName;
	private String registryAlias;
	private boolean registryNaturalPerson;
	private Country registryNationality;
	private boolean registryConfidential;

	private String name;
	private String alias;
	private Date date;
	private boolean tas;
	private boolean commercial;
	private boolean reservation;
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
	public Integer getProjectTypeId() {
		return projectTypeId;
	}
	public void setProjectTypeId(Integer projectTypeId) {
		this.projectTypeId = projectTypeId;
	}
	public Integer getProjectTypeName() {
		return projectTypeName;
	}
	public void setProjectTypeName(Integer projectTypeName) {
		this.projectTypeName = projectTypeName;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isTas() {
		return tas;
	}
	public void setTas(boolean tas) {
		this.tas = tas;
	}
	public boolean isCommercial() {
		return commercial;
	}
	public void setCommercial(boolean commercial) {
		this.commercial = commercial;
	}
	public boolean isReservation() {
		return reservation;
	}
	public void setReservation(boolean reservation) {
		this.reservation = reservation;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
