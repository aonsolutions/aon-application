package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod131Key;

public enum Mod131Key implements IFiscalModelKey, IResourceable, IStringEnum  {

	ACH1("131-ACH1",true ,false,0,null),
	AC11("131-AC11",false,false,1,null),
	AC12("131-AC12",false,false,1,null),
	AC13("131-AC13",false,false,1,null),
	AC14("131-AC14",false,false,1,null),
	AC21("131-AC21",false,false,1,null),
	AC22("131-AC22",false,false,1,null),
	AC23("131-AC23",false,false,1,null),
	AC24("131-AC24",false,false,1,null),
	AC31("131-AC31",false,false,1,null),
	AC32("131-AC32",false,false,1,null),
	AC33("131-AC33",false,false,1,null),
	AC34("131-AC34",false,false,1,null),
	AC41("131-AC41",false,false,1,null),
	AC42("131-AC42",false,false,1,null),
	AC43("131-AC43",false,false,1,null),
	AC44("131-AC44",false,false,1,null),
	AC51("131-AC51",false,false,1,null),
	AC52("131-AC52",false,false,1,null),
	AC53("131-AC53",false,false,1,null),
	AC54("131-AC54",false,false,1,null),
	AC01("131-AC01",false,true ,1,null),
	AC02("131-AC02",false,true ,1,null),
	
	H2 ("131-H2" ,true ,true ,0,null),
	C03("131-03" ,false,false,1,null),
	C04("131-04" ,false,true ,1,null),
	
	H3 ("131-H3" ,true ,true ,0,null),
	C05("131-05" ,false,false,1,null),
	C06("131-06" ,false,true ,1,null),
	
	H4 ("131-H4"   ,true ,true ,0,null),
	C07("131-07"   ,false,true ,1,null),
	C08("131-08"   ,false,false,1,null),
	C09("131-09"   ,false,false,1,null),
	C091("131-091" ,false,false,1,null),
	C10("131-10"   ,false,true ,1,null),
	C11("131-11"   ,false,false,1,null),
	C12("131-12"   ,false,false,1,null),
	C13("131-13"   ,false,true ,1,null),
	C14("131-14"   ,false,false,1,null),
	C15("131-15"   ,false,true ,1,null);

	public static final String ACTIVITIES_PREFIX = "131-AC";
	
    private static final String MSG_KEY_PREFIX = "aon_enum_mod";

    public static Mod131Key getKeyWithValue( String value ) {
    	for (Mod131Key key : Mod131Key.values() ) {
    		if (StringUtils.equals(value, key.getValue())) {
    			return key;
    		}
    	}
    	throw new IllegalArgumentException("No enum constant " + Mod131Key.class.getName() + " for value " + value);
    }
    
    private String value;
    private boolean title;
    private boolean total;
    private int level;
    private Administration[] administrations;
    
    private Mod131Key(String value,boolean title,boolean total,int level,Administration[] administrations) {
    	this.value = value;
    	this.level = level;
    	this.title = title;
    	this.total = total;
    	this.administrations = administrations;
    }
    
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + value);
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

	@Override
	public boolean isDescriptionEnabled() {
		return (this == Mod131Key.AC11 || this == Mod131Key.AC21 || this == Mod131Key.AC31 || this == Mod131Key.AC41);
	}

	public int getLevel() {
		return level;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	public boolean isActivityKey() {
		return StringUtils.startsWith(getValue(),ACTIVITIES_PREFIX);
	}
	public boolean isAc01() {
		return this == AC01;
	}
	public boolean isAc02() {
		return this == AC02;
	}

	@Override
	public boolean accept(Administration administration, Period period, int year) {
		if (this == C09 ) {
			return (year < 2015);
		}
		if (this == C091 ) {
			return (year >= 2015);
		}
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