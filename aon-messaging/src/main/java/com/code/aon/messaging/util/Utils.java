package com.code.aon.messaging.util;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

public class Utils {

	public static final String FAX_MAIL_FROM = "fax@esferalia.com";
	public static final String DEFAULT_ZONE = "3";
	public static final String DEFAULT_COUNTRY_CODE = "34";
	public static final String DEFAULT_ZONE_PREFIX = "aon_i11l_prefix_zone_";
	public static final String COUNTRY_CODES_FILE = "/com/code/aon/messaging/countrycodes.properties";

	static final String DEFAULT_BUNDLE_NAME = "com.code.aon.messaging.i18n.messages";

	/**
	 * Parse phone number.
	 * 
	 * @param phone
	 * @return
	 */
	public static final String parsePhoneNumber(String phone) {
		StringBuffer _phone = new StringBuffer();
		char[] array = phone.trim().toCharArray();
		if ( array[0] == '+' )
			_phone.append( array[0] );
		for (int i = 0; i < array.length; i++) {
			if ( array[ i ] >= '0' && array[ i ] <= '9' )
				_phone.append( array[ i ] ); 
		}
		return _phone.toString();
	}

	/**
	 * Return country codes input stream.
	 * 
	 * @return
	 */
	public static final InputStream getCountrycodesResourceAsStream() {
		return Utils.class.getResourceAsStream( Utils.COUNTRY_CODES_FILE );
	}

	/**
	 * Return Messaging resource bundle.
	 *  
	 * @param locale
	 * @return
	 */
	public static final ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle( DEFAULT_BUNDLE_NAME, locale, getCurrentLoader( DEFAULT_BUNDLE_NAME ) );
	}

	/**
	 * Gets the ClassLoader associated with the current thread. Returns the
	 * class loader associated with the specified default object if no context
	 * loader is associated with the current thread.
	 * 
	 * @param defaultObject
	 *            The default object to use to determine the class loader (if
	 *            none associated with current thread.)
	 * @return ClassLoader
	 */
	protected static ClassLoader getCurrentLoader(Object defaultObject) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = defaultObject.getClass().getClassLoader();
		}
		return loader;
	}
}
