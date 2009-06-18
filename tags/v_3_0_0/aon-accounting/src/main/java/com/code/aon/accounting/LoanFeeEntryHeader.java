package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.RegistryBank;

public class LoanFeeEntryHeader implements ITransferObject {

	private static final long serialVersionUID = -8273893100974866857L;

	/** The fee date. */
	private Date feeDate;
	
	/** The loan. */
	private Loan loan;
	
	/** The description. */
	private String description;
	
	/** The amortization. */
	private double amortization;
	
	/** The interest. */
	private double interest;
	
	/** The registry bank. */
	private RegistryBank registryBank;
	
	private SecurityLevel securityLevel;


	/**
	 * Gets the fee date.
	 * 
	 * @return the fee date
	 */
	public Date getFeeDate() {
		return feeDate;
	}

	/**
	 * Sets the fee date.
	 * 
	 * @param feeDate the fee date
	 */
	public void setFeeDate(Date feeDate) {
		this.feeDate = feeDate;
	}

	/**
	 * Gets the loan.
	 * 
	 * @return the loan
	 */
	public Loan getLoan() {
		return loan;
	}

	/**
	 * Sets the loan.
	 * 
	 * @param loan the loan
	 */
	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
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
	 * Gets the interest.
	 * 
	 * @return the interest
	 */
	public double getInterest() {
		return interest;
	}

	/**
	 * Sets the interest.
	 * 
	 * @param interest the interest
	 */
	public void setInterest(double interest) {
		this.interest = interest;
	}

	/**
	 * Gets the amortization.
	 * 
	 * @return the amortization
	 */
	public double getAmortization() {
		return amortization;
	}

	/**
	 * Sets the amortization.
	 * 
	 * @param amortization the amortization
	 */
	public void setAmortization(double amortization) {
		this.amortization = amortization;
	}

	/**
	 * Gets the registry bank.
	 * 
	 * @return the registry bank
	 */
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
	
	/**
	 * Gets the fee.
	 * 
	 * @return the fee
	 */
	public double getFee(){
		return getAmortization() + getInterest();
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	
}