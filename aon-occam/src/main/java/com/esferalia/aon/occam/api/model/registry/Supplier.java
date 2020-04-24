package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class Supplier extends Registry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer tariff;
	private Short withholding;
	private Short withholdingFarmer;
	private Short vatAccrualPayment;
	private Short transaction;
	private RegistryStatus status;
	private Integer scope;
	private Short purchaseValuated;
	private Integer account;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Supplier() {
		withholding = (byte) 0;
		withholdingFarmer = (byte) 0;
		vatAccrualPayment = (byte) 0;
		transaction = (byte) 0;
		purchaseValuated = (byte) 0;
	}
	
	public Integer getScope() {
		return scope;
	}

	public Supplier setScope(Integer scope) {
		this.scope = scope;
		return this;
	}

	public Integer getTariff() {
		return tariff;
	}

	public Supplier setTariff(Integer tariff) {
		this.tariff = tariff;
		return this;
	}

	public Short getWithholding() {
		return withholding;
	}

	public Supplier setWithholding(Short withholding) {
		this.withholding = withholding;
		return this;
	}

	public Short getWithholdingFarmer() {
		return withholdingFarmer;
	}

	public Supplier setWithholdingFarmer(Short withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}

	public Short getVatAccrualPayment() {
		return vatAccrualPayment;
	}

	public Supplier setVatAccrualPayment(Short vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}

	public Short getTransaction() {
		return transaction;
	}

	public Supplier setTransaction(Short transaction) {
		this.transaction = transaction;
		return this;
	}

	public RegistryStatus getStatus() {
		return status;
	}

	public Supplier setStatus(RegistryStatus status) {
		this.status = status;
		return this;
	}

	public Short getPurchaseValuated() {
		return purchaseValuated;
	}

	public Supplier setPurchaseValuated(Short purchaseValuated) {
		this.purchaseValuated = purchaseValuated;
		return this;
	}

	public Integer getAccount() {
		return account;
	}

	public Supplier setAccount(Integer account) {
		this.account = account;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public Supplier setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Supplier setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public Supplier setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public Supplier setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	
}
