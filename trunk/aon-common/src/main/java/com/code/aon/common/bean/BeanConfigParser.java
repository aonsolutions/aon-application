package com.code.aon.common.bean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.common.ManagerBeanException;

/**
 * Parse the bean_config_digester.xml file defined by the user and registers its beans.
 * 
 * @author Consulting & Development.
 *
 */

public class BeanConfigParser {

	/**
	 * XML rules file.
	 */
	private static final String RULES_FILE = "bean_config_digester.xml";

	/**
	 * PUBLICID
	 */
	private static final String CONFIG_FILE_PUBLICID = "-//CODE-AON//DTD Bean Config//EN";

	/**
	 * Singleton instance.
	 */
	private static BeanConfigParser configurationParser;

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(BeanConfigParser.class);

	/**
	 * The Digester instance.
	 */
	private static Digester DIGESTER;

	/**
	 * Private Constructor.
	 */
	private BeanConfigParser() {

	}

	/**
	 * Gets the <code>ReportConfigurationParser</code> instance.
	 * 
	 * @return The <code>ReportConfigurationParser</code> instance.
	 */
	public static BeanConfigParser getInstance() {
		if (configurationParser == null) {
			configurationParser = new BeanConfigParser();
		}
		return configurationParser;
	}

	/**
	 * Returns the Digester instance.
	 * 
	 * @return The Digester instance.
	 */
	private static final Digester getDigester() {
		if (DIGESTER == null) {
			LOGGER.info("Reading config from {}", RULES_FILE);
			DIGESTER = DigesterLoader
					.createDigester(BeanConfigParser.class
							.getResource(RULES_FILE));
			DIGESTER.setPublicId(CONFIG_FILE_PUBLICID);
			URL url = BeanConfigParser.class
					.getResource("bean-config.dtd");
			DIGESTER.register(CONFIG_FILE_PUBLICID, url
					.toString());
			DIGESTER.setValidating(true);
			// TODO Cambiar la DTD a un XML-Schema cuando cambiemos a la JDK-5
			// (JAXP-1.3 -xerces-).
			DIGESTER.setUseContextClassLoader(true);
		}
		return DIGESTER;
	}

	/**
	 * Parse the input stream.
	 * 
	 * @param in
	 * @throws ManagerBeanException
	 */
	public void parse(InputStream in) throws ManagerBeanException {
		try {
			Digester digester = getDigester();
			digester.parse(in);
		} catch (IOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}
}
