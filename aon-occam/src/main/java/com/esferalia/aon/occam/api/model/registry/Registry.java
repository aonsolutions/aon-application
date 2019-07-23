package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Registry implements Serializable{
	
	private static final long serialVersionUID = 9114564405091033572L;
	
	private String alias;
	private String document;
	private Country documentCountry;
	private DocumentType documentType;
	private Integer domain;
	private Integer id;
	private String name;
	private Country nationality;
	private SecurityLevel securityLevel;
	private Byte type;
	private RAddress address;
	
	public String getAlias() {
		return alias;
	}
	public Registry setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public Registry setDocument(String document) {
		this.document = document;
		return this;
	}
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public Registry setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public Registry setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Registry setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public Registry setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public Registry setName(String name) {
		this.name = name;
		return this;
	}
	public Country getNationality() {
		return nationality;
	}
	public Registry setNationality(Country nationality) {
		this.nationality = nationality;
		return this;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public Registry setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public Registry setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
	public Byte getType() {
		return type;
	}
	public Registry setType(Byte type) {
		this.type = type;
		return this;
	}
	
	public RAddress getAddress() {
		return address;
	}
	public Registry setAddress(RAddress address) {
		this.address = address;
		return this;
	}
	
	public static String getFullDescription(Registry registry) {
		return AonStringUtils.defaultIfEmpty(registry.getDocumentType().getDescription(), AonStringUtils.repeat(AonStringUtils.QUESTION, 3))
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.defaultIfEmpty(registry.getDocumentCountry().getIso2(), AonStringUtils.repeat(AonStringUtils.QUESTION, 2)) 
				+ AonStringUtils.SLASH
				+ AonStringUtils.defaultIfEmpty(registry.getDocument(), AonStringUtils.repeat(AonStringUtils.QUESTION, 9))
				+ AonStringUtils.SPACE
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.SPACE
				+ registry.getName()
				+ AonStringUtils.SPACE
				+ (AonStringUtils.isNotBlank(registry.getAlias())
					?(AonStringUtils.SPACE + AonStringUtils.OPEN_PARENTHESIS + registry.getAlias() + AonStringUtils.CLOSE_PARENTHESIS)
					:AonStringUtils.EMPTY)
				;
	}
	

}
