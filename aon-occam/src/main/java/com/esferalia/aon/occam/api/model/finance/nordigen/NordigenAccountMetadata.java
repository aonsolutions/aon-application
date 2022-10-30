package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Date;

public class NordigenAccountMetadata implements Serializable {
	
	private static final long serialVersionUID = 448347838380309919L;
	
	private String id;
	private Date created;
	private Date lastAccessed;
	private String iban;
	private String institutionId;
	private NORDIGEN_ACCOUNT_STATUS status;
	private String ownerName;
	
	public String getId() {
		return id;
	}
	public NordigenAccountMetadata setId(String id) {
		this.id = id;
		return this;
	}
	public Date getCreated() {
		return created;
	}
	public NordigenAccountMetadata setCreated(Date created) {
		this.created = created;
		return this;
	}
	public Date getLastAccessed() {
		return lastAccessed;
	}
	public NordigenAccountMetadata setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
		return this;
	}
	public String getIban() {
		return iban;
	}
	public NordigenAccountMetadata setIban(String iban) {
		this.iban = iban;
		return this;
	}
	public String getInstitutionId() {
		return institutionId;
	}
	public NordigenAccountMetadata setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
		return this;
	}
	public NORDIGEN_ACCOUNT_STATUS getStatus() {
		return status;
	}
	public NordigenAccountMetadata setStatus(NORDIGEN_ACCOUNT_STATUS status) {
		this.status = status;
		return this;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public NordigenAccountMetadata setOwnerName(String ownerName) {
		this.ownerName = ownerName;
		return this;
	}
	
}
