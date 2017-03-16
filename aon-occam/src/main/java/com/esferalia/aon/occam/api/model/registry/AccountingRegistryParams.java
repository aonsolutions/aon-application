package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class AccountingRegistryParams implements Serializable {

	private static final long serialVersionUID = -7078789958353911216L;
	
	private AccountingRegistryType type;
	private Integer id;
	private String document;
	private Country documentCountry;
	private DocumentType documentType;

	public AccountingRegistryType getType() {
		return type;
	}
	public AccountingRegistryParams setType(AccountingRegistryType type) {
		this.type = type;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public AccountingRegistryParams setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getDocument() {
		return document;
	}
	public AccountingRegistryParams setDocument(String document) {
		this.document = document;
		return this;
	}
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public AccountingRegistryParams setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public AccountingRegistryParams setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
		return this;
	}
}
