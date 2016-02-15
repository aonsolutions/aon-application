package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.CreditorStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;

public class Creditor implements Serializable, HasAudit {

	private static final long serialVersionUID = -8387527011020131042L;

	private Integer id;
	private Account account;
	private Registry registry;
	private int scope;
	private int domain;
	private boolean withholding;
	private boolean vatAccrualPayment;
	private InvoiceTransactionType transaction;
	private CreditorStatus status;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	public Integer getId() {
		return id;
	}

	public Creditor setId(Integer id) {
		this.id = id;
		return this;
	}

	public Account getAccount() {
		return account;
	}

	public Creditor setAccount(Account account) {
		this.account = account;
		return this;
	}

	public Registry getRegistry() {
		return registry;
	}

	public Creditor setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}

	public int getScope() {
		return scope;
	}

	public Creditor setScope(int scope) {
		this.scope = scope;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Creditor setDomain(int domain) {
		this.domain = domain;
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

	public CreditorStatus getStatus() {
		return status;
	}

	public Creditor setStatus(CreditorStatus status) {
		this.status = status;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public Creditor setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Creditor setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public Creditor setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public Creditor setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

}
