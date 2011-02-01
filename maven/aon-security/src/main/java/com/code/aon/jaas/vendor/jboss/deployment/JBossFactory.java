package com.code.aon.jaas.vendor.jboss.deployment;

import java.io.IOException;
import java.io.InputStream;

import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.vendor.IVendor;
import com.code.aon.jaas.vendor.IVendorFactory;
import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;

/**
 * Check where the application is running on, and creates a <code>JBoss</code> vendor instance.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-nov-2004
 * @since 1.0
 *  
 */
public class JBossFactory implements IVendorFactory {

	/** JBoss vendor instance. */
	JBoss jboss;

	/**
	 * Return true if the application is running on JBoss, false otherwise. 
	 * 
	 * @see com.code.aon.jaas.vendor.IVendorFactory#accept(java.lang.String)
	 */
	public boolean accept(String vendor) {
		return true;
	}

	/**
	 * Create a JBoss vendor instance.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendorFactory#create()
	 */
	public IVendor create() {
		return (jboss == null) ? jboss = new JBoss() : jboss;
	}

	/**
	 * Parse jboss-web.xml vendor descriptor file.
	 * 
	 * @see com.code.aon.jaas.vendor.IVendorFactory#parse(java.io.InputStream)
	 */
	public IVendorDescriptor parse(InputStream is) throws AstException, IOException {
		return (IVendorDescriptor) new AstLoader().parse(is);
	}

}