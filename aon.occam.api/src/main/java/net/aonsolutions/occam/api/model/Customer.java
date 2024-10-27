package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;

public class Customer extends Registry implements Serializable, HasAudit {

	private static final long serialVersionUID = -1763053163676341615L;
	
	private Integer tariff;
	private boolean surcharge;
	private boolean withholding;
	private InvoiceTransactionType transaction;
	private RegistryStatus status;
	private Integer scope;
	private boolean eInvoice;
	private Integer invoicingGroup;
	private boolean projectGrouped;
	private boolean deliveryGrouped;
	private boolean deliveryValuated;
	private Account account;
	private Timestamp creationDate;
	private String creationUser;
	private Timestamp modificationDate;
	private String modificationUser;

	public Customer() {
		transaction = InvoiceTransactionType.NATIONAL;
		projectGrouped = true;
		deliveryGrouped = true;
		deliveryValuated = true;
	}
	

	public Integer getTariff() {
		return tariff;
	}
	public Customer setTariff(Integer tariff) {
		this.tariff = tariff;
		return this;
	}

	public boolean isSurcharge() {
		return surcharge;
	}
	public Customer setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}
	public Customer setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public Customer setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}
	
	public RegistryStatus getStatus() {
		return status;
	}
	public Customer setStatus(RegistryStatus status) {
		this.status = status;
		return this;
	}
	
	public Integer getScope() {
		return scope;
	}
	public Customer setScope(Integer scope) {
		this.scope = scope;
		return this;
	}

	public boolean isEInvoice() {
		return eInvoice;
	}
	public Customer setEInvoice(boolean eInvoice) {
		this.eInvoice = eInvoice;
		return this;
	}

	public Integer getInvoicingGroup() {
		return invoicingGroup;
	}
	public Customer setInvoicingGroup(Integer invoicingGroup) {
		this.invoicingGroup = invoicingGroup;
		return this;
	}
	
	public boolean isProjectGrouped() {
		return projectGrouped;
	}
	public Customer setProjectGrouped(boolean projectGrouped) {
		this.projectGrouped = projectGrouped;
		return this;
	}
	
	public boolean isDeliveryGrouped() {
		return deliveryGrouped;
	}
	public Customer setDeliveryGrouped(boolean deliveryGrouped) {
		this.deliveryGrouped = deliveryGrouped;
		return this;
	}
	
	public boolean isDeliveryValuated() {
		return deliveryValuated;
	}
	public Customer setDeliveryValuated(boolean deliveryValuated) {
		this.deliveryValuated = deliveryValuated;
		return this;
	}
	
	
	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	public Customer setAccount(Account account) {
		this.account = account;
		return this;
	}
	
	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Customer setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Customer setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Customer setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Customer setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	@Override
	public Customer setId(Integer id) {
		super.setId(id);
		return this;
	}
	@Override
	public Customer setDomain(Integer domain) {
		super.setDomain(domain);
		return this;
	}
	@Override
	public Customer setDocument(String document) {
		super.setDocument(document);
		return this;
	}
	@Override
	public Customer setDocumentType(DocumentType documentType) {
		super.setDocumentType(documentType);
		return this;
	}
	@Override
	public Customer setDocumentCountry(Country documentCountry) {
		super.setDocumentCountry(documentCountry);
		return this;
	}
	@Override
	public Customer setName(String name) {
		super.setName(name);
		return this;
	}
	@Override
	public Customer setAlias(String alias) {
		super.setAlias(alias);
		return this;
	}
	@Override
	public Customer setLegalPerson(boolean legalPerson) {
		super.setLegalPerson(legalPerson);
		return this;
	}
	@Override
	public Customer setNationality(Country nationality) {
		super.setNationality(nationality);
		return this;
	}
	@Override
	public Customer setConfidential(boolean confidential) {
		super.setConfidential(confidential);
		return this;
	}
}
