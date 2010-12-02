package com.code.aon.accounting.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum AccountEntryType.
 */
public enum AnnualReportStyle implements IResourceable {

	NORMAL,
	MAIN_TITLE,
	TITLE,
	SUBTITLE,
	TABLE,
	ROW,
	COLUMN_TEXT,
	TABLE_END,
	ROW_END,
	COLUMN_HEADER,
	COLUMN_NUMBER;
	
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.accounting.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_annual_report_style_";
	
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