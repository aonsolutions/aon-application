package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod311Key;

public enum Mod311Key implements IFiscalModelKey, IResourceable, IStringEnum  {

	//    value    ,desc ,title,total ,l,admn
	H1  ("311-H1"  ,false,true ,false ,0,null),
	CAC1("311-AC1" ,true ,false,false ,1,null),
	CAC2("311-AC2" ,true ,false,false ,1,null),
	CAC3("311-AC3" ,true ,false,false ,1,null),
	CAC4("311-AC4" ,true ,false,false ,1,null),
	CAC5("311-AC5" ,true ,false,false ,1,null),
	H2  ("311-H2"  ,false,true ,false ,0,null),
	CAG1("311-AG1" ,true ,false,false ,1,null),
	CAG2("311-AG2" ,true ,false,false ,1,null),
	CAG3("311-AG3" ,true ,false,false ,1,null),
	CAG4("311-AG4" ,true ,false,false ,1,null),
	C01 ("311-01"  ,false,false,true  ,0,null),
	C02 ("311-02"  ,false,false,false ,0,null),
	C03 ("311-03"  ,false,false,true  ,0,null),
	SEP ("311-SP"  ,false,true ,true  ,0,null),
	H3  ("311-H3"  ,false,true ,false ,0,null),
	C04 ("311-04"  ,false,false,false ,1,null),
	C05 ("311-05"  ,false,false,false ,1,null),
	C06 ("311-06"  ,false,false,false ,1,null),
	C07 ("311-07"  ,false,false,true  ,0,null),
	SEP1("311-SP1" ,false,true ,true  ,0,null),
	H4  ("311-H4"  ,false,true ,false ,0,null),
	C08 ("311-08"  ,false,false,false ,1,null),
	C09 ("311-09"  ,false,false,false ,1,null),
	C10 ("311-10"  ,false,false,false ,1,null),
	SEP2 ("311-SP2",false,true ,true  ,0,null),
	C11 ("311-11"  ,false,false,true  ,0,null),
	C12 ("311-12"  ,false,false,false ,0,null),
	C13 ("311-13"  ,false,false,true  ,0,null),
	C15 ("311-15"  ,false,false,false ,0,null),
	C16 ("311-16"  ,false,false,true   ,0,null),
	SEP3("311-SP3" ,false,true,true,0  ,null),
	C14 ("311-14"   ,false,false,false ,0,null),
	PBK ("311-PBK"  ,false,false,false ,0,null),
;

    private static final String MSG_KEY_PREFIX = "aon_enum_mod";

    public static Mod311Key getKeyWithValue( String value ) {
    	for (Mod311Key key : Mod311Key.values() ) {
    		if (StringUtils.equals(value, key.getValue())) {
    			return key;
    		}
    	}
    	throw new IllegalArgumentException("No enum constant " + Mod311Key.class.getName() + " for value " + value);
    }
    
    public static final String ACTIVITIES_PREFIX = "311-AC";
    public static final String FARMING_ACTIVITIES_PREFIX = "311-AG";
    
    private String value;
    private boolean descriptionEnabled;
    private boolean title;
    private boolean total;
    private int level;
    private Administration[] administrations;
    
    private Mod311Key(String value,boolean descriptionEnabled,boolean title,boolean total,int level,Administration[] administrations) {
    	this.value = value;
    	this.descriptionEnabled = descriptionEnabled; 
    	this.level = level;
    	this.title = title;
    	this.total = total;
    	this.administrations = administrations;
    }
    
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + value);
    }

	public boolean isDescriptionEnabled() {
		return descriptionEnabled;
	}

	public boolean isDifEnabled() {
		return false;
	}
	
	public boolean isTitle() {
		return title;
	}

	public boolean isTotal() {
		return total;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean accept(Administration administration, Period period, int year) {
		if (administrations == null) {
			return true;
		}
		for (Administration admin : administrations) {
			if (admin == administration) {
				return true;
			}
		}
		return false;
	}
}