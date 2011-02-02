package com.code.aon.employee.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractCode implements IResourceable {

	C100,
	C109,
	C130,
	C139,
	C150,
	C189,
	
	C200,
	C209,
	C230,
	C239,
	C250,
	C289,
	
	C300,
	C309,
	C330,
	C350,
	C389,
	
	C401,
	C402,
	C403,
	C408,
	C410,
	C418,
	C420,
	C421,
	C430,
	C441,
	C450,
	C452,
	
	C501,
	C502,
	C503,
	C508,
	C510,
	C518,
	C520,
	C530,
	C540,
	C541,
	C550,
	C552, 

	C990,
	
	;

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.employee.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_code_";

	
	@Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
	@Override
	public String toString() {
		String name = this.name();
		return name.substring(1);
	}
	
}
