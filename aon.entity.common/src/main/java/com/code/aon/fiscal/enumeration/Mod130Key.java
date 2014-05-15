package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod130Key;

public enum Mod130Key implements IFiscalModelKey, IResourceable, IStringEnum  {

	P1 ("130-P1" ,false,true ,0,null),
	SP1("130-SP1",true ,false,0,null),
	H1 ("130-H1" ,true ,false,0,null),
	C01("130-01" ,false,false,1,null),
	C02("130-02" ,false,false,1,null),
	C03("130-03" ,false,true ,1,null),
	C04("130-04" ,false,true ,1,null),
	H11("130-H11",true ,true ,1,null),
	C05("130-05" ,false,false,2,null),
	C06("130-06" ,false,false,2,null),
	C07("130-07" ,false,true ,1,null),
	H2 ("130-H2" ,true ,false,0,null),
	C08("130-08" ,false,false,1,null),
	C09("130-09" ,false,true ,1,null),
	C10("130-10" ,false,false,1,null),
	C11("130-11" ,false,true ,1,null),
	H3 ("130-H3" ,true ,false,0,null),
	C12("130-12" ,false,true ,1,null),
	C13("130-13" ,false,false,1,null),
	C14("130-14" ,false,true ,1,null),
	C15("130-15" ,false,false,1,null),
	C16("130-16" ,false,false,1,null),
	C17("130-17" ,false,true ,1,null),
	C18("130-18" ,false,false,1,null),
	SEP("130-SP" ,true ,false,0,null),
	C19("130-19" ,false,true ,0,null);

    private static final String MSG_KEY_PREFIX = "aon_enum_mod";

    public static Mod130Key getKeyWithValue( String value ) {
    	for (Mod130Key key : Mod130Key.values() ) {
    		if (StringUtils.equals(value, key.getValue())) {
    			return key;
    		}
    	}
    	throw new IllegalArgumentException("No enum constant " + Mod130Key.class.getName() + " for value " + value);
    }
    
    private String value;
    private boolean title;
    private boolean total;
    private int level;
    private Administration[] administrations;
    
    private Mod130Key(String value,boolean title,boolean total,int level,Administration[] administrations) {
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
		return false;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean accept(Administration administration, Period period) {
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