package com.code.aon.project.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum TaskSource.
 */
public enum TaskSource implements IResourceable {

	/** MANUAL. */
	MANUAL,
	
	/** ASSIGNED. */
	ASSIGNED,
	
	/** AON CONSULTANT. */
	AON_CONSULTANT, 
	
	/** PERIODICAL. */
	PERIODICAL;
	
	/** Message file base path. */
	private static final String BASE_NAME = "com.code.aon.project.i18n.messages";

	/** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_task_source_";
	
	/**
	 * Returns a <code>String</code> with the transalation <code>Locale</code>
	 * for the locale.
	 * 
	 * @param locale Required Locale.
	 * 
	 * @return String a <code>String</code>.
	 */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}
