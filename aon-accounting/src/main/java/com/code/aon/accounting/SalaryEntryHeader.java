package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.registry.RegistryBank;

public class SalaryEntryHeader implements ITransferObject {
	
	private static final long serialVersionUID = -3872966060253678894L;

	/** The period. */
	private Period period;

	/** The date. */
	private Date date;
	
	/** The registry bank. */
	private RegistryBank registryBank;
	
	/** The concept. */
	private String concept;
	
	/** The security level. */
	private SecurityLevel securityLevel;

	/** The gross salary. */
	private double grossSalary;
	
	/** The retention. */
	private double retention;
	
	/** The employee social insurance. */
	private double employeeSocialInsurance;
	
	/** The company social insurance. */
	private double companySocialInsurance;
	
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
	 * Gets the employee social insurance.
	 * 
	 * @return the employee social insurance
	 */
	public double getEmployeeSocialInsurance() {
		return employeeSocialInsurance;
	}

	/**
	 * Sets the employee social insurance.
	 * 
	 * @param employeeSocialInsurance the employee social insurance
	 */
	public void setEmployeeSocialInsurance(double employeeSocialIsurance) {
		this.employeeSocialInsurance = employeeSocialIsurance;
	}

	/**
	 * Gets the company social insurance.
	 * 
	 * @return the company social insurance
	 */
	public double getCompanySocialInsurance() {
		return companySocialInsurance;
	}

	/**
	 * Sets the company social insurance.
	 * 
	 * @param companySocialInsurance the company social insurance
	 */
	public void setCompanySocialInsurance(double companySocialIsurance) {
		this.companySocialInsurance = companySocialIsurance;
	}

	/**
	 * Gets the net salary.
	 * 
	 * @return the net salary
	 */
	public double getNetSalary(){
		return CommonUtil.round(grossSalary - retention - employeeSocialInsurance);
	}

	/**
	 * Gets the total social insurance.
	 * 
	 * @return the total social insurance
	 */
	public double getTotalSocialInsurance(){
		return CommonUtil.round(employeeSocialInsurance + companySocialInsurance);
	}

}