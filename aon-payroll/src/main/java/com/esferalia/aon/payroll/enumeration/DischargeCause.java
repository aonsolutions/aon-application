package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DischargeCause implements IResourceable {
	
	
	CURATION,
	DEATH,
	MEDICAL_INSPECTION,
	DISABILITY,
	TIME_EXHAUSTION,
	IMPROVEMENT,
	ENTERING,
	CONTROL_INSS,
	RECOVERY,
	ENTERING_EDUCATION;
	

	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_discharge_cause_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}
