package com.code.aon.csb.fd0.model.MOD349.data;

/**
 * The correction
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 * 
 */
public class Correction {

	/**
	 * the country
	 */
	private String country;

	/**
	 * the code
	 */
	private String code;

	/**
	 * the name
	 */
	private String name;

	/**
	 * the key
	 */
	private String key;

	/**
	 * the year
	 */
	private Integer year;

	/**
	 * the period
	 */
	private String period;

	/**
	 * the base quantity
	 */
	private Double base;

	/**
	 * the previous base quantity
	 */
	private Double previousBase;

	/**
	 * @return the base
	 */
	public Double getBase() {
		return base;
	}

	/**
	 * @param base
	 *            the base to set
	 */
	public void setBase(Double base) {
		this.base = base;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the period
	 */
	public String getPeriod() {
		return period;
	}

	/**
	 * @param period
	 *            the period to set
	 */
	public void setPeriod(String period) {
		this.period = period;
	}

	/**
	 * @return the previousBase
	 */
	public Double getPreviousBase() {
		return previousBase;
	}

	/**
	 * @param previousBase
	 *            the previousBase to set
	 */
	public void setPreviousBase(Double previousBase) {
		this.previousBase = previousBase;
	}

	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String description = "CORRECTION ";
		description += "CUONTRY ";
		description += country == null ? "NULL " : "'" + country + "'";
		description += "CODE ";
		description += code == null ? "NULL " : "'" + code + "''";
		description += "NAME ";
		description += name == null ? "NULL " : "'" + name + "'; ";
		return description;
	}

}
