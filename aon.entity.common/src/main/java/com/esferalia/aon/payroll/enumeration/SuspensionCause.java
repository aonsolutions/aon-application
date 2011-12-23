package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum SuspensionCause implements IResourceable, IStringEnum  {

	C1("01"),      
	C2("02"),      
	C3("03"),      
	C4("04"),      
	C5("05"),      
	C6("06"),      
	C7("07"),      
	C8("08"),      
	C9("09"),      
	C10("10"),      
	C11("11"),      
	C12("12"),      
	C13("13"),      
	C14("14"),      
	C15("15"),      
	C16("16"),      
	C17("17"),      
	C18("18"),      
	C19("19"),      
	C20("20"),      
	C21("21"),      
	C22("22"),      
	C23("23"),      
	C24("24"),      
	C25("25"),      
	C26("26"),      
	C27("27"),      
	C28("28"),      
	C29("29");      
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_suspension_cause_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    public String getFullName(Locale locale) {
    	return getValue()+". "+getName(locale);
    }
    
    private String value;
    
    SuspensionCause( String value ) {
      	this.value = value;
  	}
    
    @Override
	public String getValue() {
		return value;
	}
}
