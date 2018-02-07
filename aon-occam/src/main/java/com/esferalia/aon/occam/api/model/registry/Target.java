package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.TargetStatus;

public class Target extends Registry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer tariff;
	private Short advertising;
	private Short surcharge;
	private Short withholding;
	private Short transaction;
	private TargetStatus status;
	private Integer scope;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	public Integer getTariff() {
		return tariff;
	}
	public Target setTariff(Integer tariff) {
		this.tariff = tariff;
		return this;
	}
	public Short getAdvertising() {
		return advertising;
	}
	public Target setAdvertising(Short advertising) {
		this.advertising = advertising;
		return this;
	}
	public Short getSurcharge() {
		return surcharge;
	}
	public Target setSurcharge(Short surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	public Short getWithholding() {
		return withholding;
	}
	public Target setWithholding(Short withholding) {
		this.withholding = withholding;
		return this;
	}
	public Short getTransaction() {
		return transaction;
	}
	public Target setTransaction(Short transaction) {
		this.transaction = transaction;
		return this;
	}
	public TargetStatus getStatus() {
		return status;
	}
	public Target setStatus(TargetStatus status) {
		this.status = status;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public Target setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public Target setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public Target setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public Target setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public Target setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
}
