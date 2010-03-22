package com.code.aon.csb.fd0.formats;

import java.util.HashMap;
import java.util.Map;

/**
 * The Factory of Formats 
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class FormatFactory {

	/**
	 * Returns true if is the correct factory
	 * 
	 * @return boolean
	 */
	protected boolean accept() {
		return true;
	}

	/**
	 * Recovers the required Format from the map,
	 * Instances if needed and stores it in the map
	 * 
	 * @param formatName the name of the Format
	 * @return the required Format
	 */
	public Format createFormat(String formatName) {
		if (map.containsKey(formatName)) {
			return (Format)map.get(formatName);
		}

		Format format = null;
		try {
			format = (Format)Class.forName("com.code.aon.csb.fd0.formats." + formatName).newInstance();
			map.put(formatName,format);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return format;
	}

	/**
	 * Constains instanced Formats 
	 */
	private Map<String,Format> map = new HashMap<String,Format>();

	/**
	 * A static instance of this FormatFactory
	 */
	protected static FormatFactory FACT = new FormatFactory();

	static {
		FormatFactoryManager.register(FACT);
	}

}
