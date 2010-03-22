package com.code.aon.marketplace.tickets;

import java.util.Date;

/**
 * Class that represents a Trace.
 */
public class Trace {

	/** The crotal. */
	private String crotal;
	
	/** The born place. */
	private String born;
	
	/** The raised place. */
	private String raised;
	
	/** The sacrificed place. */
	private String sacrificed;
	
	/** The quartered place. */
	private String quartered;
	
	/** The start date. */
	private Date startDate;
	
	/** The end date. */
	private Date endDate;

	/** The name. */
	private String name;
	
	/** The expiry date. */
	private Date expiry;

	/**
	 * Gets the born place.
	 * 
	 * @return the born place
	 */
	public String getBorn() {
		return born;
	}

	/**
	 * Sets the born place.
	 * 
	 * @param born the born place
	 */
	public void setBorn(String born) {
		this.born = born;
	}

	/**
	 * Gets the crotal.
	 * 
	 * @return the crotal
	 */
	public String getCrotal() {
		return crotal;
	}

	/**
	 * Sets the crotal.
	 * 
	 * @param crotal the crotal
	 */
	public void setCrotal(String crotal) {
		this.crotal = crotal;
	}

	/**
	 * Gets the quartered place.
	 * 
	 * @return the quartered place
	 */
	public String getQuartered() {
		return quartered;
	}

	/**
	 * Sets the quartered place.
	 * 
	 * @param quartered the quartered place
	 */
	public void setQuartered(String quartered) {
		this.quartered = quartered;
	}

	/**
	 * Gets the raised place.
	 * 
	 * @return the raised place
	 */
	public String getRaised() {
		return raised;
	}

	/**
	 * Sets the raised place.
	 * 
	 * @param raised the raised place
	 */
	public void setRaised(String raised) {
		this.raised = raised;
	}

	/**
	 * Gets the sacrificed place.
	 * 
	 * @return the sacrificed place
	 */
	public String getSacrificed() {
		return sacrificed;
	}

	/**
	 * Sets the sacrificed place.
	 * 
	 * @param sacrificed the sacrificed place
	 */
	public void setSacrificed(String sacrificed) {
		this.sacrificed = sacrificed;
	}

	/**
	 * Gets the start date.
	 * 
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date.
	 * 
	 * @param startDate the start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the expiry
	 */
	public Date getExpiry() {
		return expiry;
	}

	/**
	 * @param expiry the expiry to set
	 */
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
}