package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod310Key;

public enum Mod310Key implements IFiscalModelKey, IResourceable, IStringEnum  {

	//    value    ,desc ,title,total ,l,admn
	H1  ("310-H1"  ,false,true ,false ,0,null),
	CAC1("310-AC1" ,true ,false,false ,1,null),
	CAC2("310-AC2" ,true ,false,false ,1,null),
	CAC3("310-AC3" ,true ,false,false ,1,null),
	CAC4("310-AC4" ,true ,false,false ,1,null),
	CAC5("310-AC5" ,true ,false,false ,1,null),
	H2  ("310-H2"  ,false,true ,false ,0,null),
	CAG1("310-AG1" ,true ,false,false ,1,null),
	CAG2("310-AG2" ,true ,false,false ,1,null),
	CAG3("310-AG3" ,true ,false,false ,1,null),
	CAG4("310-AG4" ,true ,false,false ,1,null),
	C01 ("310-01"  ,false,false,true  ,0,null),
	SEP ("310-SP"  ,false,true ,true  ,0,null),
	H3  ("310-H3"  ,false,true ,false ,0,null),
	C02 ("310-02"  ,false,false,false ,1,null),
	C03 ("310-03"  ,false,false,false ,1,null),
	C04 ("310-04"  ,false,false,false ,1,null),
	C05 ("310-05"  ,false,false,true  ,0,null),
	SEP1("310-SP1" ,false,true ,true  ,0,null),
	H4  ("310-H4"  ,false,true ,false ,0,null),
	C06 ("310-06"  ,false,false,false ,1,null),
	SEP2 ("310-SP2",false,true ,true  ,0,null),
	C07 ("310-07"  ,false,false,true  ,0,null),
	C08 ("310-08"  ,false,false,false ,0,null),
	C09 ("310-09"  ,false,false,true  ,0,null),
	C11 ("310-11"  ,false,false,false ,0,null),
	C12 ("310-12"  ,false,false,true  ,0,null),
	SEP3("310-SP3" ,false,true,true,0,null),
	C10("310-10"   ,false,false,false ,0,null),
;

    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.keys";
    private static final String MSG_KEY_PREFIX = "aon_enum_mod";

    public static Mod310Key getKeyWithValue( String value ) {
    	for (Mod310Key key : Mod310Key.values() ) {
    		if (StringUtils.equals(value, key.getValue())) {
    			return key;
    		}
    	}
    	throw new IllegalArgumentException("No enum constant " + Mod310Key.class.getName() + " for value " + value);
    }
    
    public static final String ACTIVITIES_PREFIX = "310-AC";
    public static final String FARMING_ACTIVITIES_PREFIX = "310-AG";
    
    private String value;
    private boolean descriptionEnabled;
    private boolean title;
    private boolean total;
    private int level;
    private Administration[] administrations;
    
    private Mod310Key(String value,boolean descriptionEnabled,boolean title,boolean total,int level,Administration[] administrations) {
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
	public boolean accept(Administration administration) {
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