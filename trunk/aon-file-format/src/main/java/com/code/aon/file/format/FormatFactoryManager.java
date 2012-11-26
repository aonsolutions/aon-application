package com.code.aon.file.format;

import java.util.ArrayList;

/**
 * The FormatFactory manager
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class FormatFactoryManager {

	/**
	 * Registers a factory
	 * 
	 * @param factory the factory
	 */
	public static void register(FormatFactory factory) {
		if (!factories.contains(factory)) {
			factories.add(factory);
		}
	}

	/**
	 * Recovers a factory
	 * 
	 * @return the FormatFactory
	 */
	public static FormatFactory getFormatFactory() {
		for (FormatFactory factory: factories) {
			return (factory.accept()) ? factory : null;
		}
		return null;
	}

	/**
	 * Creates a Format from the FormatFactory accepted
	 * 
	 * @param formatName Format name
	 * @return the required Format
	 */
	public static Format createFormat(String formatName) {
		FormatFactory formatFactory = getFormatFactory();
		return (formatFactory != null) ? formatFactory.createFormat(formatName) : null;
	}

	/**
	 * The factories list
	 */
	static ArrayList<FormatFactory> factories = new ArrayList<FormatFactory>();
}