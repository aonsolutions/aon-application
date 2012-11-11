package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum ContractCode implements IResourceable, IStringEnum {

	C100("100"),
	C109("109"),
	C130("130"),
	C139("139"),
	C150("150"),
	C189("189"),
	
	C200("200"),
	C209("209"),
	C230("230"),
	C239("239"),
	C250("250"),
	C289("289"),
	
	C300("300"),
	C309("309"),
	C330("330"),
	C350("350"),
	C389("389"),
	
	C401("401"),
	C402("402"),
	C403("403"),
	C408("408"),
	C410("410"),
	C418("418"),
	C420("420"),
	C421("421"),
	C430("430"),
	C441("441"),
	C450("450"),
	C452("452"),
	
	C501("501"),
	C502("502"),
	C503("503"),
	C508("508"),
	C510("510"),
	C518("518"),
	C520("520"),
	C530("530"),
	C540("540"),
	C541("541"),
	C550("550"),
	C552("552"), 

	C970("970"),
	C980("980"),
	C990("990")
	
	;

	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_code_";

	
	@Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
	private String value;
    
    ContractCode( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}

	public static ContractCode getContractCodeByValue(String expression) {
		for( ContractCode c : ContractCode.values() ) {
    		if ( c.getValue().equals(expression) ) {
    			return c;
    		}
    	}
    	return null;
	}
	
	
}
