package com.code.aon.jaas.vendor.tomcat.deployment;

import java.io.IOException;
import java.io.InputStream;

import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.vendor.IVendor;
import com.code.aon.jaas.vendor.IVendorFactory;
import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;
import com.code.aon.jaas.vendor.jboss.deployment.AstLoader;

/**
 * Vendor deployment factory. This class checks if the application is running on Tomcat.  
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-nov-2004
 * @since 1.0
 *  
 */
public class TomcatFactory implements IVendorFactory {

	/** Tomcat vendor instance. */
	Tomcat tomcat;

	/**
	 * Return true if the application is running on Tomcat, false otherwise. 
	 * 
	 * @see com.code.aon.jaas.vendor.IVendorFactory#accept(java.lang.String)
	 */
	public boolean accept(String vendor) {
		return true;
	}

	/**
	 * Create a Tomcat vendor instance.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendorFactory#create()
	 */
	public IVendor create() {
		return (tomcat == null) ? tomcat = new Tomcat() : tomcat;
	}

	/**
	 * Application does never reach this method, because tomcat have not an own descriptor file. 
	 * 
	 * @see com.code.aon.jaas.vendor.IVendorFactory#parse(java.io.InputStream)
	 */
	public IVendorDescriptor parse(InputStream is) throws AstException, IOException {
		return (IVendorDescriptor) new AstLoader().parse(is);
	}

}