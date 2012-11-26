package com.code.aon.jaas.vendor.tomcat;

import java.io.File;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ConfigurationParser {
	
	/** ConfigurationParser Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationParser.class);

	public Properties parse(File file) {
		Properties properties = new Properties();
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList options = doc.getElementsByTagName("option");
			for (int i = 0; i < options.getLength(); i++) {
				Element e = (Element) options.item(i);
				properties.put( e.getAttribute("name"), e.getAttribute("value") );
			}
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		}
		return properties;
	}

}
