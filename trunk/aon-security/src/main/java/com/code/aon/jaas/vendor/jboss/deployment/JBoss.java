package com.code.aon.jaas.vendor.jboss.deployment;

import com.code.aon.jaas.vendor.IVendor;

/**
 * JBoss application server additional information.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-nov-2004
 * @since 1.0
 *  
 */
public class JBoss implements IVendor {

	/** 
	 * jboss-web.xml WEB file descriptor.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendor#getVendorWEBFile()
	 */
	public String getVendorWEBFile() {
		return "jboss-web.xml";
	}

	/** 
	 * jboss.xml EJB file descriptor.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendor#getVendorEJBFile()
	 */
	public String getVendorEJBFile() {
		return "jboss.xml";
	}

	/** 
	 * JBoss configuration directory.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendor#getSecurityPath()
	 */
	public String getSecurityPath() {
		return getServerHome() + "/conf/";
	}

	/** 
	 * JBoss home directory.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendor#getServerHome()
	 */
	public String getServerHome() {
		return System.getProperty( "jboss.server.home.dir" );
	}

}
