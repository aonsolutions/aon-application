package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.FBatchType;

public class FBatchParams implements Serializable{

	private static final long serialVersionUID = 1011376614663696492L;
	
	private int domain;
	private String domainName;
	
	private String description;
	private Date fromIssueDate;
	private Date toIssueDate;
	private Integer rbank;
	
	private FBatchType fbatchType;   // pantalla de origen (obligatorio)
	private Byte type;               // filtro concreto, null = todos los de fbatchType
	
	private Byte status;
	private Boolean confidential;
	
	public int getDomain() {
		return domain;
	}
	
	public FBatchParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	public FBatchParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public FBatchParams setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getFromIssueDate() {
		return fromIssueDate;
	}

	public FBatchParams setFromIssueDate(Date fromIssueDate) {
		this.fromIssueDate = fromIssueDate;
		return this;
	}

	public Date getToIssueDate() {
		return toIssueDate;
	}

	public FBatchParams setToIssueDate(Date toIssueDate) {
		this.toIssueDate = toIssueDate;
		return this;
	}

	public Integer getRbank() {
		return rbank;
	}

	public FBatchParams setRbank(Integer rbank) {
		this.rbank = rbank;
		return this;
	}
	
	public FBatchType getFbatchType() {
		return fbatchType;
	}
	
	public FBatchParams setFbatchType(FBatchType fbatchType) {
		this.fbatchType = fbatchType;
		return this;
	}

	public Byte getType() {
		return type;
	}

	public FBatchParams setType(Byte type) {
		this.type = type;
		return this;
	}

	public Byte getStatus() {
		return status;
	}

	public FBatchParams setStatus(Byte status) {
		this.status = status;
		return this;
	}

	public Boolean getConfidential() {
		return confidential;
	}

	public FBatchParams setConfidential(Boolean confidential) {
		this.confidential = confidential;
		return this;
	}
	
}
