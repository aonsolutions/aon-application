package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.account.Account;
import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.registry.enumeration.DocumentType;

public class InvoiceEntryHeader implements ITransferObject {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private InvoiceType type;
	private boolean investment;
	private InvoiceTransactionType transaction;
	private Registry registry;
	private String name;
	private Country documentCountry;
	private DocumentType documentType;
	private String document;
	private String concept;
	private Date date;
	private Date taxDate;
	private Period period;
	private String series;
	private int number;
	private String referenceCode;
    private boolean surcharge;
    private boolean withholding;
    private boolean vatAccrualPayment;
	private Account account;
	private Account retentionAccount;
	private VatDeductionType vatDeductionType;
	private WithholdingType withholdingType;
	private SecurityLevel securityLevel;
   
	private Integer accountEntryId;

	private Double taxableBase;
	private Double taxPercent;
	private Double surchargePercent;
	private Double retPercent;
	
	public InvoiceType getType() {
		return type;
	}
	public void setType(InvoiceType type) {
		this.type = type;
	}

	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Country getDocumentCountry() {
		return documentCountry;
	}
	public void setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public boolean isValidRegistryDocument() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getDocument());
		registryDocument.setType(getDocumentType());
		registryDocument.setCountry(getDocumentCountry());
		return registryDocument.isValid();
	}
	public boolean isRegistryDocumentValidable() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getDocument());
		registryDocument.setType(getDocumentType());
		registryDocument.setCountry(getDocumentCountry());
		return registryDocument.isValidable();
	}

	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Date getTaxDate() {
		return taxDate;
	}
	public void setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
	}

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}

	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	public Account getRetentionAccount() {
		return retentionAccount;
	}
	public void setRetentionAccount(Account retentionAccount) {
		this.retentionAccount = retentionAccount;
	}

	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public void setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
	}

	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public void setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public boolean isConfidential() {
		return getSecurityLevel() == SecurityLevel.CONFIDENTIAL;
	}
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential?SecurityLevel.CONFIDENTIAL:SecurityLevel.OFFICIAL );
	}

	public boolean isInvestment() {
		return investment;
	}
	public void setInvestment(boolean investment) {
		this.investment = investment;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}

	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}

	public Integer getAccountEntryId() {
		return accountEntryId;
	}
	public void setAccountEntryId(Integer accountEntryId) {
		this.accountEntryId = accountEntryId;
	}

	public Double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(Double taxableBase) {
		this.taxableBase = taxableBase;
	}

	public Double getTaxPercent() {
		return taxPercent;
	}
	public void setTaxPercent(Double taxPercent) {
		this.taxPercent = taxPercent;
	}

	public Double getSurchargePercent() {
		return surchargePercent;
	}
	public void setSurchargePercent(Double surchargePercent) {
		this.surchargePercent = surchargePercent;
	}

	public Double getRetPercent() {
		return retPercent;
	}
	public void setRetPercent(Double retPercent) {
		this.retPercent = retPercent;
	}

	public boolean isSurcharge() {
		return surcharge;
	}

	public void setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
	}

	public boolean isWithholding() {
		return withholding;
	}

	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public void setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
	}
	
}