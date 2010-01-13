package com.code.aon.jaas.client.ast.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;

import org.xml.sax.SAXException;

import com.code.aon.jaas.client.ast.IAstLoader;

import com.code.aon.jaas.deployment.ast.AstException;

import com.code.aon.jaas.storage.IStorage;

/**
 * Application, Domain and Option entities loader. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 07-feb-2004
 * @since 1.0
 *  
 */
public class AstLoader implements IAstLoader {

	/** Deployed Application entity grammar file. */
	private static final String RULES_0 = "applications_digester.xml";
	/** Deployed Domain entity grammar file. */
	private static final String RULES_1 = "domain_digester.xml";
	/** Deployed Option entity. */
	private static final String RULES_2 = "options_digester.xml";
	/** XML parse types. */
	private static String[] RULES = { RULES_0, RULES_1, RULES_2 };
	/** XML parser. */
	private static Digester[] DIGESTERS = new Digester[3];
	/** Class Singleton instance. */
	private static final AstLoader LOADER = new AstLoader();

	/**
	 * Returns singleton instance.
	 * 
	 * @return
	 */
	public static final AstLoader getInstance() {
		return LOADER;
	}

	/**
	 * Devuelve el Parseador.
	 * 
	 * @return Digester
	 */
	private static final Digester getDigester(int type) {
		if (DIGESTERS[type] == null) {
			DIGESTERS[type] = DigesterLoader.createDigester( Role.class.getResource( RULES[type] ) );
		}
		return DIGESTERS[type];
	}

	/**
	 * Class Singleton constructor.
	 */
	private AstLoader() {
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IAstLoader#parse(int, java.io.InputStream)
	 */
	public synchronized IStorage parse(int type, InputStream in) throws AstException, IOException {
		try {
			return (IStorage) getDigester(type).parse(in);
		} catch (SAXException e) {
			throw new AstException(e);
		}
	}

}