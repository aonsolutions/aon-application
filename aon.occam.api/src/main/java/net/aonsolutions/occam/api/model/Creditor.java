package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;

public class Creditor extends Registry implements Serializable, HasAudit {

	private static final long serialVersionUID = -8387527011020131042L;

	private boolean withholding;
	private boolean vatAccrualPayment;
	private InvoiceTransactionType transaction;
	private RegistryStatus status;
	private Integer scope;
	private Account account;
	
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;

	public Creditor() {
		withholding = false;
		vatAccrualPayment = false;
		transaction = InvoiceTransactionType.NATIONAL;
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

	public Integer getScope() {
		return scope;
	}
	public Creditor setScope(Integer scope) {
		this.scope = scope;
		return this;
	}

	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	public Creditor setAccount(Account account) {
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
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Creditor setCreationDate(Timestamp creationDate) {
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
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Creditor setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	
	@Override
	public Creditor setId(Integer id) {
		super.setId(id);
		return this;
	}
	@Override
	public Creditor setDomain(Integer domain) {
		super.setDomain(domain);
		return this;
	}
	@Override
	public Creditor setDocument(String document) {
		super.setDocument(document);
		return this;
	}
	@Override
	public Creditor setDocumentType(DocumentType documentType) {
		super.setDocumentType(documentType);
		return this;
	}
	@Override
	public Creditor setDocumentCountry(Country documentCountry) {
		super.setDocumentCountry(documentCountry);
		return this;
	}
	@Override
	public Creditor setName(String name) {
		super.setName(name);
		return this;
	}
	@Override
	public Creditor setAlias(String alias) {
		super.setAlias(alias);
		return this;
	}
	@Override
	public Creditor setLegalPerson(boolean legalPerson) {
		super.setLegalPerson(legalPerson);
		return this;
	}
	@Override
	public Creditor setNationality(Country nationality) {
		super.setNationality(nationality);
		return this;
	}
	@Override
	public Creditor setConfidential(boolean confidential) {
		super.setConfidential(confidential);
		return this;
	}
	
}
