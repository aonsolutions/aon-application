package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod123Key;

public enum Mod123Key implements IFiscalModelKey, IResourceable, IStringEnum  {

	H1 ("123-H1",true ,0,false,true ,new Administration[]{Administration.GIPUZKOA}),
	C01("123-01",false,1,false,false,null),
	C02("123-02",false,1,false,true ,null),
	C03("123-03",false,1,false,true ,null),
	
	H2 ("123-H2",true ,0,false,true ,new Administration[]{Administration.GIPUZKOA}),
	C04("123-04",false,1,false,false,new Administration[]{Administration.GIPUZKOA}),
	C05("123-05",false,1,false,true ,new Administration[]{Administration.GIPUZKOA}),
	C06("123-06",false,1,false,true ,new Administration[]{Administration.GIPUZKOA}),
	
	H3 ("123-H3",true ,0,false,true ,new Administration[]{Administration.GIPUZKOA}),
	C07("123-07",false,1,false,true ,null),
	C08("123-08",false,1,false,true ,null),
	
	C09("123-09",false,0,true ,false,new Administration[]{Administration.ALAVA,Administration.COMMON_TERRITORY}),
	C10("123-10",false,0,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	C11("123-11",false,0,false,false,new Administration[]{Administration.ALAVA}),
	C12("123-12",false,0,false,false,new Administration[]{Administration.ALAVA}),
	C13("123-13",false,0,true ,false,null);
	
    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.keys";
    private static final String MSG_KEY_PREFIX = "aon_enum_mod";

    public static Mod123Key getKeyWithValue( String value ) {
    	for (Mod123Key key : Mod123Key.values() ) {
    		if (StringUtils.equals(value, key.getValue())) {
    			return key;
    		}
    	}
    	throw new IllegalArgumentException("No enum constant " + Mod123Key.class.getName() + " for value " + value);
    }
    
    private String value;
    private boolean title;
    private boolean total;
    private int level;
    private boolean difEnabled;
    private Administration[] administrations;
    
    private Mod123Key(String value,boolean title,int level,boolean total,boolean difEnabled,Administration[] administrations) {
    	this.value = value;
    	this.difEnabled = difEnabled;
    	this.total = total;
    	this.level = level;
    	this.title = title;
    	this.administrations = administrations;
    }
    
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + value);
    }

	public boolean isDifEnabled() {
		return !isTitle() && difEnabled;
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