package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class Supplier extends Registry implements Serializable, HasAudit {
	
	private static final long serialVersionUID = -6627852535384896368L;
	
	private Integer tariff;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;
	private InvoiceTransactionType transaction;
	private RegistryStatus status;
	private Integer scope;
	private boolean purchaseValuated;
	private Integer account;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Supplier() {
		transaction = InvoiceTransactionType.NATIONAL;
	}
	
	public Supplier copy(Registry registry) {
		return super.copy( registry, this);
	}

	public Integer getTariff() {
		return tariff;
	}
	public Supplier setTariff(Integer tariff) {
		this.tariff = tariff;
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}
	public Supplier setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}

	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public Supplier setWithholdingFarmer(boolean withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}

	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Supplier setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public Supplier setTransaction(InvoiceTransactionType transaction) {
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

	public Integer getScope() {
		return scope;
	}
	public Supplier setScope(Integer scope) {
		this.scope = scope;
		return this;
	}

	public boolean isPurchaseValuated() {
		return purchaseValuated;
	}
	public Supplier setPurchaseValuated(boolean purchaseValuated) {
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

	@Override
	public String getCreationUser() {
		return creationUser;
	}

	public Supplier setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public Supplier setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	@Override
	public String getModificationUser() {
		return modificationUser;
	}

	public Supplier setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	@Override
	public Date getModificationDate() {
		return modificationDate;
	}

	public Supplier setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

}
