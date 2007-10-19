package com.code.aon.account;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.Registry;

/**
 * The Class AccountInvoiceHeader.
 */
public class AccountInvoiceHeader implements ITransferObject {
	
	/** The type. */
	private InvoiceType type;
	
	/** The registry. */
	private Registry registry;
	
	/** The name. */
	private String name;
	
	/** The document. */
	private String document;
	
	/** The date. */
	private Date date;
	
	/** The period. */
	private Period period;
	
	/** The series. */
	private String series;
	
	/** The number. */
	private int number;
	
	/** The concept. */
	private Account account;
	
	/** The security level. */
	private SecurityLevel securityLevel;

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public InvoiceType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(InvoiceType type) {
		this.type = type;
	}

	/**
	 * Gets the registry.
	 * 
	 * @return the registry
	 */
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry the registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the document.
	 * 
	 * @return the document
	 */
	public String getDocument() {
		return document;
	}

	/**
	 * Sets the document.
	 * 
	 * @param document the document
	 */
	public void setDocument(String document) {
		this.document = document;
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
	 * Gets the account.
	 * 
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * Sets the account.
	 * 
	 * @param account the account
	 */
	public void setAccount(Account account) {
		this.account = account;
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