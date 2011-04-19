package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum DismissalCollective implements IResourceable, IStringEnum {
	
	DC01("01"),
	DC02("02"),
	DC03("03"),
	DC04("04"),
	DC05("05"),
	DC06("06"),
	DC07("07"),
	DC08("08"),
	DC09("09"),
	DC10("10"),
	DC11("11"),
	DC12("12"),
	DC13("13"),
	DC14("14"),
	DC15("15"),
	DC16("16"),
	DC17("17")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_dismissal_collective_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    DismissalCollective( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
}
