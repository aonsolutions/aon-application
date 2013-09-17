package com.code.aon.common.enumeration;

import java.util.Locale;

/**
 * Interface for all enumerations that needs messages i18n file.
 * 
 * @author Consulting & Development.
 * 
 */
public interface IResourceable {

    /**
     * Messages file base path.
     */
	String BASE_NAME = "com.code.aon.common.i18n.enum";
	
	/**
	 * Return the message <code>String</code> bound to <code>Locale</code>
	 * passed by parameter.
	 * 
	 * @param locale
	 * 
	 * @return The message <code>String</code> bound to <code>Locale</code>
	 *         passed by parameter.
	 */
	String getName(Locale locale);

}
