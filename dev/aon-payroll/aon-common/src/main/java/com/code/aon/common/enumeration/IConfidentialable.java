package com.code.aon.common.enumeration;

/**
 * Interface for all the pojos that contains securityLevel
 * 
 * @author Consulting & Development.
 * 
 */
public interface IConfidentialable {

	/**
	 * Return true if is confidential and false if not
	 * 
	 * @return boolean
	 */
	public boolean isConfidential();

	/**
	 * Sets confidential value
	 * 
	 * @param confidential
	 */
	public void setConfidential(boolean confidential);
	
}
