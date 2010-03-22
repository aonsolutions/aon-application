package com.code.aon.jaas.vendor;

/**
 * Additional information about vendor specifications.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-nov-2004
 * @since 1.0
 *  
 */
public interface IVendor {

	/**
	 * Vendor own WEB file name.
	 * 
	 * @return String
	 */
	String getVendorWEBFile();

	/**
	 * Vendor own EJB file name.
	 * 
	 * @return String
	 */
	String getVendorEJBFile();

	/**
	 * Vendor configuration directory that security would use as base directory. 
	 * 
	 * @return String
	 */
	String getSecurityPath();

	/**
	 * Vendor home base directory. 
	 * 
	 * @return String
	 */
	String getServerHome();

}