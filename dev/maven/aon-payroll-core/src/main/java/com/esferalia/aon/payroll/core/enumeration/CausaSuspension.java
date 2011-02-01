package com.esferalia.aon.payroll.core.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum CausaSuspension implements IResourceable, IStringEnum  {

	CAUSA1("01"),      
	CAUSA2("02"),      
	CAUSA3("03"),      
	CAUSA4("04"),      
	CAUSA5("05"),      
	CAUSA6("06"),      
	CAUSA7("07"),      
	CAUSA8("08"),      
	CAUSA9("09"),      
	CAUSA10("10"),      
	CAUSA11("11"),      
	CAUSA12("12"),      
	CAUSA13("13"),      
	CAUSA14("14"),      
	CAUSA15("15"),      
	CAUSA16("16"),      
	CAUSA17("17"),      
	CAUSA18("18"),      
	CAUSA19("19"),      
	CAUSA20("20"),      
	CAUSA21("21"),      
	CAUSA22("22"),      
	CAUSA23("23"),      
	CAUSA24("24"),      
	CAUSA25("25"),      
	CAUSA26("26"),      
	CAUSA27("27"),      
	CAUSA28("28"),      
	CAUSA29("29");      
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.core.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_causa_suspension_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    public String getFullName(Locale locale) {
    	return getValue()+". "+getName(locale);
    }
    
    private String value;
    
    CausaSuspension( String value ) {
      	this.value = value;
  	}
    
    @Override
	public String getValue() {
		return value;
	}
}
