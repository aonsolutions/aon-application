package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Registry implements Serializable{
	String alias;
	String document;
	String documentCountry;
	Byte documentType;
	Integer domain;
	Integer id;
	String name;
	String nationality;
	Byte securityLevel;
	Byte type;
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
	public String getDocumentCountry() {
		return documentCountry;
	}
	public Registry setDocumentCountry(String documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}
	public Byte getDocumentType() {
		return documentType;
	}
	public Registry setDocumentType(Byte documentType) {
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
	public String getNationality() {
		return nationality;
	}
	public Registry setNationality(String nationality) {
		this.nationality = nationality;
		return this;
	}
	public Byte getSecurityLevel() {
		return securityLevel;
	}
	public Registry setSecurityLevel(Byte securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public Byte getType() {
		return type;
	}
	public Registry setType(Byte type) {
		this.type = type;
		return this;
	}
	
	
}
