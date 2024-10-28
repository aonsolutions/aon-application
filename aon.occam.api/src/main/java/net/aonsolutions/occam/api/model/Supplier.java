package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;

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
	private Account account;
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	
	public Supplier() {
		transaction = InvoiceTransactionType.NATIONAL;
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
	
	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	public Supplier setAccount(Account account) {
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
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Supplier setCreationDate(Timestamp creationDate) {
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
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Supplier setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	@Override
	public Supplier setId(Integer id) {
		super.setId(id);
		return this;
	}
	@Override
	public Supplier setDomain(Integer domain) {
		super.setDomain(domain);
		return this;
	}
	@Override
	public Supplier setDocument(String document) {
		super.setDocument(document);
		return this;
	}
	@Override
	public Supplier setDocumentType(DocumentType documentType) {
		super.setDocumentType(documentType);
		return this;
	}
	@Override
	public Supplier setDocumentCountry(Country documentCountry) {
		super.setDocumentCountry(documentCountry);
		return this;
	}
	@Override
	public Supplier setName(String name) {
		super.setName(name);
		return this;
	}
	@Override
	public Supplier setAlias(String alias) {
		super.setAlias(alias);
		return this;
	}
	@Override
	public Supplier setLegalPerson(boolean legalPerson) {
		super.setLegalPerson(legalPerson);
		return this;
	}
	@Override
	public Supplier setNationality(Country nationality) {
		super.setNationality(nationality);
		return this;
	}
	@Override
	public Supplier setConfidential(boolean confidential) {
		super.setConfidential(confidential);
		return this;
	}
}
