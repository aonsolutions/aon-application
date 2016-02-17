package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.config.enumeration.Administration;

public enum Mod111Key implements IFiscalModelKey, IResourceable, IStringEnum  {
	
	// ARABA
	AR_H1  ("111-AR-H1"  ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_H11 ("111-AR-H11" ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_C01 ("111-AR-01"  ,false,2,false,false,new Administration[]{Administration.ALAVA}),
	AR_C02 ("111-AR-02"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C03 ("111-AR-03"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_H12 ("111-AR-H12" ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_C04 ("111-AR-04"  ,false,2,false,false,new Administration[]{Administration.ALAVA}),
	AR_C05 ("111-AR-05"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C06 ("111-AR-06"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_H13 ("111-AR-H13" ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_C07 ("111-AR-07"  ,false,2,false,false,new Administration[]{Administration.ALAVA}),
	AR_C08 ("111-AR-08"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C09 ("111-AR-09"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_H14 ("111-AR-H14" ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_C10 ("111-AR-10"  ,false,2,false,false,new Administration[]{Administration.ALAVA}),
	AR_C11 ("111-AR-11"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C12 ("111-AR-12"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_H5  ("111-AR-H2"  ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_C13 ("111-AR-13"  ,false,2,false,false,new Administration[]{Administration.ALAVA}),
	AR_C14 ("111-AR-14"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C15 ("111-AR-15"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_H6  ("111-AR-H3"  ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_C16 ("111-AR-16"  ,false,2,false,false,new Administration[]{Administration.ALAVA}),
	AR_C17 ("111-AR-17"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C18 ("111-AR-18"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_H7  ("111-AR-H4"  ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_C19 ("111-AR-19"  ,false,2,false,false,new Administration[]{Administration.ALAVA}),
	AR_C20 ("111-AR-20"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C21 ("111-AR-21"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_H8  ("111-AR-H5"  ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_C22 ("111-AR-22"  ,false,2,false,false,new Administration[]{Administration.ALAVA}),
	AR_C23 ("111-AR-23"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C24 ("111-AR-24"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_H9  ("111-AR-H6"  ,true ,0,false,false,new Administration[]{Administration.ALAVA}),
	AR_C25 ("111-AR-25"  ,false,2,false,false,new Administration[]{Administration.ALAVA}),
	AR_C26 ("111-AR-26"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C27 ("111-AR-27"  ,false,2,false,true ,new Administration[]{Administration.ALAVA}),
	AR_C28 ("111-AR-28"  ,false,1,true ,false,new Administration[]{Administration.ALAVA}),
	AR_C29 ("111-AR-29"  ,false,1,false,false,new Administration[]{Administration.ALAVA}),
	AR_C30 ("111-AR-30"  ,false,1,false,false,new Administration[]{Administration.ALAVA}),
	AR_C31 ("111-AR-31"  ,false,1,true ,false,new Administration[]{Administration.ALAVA}),

	// BIZKAIA
	BZ_H1  ("111-BZ-H1"  ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C01 ("111-BZ-01"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C02 ("111-BZ-02"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C03 ("111-BZ-03"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H2  ("111-BZ-H2"  ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C04 ("111-BZ-04"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C05 ("111-BZ-05"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C06 ("111-BZ-06"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H3  ("111-BZ-H3"  ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C07 ("111-BZ-07"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C08 ("111-BZ-08"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C09 ("111-BZ-09"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H4  ("111-BZ-H4"  ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C10 ("111-BZ-10"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C11 ("111-BZ-11"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C12 ("111-BZ-12"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H5  ("111-BZ-H5"  ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C13 ("111-BZ-13"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C14 ("111-BZ-14"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C15 ("111-BZ-15"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H6  ("111-BZ-H6"  ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C16 ("111-BZ-16"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C17 ("111-BZ-17"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C18 ("111-BZ-18"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H7  ("111-BZ-H7"  ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C19 ("111-BZ-19"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C20 ("111-BZ-20"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C21 ("111-BZ-21"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H8  ("111-BZ-H8"  ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C22 ("111-BZ-22"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C23 ("111-BZ-23"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C24 ("111-BZ-24"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H9  ("111-BZ-H9"  ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C25 ("111-BZ-25"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C26 ("111-BZ-26"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C27 ("111-BZ-27"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H10 ("111-BZ-H10" ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C28 ("111-BZ-28"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C29 ("111-BZ-29"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C30 ("111-BZ-30"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H11 ("111-BZ-H11" ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C31 ("111-BZ-31"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C32 ("111-BZ-32"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C33 ("111-BZ-33"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_H12 ("111-BZ-H12" ,true ,0,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C34 ("111-BZ-34"  ,false,2,false,false,new Administration[]{Administration.BIZKAIA}),
	BZ_C35 ("111-BZ-35"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C36 ("111-BZ-36"  ,false,2,false,true ,new Administration[]{Administration.BIZKAIA}),
	BZ_C37 ("111-BZ-37"  ,false,1,true ,false,new Administration[]{Administration.BIZKAIA}),
	
	// TERRITORIO COMUN
	CT_H1  ("111-CT-H1"  ,true ,0,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H11 ("111-CT-H11" ,true ,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C01 ("111-CT-01"  ,false,2,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C02 ("111-CT-02"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C03 ("111-CT-03"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H12 ("111-CT-H12" ,true ,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C04 ("111-CT-04"  ,false,2,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C05 ("111-CT-05"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C06 ("111-CT-06"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H2  ("111-CT-H2"  ,true ,0,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H21 ("111-CT-H21" ,true ,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C07 ("111-CT-07"  ,false,2,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C08 ("111-CT-08"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C09 ("111-CT-09"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H22 ("111-CT-H22" ,true ,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C10 ("111-CT-10"  ,false,2,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C11 ("111-CT-11"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C12 ("111-CT-12"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H3  ("111-CT-H3"  ,true ,0,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H31 ("111-CT-H31" ,true ,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C13 ("111-CT-13"  ,false,2,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C14 ("111-CT-14"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C15 ("111-CT-15"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H32 ("111-CT-H32" ,true ,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C16 ("111-CT-16"  ,false,2,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C17 ("111-CT-17"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C18 ("111-CT-18"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_CH4 ("111-CT-H4"  ,true ,0,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H41("111-CT-H41"  ,true ,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C19 ("111-CT-19"  ,false,2,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C20 ("111-CT-20"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C21 ("111-CT-21"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H42 ("111-CT-H42" ,true ,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C22 ("111-CT-22"  ,false,2,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C23 ("111-CT-23"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C24 ("111-CT-24"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_CT5 ("111-CT-H5"  ,true ,0,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H51 ("111-CT-H51" ,true ,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C25 ("111-CT-25"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C26 ("111-CT-26"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C27 ("111-CT-27"  ,false,2,false,true ,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_H6  ("111-CT-H6"  ,true ,0,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C28 ("111-CT-28"  ,false,1,true ,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C29 ("111-CT-29"  ,false,1,false,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_C30 ("111-CT-30"  ,false,1,true ,false,new Administration[]{Administration.COMMON_TERRITORY}),
	CT_TIP ("111-CT-DT"  ,true ,1,true ,false,new Administration[]{Administration.COMMON_TERRITORY}),

	// GIPUZKOA
	GP_H1  ("111-GP-H1"  ,true ,0,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_H11 ("111-GP-H11" ,true ,1,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C01 ("111-GP-01"  ,false,2,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C02 ("111-GP-02"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_C03 ("111-GP-03"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_H12 ("111-GP-H12" ,true ,1,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C04 ("111-GP-04"  ,false,2,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C05 ("111-GP-05"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_C06 ("111-GP-06"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_H13 ("111-GP-H13" ,true ,1,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C07 ("111-GP-07"  ,false,2,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C08 ("111-GP-08"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_C09 ("111-GP-09"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_H14 ("111-GP-H14" ,true ,1,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C10 ("111-GP-10"  ,false,2,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C11 ("111-GP-11"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_C12 ("111-GP-12"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_H2  ("111-GP-H2"  ,true ,0,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_H21 ("111-GP-H21" ,true ,1,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C13 ("111-GP-13"  ,false,2,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C14 ("111-GP-14"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_C15 ("111-GP-15"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_H22 ("111-GP-H22" ,true ,1,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C16 ("111-GP-16"  ,false,2,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C17 ("111-GP-17"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_C18 ("111-GP-18"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_H23 ("111-GP-H23" ,true ,1,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C19 ("111-GP-19"  ,false,2,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C20 ("111-GP-20"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_C21 ("111-GP-21"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_H24 ("111-GP-H24" ,true ,1,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C22 ("111-GP-22"  ,false,2,false,false,new Administration[]{Administration.GIPUZKOA}),
	GP_C23 ("111-GP-23"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_C24 ("111-GP-24"  ,false,2,false,true ,new Administration[]{Administration.GIPUZKOA}),
	GP_C25 ("111-GP-25"  ,false,1,true ,false,new Administration[]{Administration.GIPUZKOA}),

	// NAFARROA
	NF_A1  ("111-NF-A1"  ,false,2,false,true,new Administration[]{Administration.NAVARRA});

	private static final String MSG_KEY_PREFIX = "aon_enum_mod";

    public static Mod111Key getKeyWithValue( String value ) {
    	for (Mod111Key key : Mod111Key.values() ) {
    		if (StringUtils.equals(value, key.getValue())) {
    			return key;
    		}
    	}
    	throw new IllegalArgumentException("No enum constant " + Mod111Key.class.getName() + " for value " + value);
    }
    
    private String value;
    private boolean title;
    private boolean total;
    private int level;
    private boolean difEnabled;
    private Administration[] administrations;
    
    private Mod111Key(String value,boolean title,int level,boolean total,boolean difEnabled,Administration[] administrations) {
    	this.value = value;
    	this.difEnabled = difEnabled;
    	this.total = total;
    	this.level = level;
    	this.title = title;
    	this.administrations = administrations;
    }
    
    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + value);
    }
    
    @Override
	public boolean isDifEnabled() {
		return !isTitle() && difEnabled;
	}
	
	@Override
	public int getLevel() {
		return level;
	}
	@Override
	public boolean isTitle() {
		return title;
	}
	@Override
	public boolean isTotal() {
		return total;
	}
	@Override
	public boolean isDescriptionEnabled() {
		return false;
	}

	@Override
	public String getValue() {
		return value;
	}
	public Administration[] getAdministrations() {
		return administrations;
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