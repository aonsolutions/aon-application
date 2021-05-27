package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum QuoteGroup implements IResourceable, IStringEnum {

	QG01("01"),
	QG02("02"),
	QG03("03"),
	QG04("04"),
	QG05("05"),
	QG06("06"),
	QG07("07"),
	QG08("08"),
	QG09("09"),
	QG10("10"),
	QG11("11")
	
	;

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_quote_group_";

	
	@Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	public String getFullName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return getValue()+". "+bundle.getString(MSG_KEY_PREFIX + toString());
	}

	private String value;
	    
    QuoteGroup( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
	public static QuoteGroup getQuoteGroupByValue(String value){
    	for( QuoteGroup c : QuoteGroup.values() ) {
    		if ( c.getValue().equals(value) ) {
    			return c;
    		}
    	}
    	return null;
    }
	
	
}
