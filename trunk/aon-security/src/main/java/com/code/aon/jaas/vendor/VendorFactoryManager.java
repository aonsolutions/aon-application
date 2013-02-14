package com.code.aon.jaas.vendor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;

/**
 * Application server Factory manager.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-nov-2004
 * @since 1.0
 *  
 */
public class VendorFactoryManager {

	/** Factories list */
	private static final List FACTORIES = new LinkedList();

	/**
	 * Register a new Factory.
	 * 
	 * @param factory
	 */
	public static final void register(IVendorFactory factory) {
		if (!FACTORIES.contains(factory)) {
			FACTORIES.add(factory);
		}
	}

	/**
	 * Get IVendorFactory.
	 * 
	 * @param name
	 * @return
	 */
	public static IVendorFactory getVendorFactory(String name) {
		Iterator iter = FACTORIES.iterator();
		while (iter.hasNext()) {
			IVendorFactory factory = (IVendorFactory) iter.next();
			if (factory.accept(name)) {
				return factory;
			}
		}
		return null;
	}

	/**
	 * Creates a new specific vendor for the given type.
	 * 
	 * @param name
	 * @return
	 */
	public static IVendor create(String name) {
		IVendorFactory factory = getVendorFactory(name);
		return (factory != null) ? factory.create() : null;
	}

	/**
	 * Parses the file passed by parameter for the specific vendor.
	 * 
	 * @param name
	 * @param is
	 * @return
	 * @throws AstException
	 * @throws IOException
	 */
	public static IVendorDescriptor parse(String name, InputStream is)
			throws AstException, IOException {
		IVendorFactory factory = getVendorFactory(name);
		return (factory != null) ? factory.parse(is) : null;
	}

}