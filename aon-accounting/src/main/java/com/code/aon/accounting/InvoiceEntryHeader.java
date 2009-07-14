package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.Registry;

public class InvoiceEntryHeader implements ITransferObject {
	
	private static final long serialVersionUID = 7455764834130396589L;

	/** The type. */
	private InvoiceType type;

	/** The type. */
	private boolean investment;

	/** The type. */
	private InvoiceTransactionType transaction;

	/** The registry. */
	private Registry registry;
	
	/** The name. */
	private String name;
	
	/** The document. */
	private String document;
	
	/** The date. */
	private Date date;
	
	/** The date. */
	private Date taxDate;

	/** The period. */
	private Period period;
	
	/** The series. */
	private String series;
	
	/** The number. */
	private int number;
	
	/** The reference code. */
	private String referenceCode;
	
    private boolean taxFree;
    private boolean surcharge;
    private boolean withholding;

    /** The concept. */
	private Account account;
	
	/** The security level. */
	private SecurityLevel securityLevel;
	
	private Integer accountEntryId;

	private Double taxableBase;

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

	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
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

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
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

	public boolean isTaxFree() {
		return taxFree;
	}

	public void setTaxFree(boolean taxFree) {
		this.taxFree = taxFree;
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
}