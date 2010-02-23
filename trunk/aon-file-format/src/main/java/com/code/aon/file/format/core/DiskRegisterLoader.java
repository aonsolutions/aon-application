package com.code.aon.file.format.core;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * Loads the register files into register manager 
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class DiskRegisterLoader {

	/**
	 * Load from an inputstream into register manager
	 * 
	 * @param input the inputstream
	 * @param manager the register manager
	 */
	public static void load(InputStream input, RegisterManager manager) {
		try {
			SAXParser parser = FACTORY.newSAXParser();
			parser.parse(input,new DiskRegisterHandler(manager));
		}
		catch (SAXException e) {}
		catch (ParserConfigurationException e) {}
		catch (IOException e) {}
	}

	/**
	 * SAXParserFactory
	 */
	public static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();

	static {
		FACTORY.setNamespaceAware(true);
	}

}
