package net.aonsolutions.occam.api.model;

import java.io.Serializable;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;

public class Registry implements Serializable{
	
	private static final long serialVersionUID = 9114564405091033572L;
	
	private Integer id;
	private Integer domain;
	private String document;
	private DocumentType documentType;
	private Country documentCountry;
	private String name;
	private String alias;
	private boolean legalPerson;
	private Country nationality;
	private boolean confidential;
	
	public Integer getId() {
		return id;
	}
	public Registry setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Registry setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public Registry setDocument(String document) {
		this.document = document;
		return this;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}
	public Registry setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
		return this;
	}
	
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public Registry setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Registry setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getAlias() {
		return alias;
	}
	public Registry setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public boolean isLegalPerson() {
		return legalPerson;
	}
	public Registry setLegalPerson(boolean legalPerson) {
		this.legalPerson = legalPerson;
		return this;
	}

	public Country getNationality() {
		return nationality;
	}
	public Registry setNationality(Country nationality) {
		this.nationality = nationality;
		return this;
	}
	
	public boolean isConfidential() {
		return confidential;
	}
	public Registry setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

}
