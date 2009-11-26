package com.code.aon.jaas.vendor;

import java.io.IOException;
import java.io.InputStream;

import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;

/**
 * Check where the application is running on, and creates an specific vendor instance.  
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-nov-2004
 * @since 1.0
 *  
 */
public interface IVendorFactory {

	/**
	 * Check where the application is running on.
	 * 
	 * @param vendor
	 * @return boolean
	 */
	boolean accept(String vendor);

	/**
	 * Create a vendor instance.
	 * 
	 * @return IVendor
	 */
	IVendor create();

	/**
	 * Parse vendor descriptor file.
	 * 
	 * @param is
	 * @return IVendorDescriptor
	 * @throws AstException
	 * @throws IOException
	 */
	IVendorDescriptor parse(InputStream is) throws AstException, IOException;

}