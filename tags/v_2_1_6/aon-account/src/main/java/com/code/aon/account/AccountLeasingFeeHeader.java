package com.code.aon.account;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.RegistryBank;

/**
 * The Class AccountLeasingFeeHeader.
 */
public class AccountLeasingFeeHeader implements ITransferObject {

	/** The leasing. */
	private Leasing leasing;
	
	/** The leasing date. */
	private Date leasingFeeDate;
	
	/** The series. */
	private String series;
	
	/** The number. */
	private int number;
	
	/** The reference code. */
	private String referenceCode;
	
	/** The amortization. */
	private double amortization;
	
	/** The interest. */
	private double interest;
	
	/** The taxable base. */
	private double taxableBase;
	
	private double expenses;
	
	/** The registry bank. */
	private RegistryBank registryBank;

	/** The security level. */
	private SecurityLevel securityLevel;
	
	
	/**
	 * Gets the leasing.
	 * 
	 * @return the leasing
	 */
	public Leasing getLeasing() {
		return leasing;
	}

	/**
	 * Sets the leasing.
	 * 
	 * @param leasing the leasing
	 */
	public void setLeasing(Leasing leasing) {
		this.leasing = leasing;
	}

	/**
	 * Gets the leasingFee date.
	 * 
	 * @return the leasingFee date
	 */
	public Date getLeasingFeeDate() {
		return leasingFeeDate;
	}

	/**
	 * Sets the leasingFee date.
	 * 
	 * @param leasingFeeDate the leasingFee date
	 */
	public void setLeasingFeeDate(Date leasingFeeDate) {
		this.leasingFeeDate = leasingFeeDate;
	}

	/**
	 * Gets the series.
	 * 
	 * @return the series
	 */
	public String getSeries() {
		return series;
	}

	/**
	 * Sets the series.
	 * 
	 * @param series the series
	 */
	public void setSeries(String series) {
		this.series = series;
	}

	/**
	 * Gets the number.
	 * 
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number the number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Gets the reference code.
	 * 
	 * @return the reference code
	 */
	public String getReferenceCode() {
		return referenceCode;
	}

	/**
	 * Sets the reference code.
	 * 
	 * @param referenceCode the reference code
	 */
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
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
	 * Gets the taxable base.
	 * 
	 * @return the taxable base
	 */
	public double getTaxableBase() {
		return taxableBase;
	}

	/**
	 * Sets the taxable base.
	 * 
	 * @param taxableBase the taxable base
	 */
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}

	public double getExpenses() {
		return expenses;
	}

	public void setExpenses(double expenses) {
		this.expenses = expenses;
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
	 * Gets the security level.
	 * 
	 * @return the security level
	 */
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	/**
	 * Sets the security level.
	 * 
	 * @param securityLevel the security level
	 */
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public double getTotal() {
		 return getVatQuota() + getTaxableBase() + getExpenses();
	}

	public double getVatQuota() {
		return round((getTaxableBase() * getLeasing().getVat().getPercentage()) / 100, 2);
	}
	
    private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
}