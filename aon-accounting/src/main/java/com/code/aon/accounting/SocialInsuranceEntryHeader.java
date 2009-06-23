package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.registry.RegistryBank;

public class SocialInsuranceEntryHeader implements ITransferObject{

	private static final long serialVersionUID = 9083596439112054168L;

	/** The date. */
	private Date date;
	
	/** The period. */
	private Period period;
	
	/** The description. */
	private String description;
	
	/** The amount. */
	private double amount;
	
	/** The registry bank. */
	private RegistryBank rBank;
	
	private SecurityLevel securityLevel;

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
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
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
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the registry bank.
	 * 
	 * @return the registry bank
	 */
	public RegistryBank getRBank() {
		return rBank;
	}

	/**
	 * Sets the registry bank.
	 * 
	 * @param registryBank the registry bank
	 */
	public void setRBank(RegistryBank registryBank) {
		this.rBank = registryBank;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
}