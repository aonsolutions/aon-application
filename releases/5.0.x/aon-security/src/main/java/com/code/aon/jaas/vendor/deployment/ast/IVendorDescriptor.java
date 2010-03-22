package com.code.aon.jaas.vendor.deployment.ast;

import com.code.aon.jaas.deployment.ast.INode;

/**
 * Vendor file descriptor.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-nov-2004
 * @since 1.0
 * @see INode
 *  
 */
public interface IVendorDescriptor extends INode {

	/** 
	 * Indica la cadena base bajo la cual se define el dominio de seguridad de una aplicación que 
	 * es accedida a traves de JNDI.
	 */
	static final String JAAS_JNDI = "java:/jaas/";

	/**
	 * Return application security domain.
	 * 
	 * @return String
	 */
	String getSecurityDomain();

	/**
	 * Return application root context.
	 * 
	 * @return String
	 */
	String getContext();

}