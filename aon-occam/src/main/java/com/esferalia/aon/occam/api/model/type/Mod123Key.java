package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod123Key implements IFiscalModelKey {
	
	// ---------------------------------------------------------------- ALAVA
	 AR_907("123-AR-907", 907,Administration.ALAVA)
	,AR_908("123-AR-908", 908,Administration.ALAVA)
	,AR_909("123-AR-909", 909,Administration.ALAVA)
	,AR_C01("123-01" 	,   1,Administration.ALAVA)
	,AR_C02("123-02" 	,   2,Administration.ALAVA)
	,AR_C03("123-03" 	,   3,Administration.ALAVA)
	,AR_C04("123-07" 	,   4,Administration.ALAVA)
	,AR_C05("123-08" 	,   5,Administration.ALAVA)
	,AR_C06("123-09" 	,   6,Administration.ALAVA)
	,AR_C07("123-AJ" 	,   7,Administration.ALAVA)
	,AR_C08("123-11" 	,   8,Administration.ALAVA)
	,AR_C09("123-12" 	,   9,Administration.ALAVA)
	,AR_C10("123-13" 	,  10,Administration.ALAVA)
	,AR_TIP("123-AR-DT" ,   0,Administration.ALAVA)
	
	// -------------------------------------------------------------- BIZKAIA
	,BZ_C01("123-01" 	,  1,Administration.BIZKAIA)
	,BZ_C02("123-02" 	,  2,Administration.BIZKAIA)
	,BZ_C03("123-03" 	,  3,Administration.BIZKAIA)
	,BZ_C04("123-07" 	,  4,Administration.BIZKAIA)
	,BZ_C05("123-08" 	,  5,Administration.BIZKAIA)
	,BZ_C06("123-13" 	,  6,Administration.BIZKAIA)
	,BZ_TIP("123-BZ-DT" ,  0,Administration.BIZKAIA)
	
	// ----------------------------------------------------- COMMON TERRITORY
	,CT_C01("123-01" 	,  1,Administration.COMMON_TERRITORY)
	,CT_C02("123-02" 	,  2,Administration.COMMON_TERRITORY)
	,CT_C03("123-03" 	,  3,Administration.COMMON_TERRITORY)
	,CT_C04("123-07" 	,  4,Administration.COMMON_TERRITORY)
	,CT_C05("123-08" 	,  5,Administration.COMMON_TERRITORY)
	,CT_C06("123-09" 	,  6,Administration.COMMON_TERRITORY)
	,CT_C07("123-10" 	,  7,Administration.COMMON_TERRITORY)
	,CT_C08("123-13" 	,  8,Administration.COMMON_TERRITORY)
	,CT_TIP("123-CT-DT" ,  0,Administration.COMMON_TERRITORY)
	
	// ------------------------------------------------------------- GIPUZKOA
	,GP_C01("123-01" 	,  1,Administration.GIPUZKOA)
	,GP_C02("123-02" 	,  2,Administration.GIPUZKOA)
	,GP_C03("123-03" 	,  3,Administration.GIPUZKOA)
	,GP_C04("123-04" 	,  4,Administration.GIPUZKOA)
	,GP_C05("123-05" 	,  5,Administration.GIPUZKOA)
	,GP_C06("123-06" 	,  6,Administration.GIPUZKOA)
	,GP_C07("123-07" 	,  7,Administration.GIPUZKOA)
	,GP_C08("123-08" 	,  8,Administration.GIPUZKOA)
	,GP_C09("123-13" 	,  9,Administration.GIPUZKOA)
	,GP_TIP("123-GP-DT" ,  0,Administration.GIPUZKOA)
	
	// -------------------------------------------------------------- NAVARRA
	,NF_C01("123-13" 	,  1,Administration.NAVARRA)
	,NF_TIP("123-NF-DT" ,  0,Administration.NAVARRA)
	;
	
    private String value;
    private int box;
    private Administration admon;
    
    private Mod123Key(String value, int box,Administration admon) {
		this.value = value;
		this.box = box;
		this.admon = admon;
	}
    @Override
	public String getValue() {
		return value;
	}
	public int getBox() {
		return box;
	}
	public Administration getAdministration() {
		return admon;
	} 
	public static Mod123Key getKey(String value, Administration admon) {
		for (Mod123Key key : Mod123Key.values()) {
			if (admon == key.getAdministration() && AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}
}