package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class CreditorParams implements Serializable{

	private static final long serialVersionUID = 7399522390660289406L;
	
	private int domain;
	private String domainName;
	private String user;
	private SecurityLevel securityLevel; 
	private boolean hasConfidentialityRole;
	
	private Integer id;
	private DocumentType documentType;
	private Country documentCountry;
	private String document;
	private String name;
	private String alias;
	private boolean active;
	private boolean inactive;
	private boolean blocked;

	private int order;
	
	public int getDomain() {
		return domain;
	}
	public CreditorParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public CreditorParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public CreditorParams setUser(String user) {
		this.user = user;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public CreditorParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public CreditorParams setConfidential(Boolean confidential) {
		if (confidential == null) {
			setSecurityLevel( null );	
		} else {
			setSecurityLevel(confidential?SecurityLevel.CONFIDENTIAL:SecurityLevel.OFFICIAL);
		}
		return this;
	}
	
	public boolean hasConfidentialityRole() {
		return hasConfidentialityRole;
	}
	public CreditorParams setHasConfidentialityRole(boolean hasConfidentialityRole) {
		this.hasConfidentialityRole = hasConfidentialityRole;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	public CreditorParams setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public DocumentType getDocumentType() {
		return documentType;
	}
	public CreditorParams setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
		return this;
	}
	
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public CreditorParams setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public CreditorParams setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public CreditorParams setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getAlias() {
		return alias;
	}
	public CreditorParams setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	public CreditorParams setActive(boolean active) {
		this.active = active;
		return this;
	}
	
	public boolean isInactive() {
		return inactive;
	}
	public CreditorParams setInactive(boolean inactive) {
		this.inactive = inactive;
		return this;
	}
	
	public boolean isBlocked() {
		return blocked;
	}
	public CreditorParams setBlocked(boolean blocked) {
		this.blocked = blocked;
		return this;
	}
	
	public int getOrder() {
		return order;
	}
	public CreditorParams setOrder(int order) {
		this.order = order;
		return this;
	}
	
}
