package com.code.aon.jaas.vendor.jboss.deployment;

import com.code.aon.jaas.deployment.ast.INode;
import com.code.aon.jaas.deployment.ast.INodeVisitor;
import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;

/**
 * JBoss application server WEB deployment descriptor, "jboss-web.xml".
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 04-nov-2004
 * @since 1.0
 * @see INode
 * @see IVendorDescriptor
 *  
 */
public class JBossDescriptor implements IVendorDescriptor {

	/** Security domain. */
	String securityDomain;

	/** Context root. */
	String context;

	/**
	 * Assign security domain.
	 * 
	 * @param securityDomain
	 */
	public void setSecurityDomain(String securityDomain) {
		int index = securityDomain.lastIndexOf(JAAS_JNDI) + JAAS_JNDI.length();
		this.securityDomain = securityDomain.substring(index, securityDomain.length());
	}

	/**
	 * Assign context root.
	 * 
	 * @param context
	 */
	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * Return security domain. 
	 * 
	 * @see com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor#getDomain()
	 */
	public String getSecurityDomain() {
		return securityDomain;
	}

	/**
	 * Return context root.
	 * 
	 * @see com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor#getContext()
	 */
	public String getContext() {
		return context;
	}

	/**
	 * JBoss WEB descriptor visitor pattern.
	 * 
	 * @see com.code.aon.jaas.deployment.ast.INode#accept(com.code.aon.jaas.deployment.ast.INodeVisitor)
	 */
	public void accept(INodeVisitor visitor) {
		visitor.visitVendorDescriptor(this);
	}

}