package com.code.aon.ui.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum CampaignStatus.
 */
public enum EnumCalculo implements IResourceable {

	pendiente,
	otro,
	otromas;
	
	/*
    "% s/cuota Empresa",
    "% s/cuota Trabajador",
    "% s/cuota Total",
    "Reduc. s/% Empresa",
    "Reduc. s/% Trabajador",
    "Reduc. s/% Total";
    */
	private static final String BASE_NAME = "com.code.aon.payroll.i18n.messages";

	/** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_status_";
	
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