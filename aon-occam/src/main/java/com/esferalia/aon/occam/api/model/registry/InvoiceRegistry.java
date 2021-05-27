package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceRegistry implements Serializable {
	private static final long serialVersionUID = -5523495170211215075L;

	private AccountingRegistryType type;
	
	private Integer id;
	private int domain;

	private String document;
	private Country documentCountry;
	private DocumentType documentType;
	private Country nationality;
	private String name;
	private String alias;
	private int scope;

	public Integer getId() {
		return id;
	}

	public InvoiceRegistry setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public InvoiceRegistry setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public InvoiceRegistry setDocument(String document) {
		this.document = document;
		return this;
	}
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public InvoiceRegistry setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public InvoiceRegistry setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
		return this;
	}
	public Country getNationality() {
		return nationality;
	}
	public InvoiceRegistry setNationality(Country nationality) {
		this.nationality = nationality;
		return this;
	}
	public String getName() {
		return name;
	}
	public InvoiceRegistry setName(String name) {
		this.name = name;
		return this;
	}

	public AccountingRegistryType getType() {
		return type;
	}
	
	public InvoiceRegistry setType(AccountingRegistryType type) {
		this.type = type;
		return this;
	}
	
	public int getScope() {
		return scope;
	}

	public InvoiceRegistry setScope(int scope) {
		this.scope = scope;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public InvoiceRegistry setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public static String getFullDescription(InvoiceRegistry accRegistry) {
		return AonStringUtils.defaultIfEmpty((accRegistry.getDocumentType() != null
						?accRegistry.getDocumentType().getDescription()
						:null)
					, AonStringUtils.repeat(AonStringUtils.QUESTION, 3))
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.defaultIfEmpty((accRegistry.getDocumentCountry()!=null
						?accRegistry.getDocumentCountry().getIso2()
						:null)
					, AonStringUtils.repeat(AonStringUtils.QUESTION, 2)) 
				+ AonStringUtils.SLASH
				+ AonStringUtils.defaultIfEmpty(accRegistry.getDocument()
					, AonStringUtils.repeat(AonStringUtils.QUESTION, 9))
				+ AonStringUtils.SPACE
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.SPACE
				+ accRegistry.getName()
				+ AonStringUtils.SPACE
				+ (AonStringUtils.isNotBlank(accRegistry.getAlias())
					?(AonStringUtils.SPACE + AonStringUtils.OPEN_PARENTHESIS + accRegistry.getAlias() + AonStringUtils.CLOSE_PARENTHESIS)
					:AonStringUtils.EMPTY)
				;
	}

}
