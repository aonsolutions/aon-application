package com.code.aon.jaas.vendor.tomcat.deployment;

import com.code.aon.jaas.vendor.IVendor;

/**
 * Tomcat servlet container additional information.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-nov-2004
 * @since 1.0
 *  
 */
public class Tomcat implements IVendor {

	/** 
	 * Tomcat have not a web file directory, it uses web.xml.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendor#getVendorWEBFile()
	 */
	public String getVendorWEBFile() {
		return null;
	}

	/** 
	 * Tomcat is not an EJB container.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendor#getVendorEJBFile()
	 */
	public String getVendorEJBFile() {
		return null;
	}

	/** 
	 * Tomcat configuration directory.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendor#getSecurityPath()
	 */
	public String getSecurityPath() {
		return getServerHome() + "/conf/";
	}

	/** 
	 * Tomcat home directory.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendor#getServerHome()
	 */
	public String getServerHome() {
		return System.getProperty( "catalina.home" );
	}

}
