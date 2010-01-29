package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.registry.RegistryBank;

public class SocialInsuranceEntryHeader implements ITransferObject{

	private static final long serialVersionUID = 9083596439112054168L;

	/** The period. */
	private Period period;

	/** The date. */
	private Date date;
	
	/** The amount. */
	private double amount;
	
	/** The registry bank. */
	private RegistryBank registryBank;
	
	/** The concept. */
	private String concept;
	
	/** The security level. */
	private SecurityLevel securityLevel;

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
	 * Gets the concept.
	 * 
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}

	/**
	 * Sets the concept.
	 * 
	 * @param concept the concept
	 */
	public void setConcept(String concept) {
		this.concept = concept;
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

}