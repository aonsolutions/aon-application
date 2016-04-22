package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

public class AccountingInvoice implements Serializable {
	
	private static final long serialVersionUID = -4435280253306756102L;
	
	private Integer id;
	private int domain;
	
	private InvoiceType invoiceType;
	private Integer registry;
	private Integer registryAccountId;
	private String registryAccountCode;
	private String registryAccountDescription;
	private String registryDocument;
	private Country registryDocumentCountry;
	private DocumentType registryDocumentType;
	private String registryName;
	private int scope;
	private boolean surcharge;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;
	private InvoiceTransactionType transaction;


	
	public Integer getId() {
		return id;
	}

	public AccountingInvoice setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public AccountingInvoice setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public AccountingInvoice setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}

	public AccountingInvoice setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Integer getRegistryAccountId() {
		return registryAccountId;
	}

	public AccountingInvoice setRegistryAccountId(Integer registryAccountId) {
		this.registryAccountId = registryAccountId;
		return this;
	}

	public String getRegistryAccountCode() {
		return registryAccountCode;
	}

	public AccountingInvoice setRegistryAccountCode(String registryAccountCode) {
		this.registryAccountCode = registryAccountCode;
		return this;
	}

	public String getRegistryAccountDescription() {
		return registryAccountDescription;
	}

	public AccountingInvoice setRegistryAccountDescription(String registryAccountDescription) {
		this.registryAccountDescription = registryAccountDescription;
		return this;
	}

	public String getRegistryDocument() {
		return registryDocument;
	}

	public AccountingInvoice setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}

	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}

	public AccountingInvoice setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}

	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}

	public AccountingInvoice setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}

	public String getRegistryName() {
		return registryName;
	}

	public AccountingInvoice setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}

	public int getScope() {
		return scope;
	}

	public AccountingInvoice setScope(int scope) {
		this.scope = scope;
		return this;
	}

	public boolean isSurcharge() {
		return surcharge;
	}

	public AccountingInvoice setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}

	public AccountingInvoice setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}

	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}

	public AccountingInvoice setWithholdingFarmer(boolean withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}

	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}

	public AccountingInvoice setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}

	public AccountingInvoice setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}

	public AccountingInvoice setRegistry(AccountingRegistry registry) {
		if (registry != null) {
			this.setInvoiceType(registry.getType().getInvoiceType())
				.setRegistry(registry.getId())
				.setRegistryAccountId(registry.getAccountId())
				.setRegistryAccountCode(registry.getAccountCode())
				.setRegistryAccountDescription(registry.getAccountDescription())
				.setRegistryDocument(registry.getDocument())
				.setRegistryDocumentCountry(registry.getDocumentCountry())
				.setRegistryDocumentType(registry.getDocumentType())
				.setRegistryName(registry.getName())
				.setScope(registry.getScope())
				.setSurcharge(registry.isSurcharge())
				.setWithholding(registry.isWithholding())
				.setWithholdingFarmer(registry.isWithholdingFarmer())
				.setVatAccrualPayment(registry.isVatAccrualPayment())
				.setTransaction(registry.getTransaction())
			;
		}
		return this;
	}

}
