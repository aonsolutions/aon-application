package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.IAccountId;
import com.esferalia.aon.occam.api.model.IScopable;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class Creditor extends Registry implements Serializable, HasAudit, IScopable<Creditor>, IAccountId<Creditor> {

	private static final long serialVersionUID = -8387527011020131042L;

	private boolean withholding;
	private boolean vatAccrualPayment;
	private InvoiceTransactionType transaction;
	private RegistryStatus status;
	private Scope scope;
	private Integer account;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	public Creditor() {
		withholding = false;
		vatAccrualPayment = false;
		transaction = InvoiceTransactionType.NATIONAL;
	}
	
	public Creditor copy(Registry registry) {
		return super.copy( registry, this);
	}
	
	public Creditor setId(Integer id) {
		super.setId(id);
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}
	public Creditor setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}

	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Creditor setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}

	public Creditor setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}

	public RegistryStatus getStatus() {
		return status;
	}
	public Creditor setStatus(RegistryStatus status) {
		this.status = status;
		return this;
	}

	@Override
	public Scope getScope() {
		if(scope == null) {
			scope = new Scope();
		}
		return scope;
	}
	@Override
	public Creditor setScope(Scope scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public Integer getAccount() {
		return account;
	}
	@Override
	public Creditor setAccount(Integer account) {
		this.account = account;
		return this;
	}

	@Override
	public String getCreationUser() {
		return creationUser;
	}

	public Creditor setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public Creditor setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	@Override
	public String getModificationUser() {
		return modificationUser;
	}

	public Creditor setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	@Override
	public Date getModificationDate() {
		return modificationDate;
	}

	public Creditor setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

}
