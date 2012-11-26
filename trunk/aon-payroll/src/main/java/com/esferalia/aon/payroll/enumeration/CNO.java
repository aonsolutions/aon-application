package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum CNO implements IResourceable {
	
	MILITARY,
	DIRECTORS,
	INTELLECTUAL_TECHNICIANS,
	SUPPORT_TECHNICIANS,
	OFFICE_EMPLOYEES,
	SERVICES_EMPLOYEES,
	AGRICULTURAL_EMPLOYEES,
	MANUFACTURING_EMPLOYEES,
	MACHINE_OPERATORS,
	ELEMENTAR
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_cno_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

	public static CNO getCnoByValue(String value) {
		try {
			if (value != null) {
				for (CNO c : CNO.values()) {
					if (c.ordinal() == Integer.parseInt(value)) {
						return c;
					}
				}
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return null;
	}
    
}
