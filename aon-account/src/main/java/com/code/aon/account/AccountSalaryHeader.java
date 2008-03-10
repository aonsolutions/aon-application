package com.code.aon.account;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.RegistryBank;

/**
 * The Class AccountSalaryHeader.
 */
public class AccountSalaryHeader implements ITransferObject {
	
	/** The date. */
	private Date date;
	
	/** The period. */
	private Period period;
	
	/** The description. */
	private String description;
	
	/** The gross salary. */
	private double grossSalary;
	
	/** The retention. */
	private double retention;
	
	/** The social isurance. */
	private double socialInsurance;
	
	/** The r bank. */
	private RegistryBank rBank;
	
	private SecurityLevel securityLevel;

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 * 
	 * @param date the date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the period.
	 * 
	 * @return the period
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Sets the period.
	 * 
	 * @param period the period
	 */
	public void setPeriod(Period period) {
		this.period = period;
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
	 * Gets the gross salary.
	 * 
	 * @return the gross salary
	 */
	public double getGrossSalary() {
		return grossSalary;
	}

	/**
	 * Sets the gross salary.
	 * 
	 * @param grossSalary the gross salary
	 */
	public void setGrossSalary(double grossSalary) {
		this.grossSalary = grossSalary;
	}

	/**
	 * Gets the retention.
	 * 
	 * @return the retention
	 */
	public double getRetention() {
		return retention;
	}

	/**
	 * Sets the retention.
	 * 
	 * @param retention the retention
	 */
	public void setRetention(double retention) {
		this.retention = retention;
	}

	/**
	 * Gets the social isurance.
	 * 
	 * @return the social isurance
	 */
	public double getSocialInsurance() {
		return socialInsurance;
	}

	/**
	 * Sets the social isurance.
	 * 
	 * @param socialIsurance the social isurance
	 */
	public void setSocialInsurance(double socialIsurance) {
		this.socialInsurance = socialIsurance;
	}

	/**
	 * Gets the Registry bank.
	 * 
	 * @return the Registry bank
	 */
	public RegistryBank getRBank() {
		return rBank;
	}

	/**
	 * Sets the Registry bank.
	 * 
	 * @param rBank the Registry bank
	 */
	public void setRBank(RegistryBank rBank) {
		this.rBank = rBank;
	}
	
	/**
	 * Gets the net salary.
	 * 
	 * @return the net salary
	 */
	public double getNetSalary(){
		return grossSalary - (retention + socialInsurance);
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
}