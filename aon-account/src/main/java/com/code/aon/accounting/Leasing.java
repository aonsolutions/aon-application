package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.RegistryBank;
import com.code.aon.config.Tax;

/**
 * The Class Leasing.
 */
@Entity
@Table(name="leasing")
public class Leasing implements ITransferObject {

	private static final long serialVersionUID = -7130430601713491089L;

	/** The id. */
	private Integer id;
	
	/** The leasing date. */
	private Date leasingDate;
	
	/** The supplier name. */
	private String supplierName;
	
	/** The supplier document. */
	private String supplierDocument;
	
	/** The description. */
	private String description;
	
	/** The term. */
	private String term;
	
	/** The interest percent. */
	private String interestPercent;
	
	/** The review. */
	private String review;
	
	/** The amount. */
	private double amount;
	
	/** The registry bank. */
	private RegistryBank registryBank;
	
	private Account fixedAssetAccount;
	
	private Tax vat;

	private SecurityLevel securityLevel;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@Column(nullable=false)
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the leasing date.
	 * 
	 * @return the leasing date
	 */
	@Column(name="leasing_date", nullable=false)
	public Date getLeasingDate() {
		return leasingDate;
	}

	/**
	 * Sets the leasing date.
	 * 
	 * @param leasingDate the leasing date
	 */
	public void setLeasingDate(Date leasingDate) {
		this.leasingDate = leasingDate;
	}

	/**
	 * Gets the supplier name.
	 * 
	 * @return the supplier name
	 */
	@Column(name="supplier_name", nullable=false, length=32)
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * Sets the supplier name.
	 * 
	 * @param supplierName the supplier name
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * Gets the supplier document.
	 * 
	 * @return the supplier document
	 */
	@Column(name="supplier_document", nullable=false, length=16)
	public String getSupplierDocument() {
		return supplierDocument;
	}

	/**
	 * Sets the supplier document.
	 * 
	 * @param supplierDocument the supplier document
	 */
	public void setSupplierDocument(String supplierDocument) {
		this.supplierDocument = supplierDocument;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(length=64)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the term.
	 * 
	 * @return the term
	 */
	@Column(length=16)
	public String getTerm() {
		return term;
	}

	/**
	 * Sets the term.
	 * 
	 * @param term the term
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * Gets the interest percent.
	 * 
	 * @return the interest percent
	 */
	@Column(name="interest_percent",length=16)
	public String getInterestPercent() {
		return interestPercent;
	}

	/**
	 * Sets the interest percent.
	 * 
	 * @param interestPercent the interest percent
	 */
	public void setInterestPercent(String interestPercent) {
		this.interestPercent = interestPercent;
	}

	/**
	 * Gets the review.
	 * 
	 * @return the review
	 */
	@Column(length=16)
	public String getReview() {
		return review;
	}

	/**
	 * Sets the review.
	 * 
	 * @param review the review
	 */
	public void setReview(String review) {
		this.review = review;
	}

	/**
	 * Gets the amount.
	 * 
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Sets the amount.
	 * 
	 * @param amount the amount
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * Gets the registry bank.
	 * 
	 * @return the registry bank
	 */
    @ManyToOne
    @JoinColumn(name="rbank", nullable=false)
	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	/**
	 * Sets the registry bank.
	 * 
	 * @param registryBank the registry bank
	 */
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
	@ManyToOne
	@JoinColumn(name="fixed_asset_account", nullable=false)
	public Account getFixedAssetAccount() {
		return fixedAssetAccount;
	}

	public void setFixedAssetAccount(Account fixedAssetAccount) {
		this.fixedAssetAccount = fixedAssetAccount;
	}
	
	@ManyToOne
    @JoinColumn(name="vat", nullable=false)
	public Tax getVat() {
		return vat;
	}

	public void setVat(Tax vat) {
		this.vat = vat;
	}

	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
}