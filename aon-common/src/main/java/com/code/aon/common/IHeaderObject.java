package com.code.aon.common;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.SecurityLevel;

/**
 * Base interface that constructs a Header Object.  
 * 
 * @author Consulting & Development. Joseba Urkiri - 25-sep-2006
 * @since 1.0
 *  
 */
public interface IHeaderObject {

	/**
	 * Return the series.
	 * 
	 * @return The series.
	 */
	public String getSeries();

	/**
	 * Assign the series.
	 * 
	 * @param series
	 */
	public void setSeries(String series);

	/**
	 * Return the number.
	 * 
	 * @return The number.
	 */
	public int getNumber();

	/**
	 * Assign the number.
	 * 
	 * @param number
	 */
	public void setNumber(int number);

	/**
	 * Return the security level.
	 * 
	 * @return The security level.
	 */
	public SecurityLevel getSecurityLevel();
	
    /**
     * Gets the reference code.
     * 
     * @return the reference code
     */
    public String getReferenceCode();

}
