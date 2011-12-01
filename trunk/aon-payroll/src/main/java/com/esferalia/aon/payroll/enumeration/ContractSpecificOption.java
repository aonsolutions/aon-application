package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractSpecificOption implements IResourceable {
	
	RESEARCH,
	JOB_PROGRAM,
	ETT,
	JOB_OFFER,
	SCHOOL_WORKSHOP,
	DISABILITY,
	OLDER_THAN_52,	
	MANAGEMENT_ATTACH,
	CANPAIGN
	;
		
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_specific_option_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
	
}
