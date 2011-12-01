package com.code.aon.jaas.vendor.jboss.deployment;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.SAXException;

import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.deployment.ast.INode;
import com.code.aon.jaas.vendor.deployment.ast.IAstLoader;

/**
 * JBoss application server WEB descriptor loader.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 07-feb-2004
 * @since 1.0
 *  
 */
public class AstLoader implements IAstLoader {

	/** Digester rules name. */
	private static final String RULES = "vendor_digester.xml";

	/** Digester instance. */
	private static Digester DIGESTER;

	/**
	 * Return a single instance of Digester class. 
	 * 
	 * @return Digester
	 */
	private static final Digester getDigester() {
		if (DIGESTER == null) {
			DIGESTER = DigesterLoader.createDigester( AstLoader.class.getResource(RULES) );
		}
		return DIGESTER;
	}

	/**
	 * Parse JBoss WEB file descriptor.
	 * 
	 * @see com.code.aon.jaas.vendor.deployment.ast.IAstLoader#parse(java.io.InputStream)
	 */
	public INode parse(InputStream in) throws AstException, IOException {
		try {
			return (INode) getDigester().parse(in);
		} catch (SAXException e) {
			throw new AstException(e);
		}
	}

}