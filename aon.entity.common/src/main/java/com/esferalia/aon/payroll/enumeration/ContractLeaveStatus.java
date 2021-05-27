package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractLeaveStatus implements IResourceable {

	/** PENDING. */
	PENDING,

	/** BATCHED. */
	BATCHED,
    
	/** RETURNED. */
	RETURNED,
    
	/** PROCESSED. */
	PROCESSED
	
	;

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_leave_status_";

	
	@Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}
