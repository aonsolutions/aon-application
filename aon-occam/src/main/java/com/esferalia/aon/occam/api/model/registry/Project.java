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
	private String projectTypeName;
	
	private Integer registryId;
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
	public Project setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Project setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getProjectTypeId() {
		return projectTypeId;
	}
	public Project setProjectTypeId(Integer projectTypeId) {
		this.projectTypeId = projectTypeId;
		return this;
	}
	public String getProjectTypeName() {
		return projectTypeName;
	}
	public Project setProjectTypeName(String projectTypeName) {
		this.projectTypeName = projectTypeName;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Project setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Project setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Project setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public Project setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}
	public String getRegistryAlias() {
		return registryAlias;
	}
	public Project setRegistryAlias(String registryAlias) {
		this.registryAlias = registryAlias;
		return this;
	}
	public boolean isRegistryNaturalPerson() {
		return registryNaturalPerson;
	}
	public Project setRegistryNaturalPerson(boolean registryNaturalPerson) {
		this.registryNaturalPerson = registryNaturalPerson;
		return this;
	}
	public Country getRegistryNationality() {
		return registryNationality;
	}
	public Project setRegistryNationality(Country registryNationality) {
		this.registryNationality = registryNationality;
		return this;
	}
	public boolean isRegistryConfidential() {
		return registryConfidential;
	}
	public Project setRegistryConfidential(boolean registryConfidential) {
		this.registryConfidential = registryConfidential;
		return this;
	}
	public String getName() {
		return name;
	}
	public Project setName(String name) {
		this.name = name;
		return this;
	}
	public String getAlias() {
		return alias;
	}
	public Project setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public Project setDate(Date date) {
		this.date = date;
		return this;
	}
	public boolean isTas() {
		return tas;
	}
	public Project setTas(boolean tas) {
		this.tas = tas;
		return this;
	}
	public boolean isCommercial() {
		return commercial;
	}
	public Project setCommercial(boolean commercial) {
		this.commercial = commercial;
		return this;
	}
	public boolean isReservation() {
		return reservation;
	}
	public Project setReservation(boolean reservation) {
		this.reservation = reservation;
		return this;
	}
	public boolean isActive() {
		return active;
	}
	public Project setActive(boolean active) {
		this.active = active;
		return this;
	}
	public Integer getRegistryId() {
		return registryId;
	}
	public Project setRegistryId(Integer registryId) {
		this.registryId = registryId;
		return this; 
	}
	
}
