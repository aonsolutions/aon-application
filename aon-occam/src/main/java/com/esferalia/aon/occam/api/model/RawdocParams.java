package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;

public class RawdocParams implements Serializable{
	
	private static final long serialVersionUID = 209481656259438163L;
	
	private int domain;
	private String domainName;
	
	private RawdocNature nature;
	private RawdocType type;
	private RawdocStatus status;

	public int getDomain() {
		return domain;
	}
	public RawdocParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getDomainName() {
		return domainName;
	}
	public RawdocParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public RawdocNature getNature() {
		return nature;
	}
	public RawdocParams setNature(RawdocNature nature) {
		this.nature = nature;
		return this;
	}
	public RawdocType getType() {
		return type;
	}
	public RawdocParams setType(RawdocType type) {
		this.type = type;
		return this;
	}
	public RawdocStatus getStatus() {
		return status;
	}
	public RawdocParams setStatus(RawdocStatus status) {
		this.status = status;
		return this;
	}
	
	
}
