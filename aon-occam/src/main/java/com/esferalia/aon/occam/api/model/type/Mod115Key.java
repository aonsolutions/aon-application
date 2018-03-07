package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod115Key implements IFiscalModelKey {

	CM_001("115-CM-01", 1, null)  // Deshabilitado el cálculo por diferencias (0-Habilitado, 1-Deshabilitado)

	// ---------------------------------------------------------------- ALAVA
	,AR_907("115-AR-907", 907,Administration.ALAVA)
	,AR_908("115-AR-908", 908,Administration.ALAVA)
	,AR_909("115-AR-909", 909,Administration.ALAVA)
	,AR_C01("115-01" 	,   1,Administration.ALAVA)
	,AR_C02("115-02" 	,   2,Administration.ALAVA)
	,AR_C03("115-03" 	,   3,Administration.ALAVA)
	,AR_C04("115-04" 	,   4,Administration.ALAVA)
	,AR_C05("115-05" 	,   5,Administration.ALAVA)
	,AR_C06("115-06" 	,   6,Administration.ALAVA)
	,AR_C07("115-08" 	,   7,Administration.ALAVA)
	,AR_C08("115-AJ" 	,   8,Administration.ALAVA)
	,AR_C09("115-09" 	,   9,Administration.ALAVA)
	,AR_C10("115-10" 	,  10,Administration.ALAVA)
	,AR_C11("115-11" 	,  11,Administration.ALAVA)
	,AR_TIP("115-AR-DT" ,   0,Administration.ALAVA)
	
	// -------------------------------------------------------------- BIZKAIA
	,BZ_C01("115-01" 	,  1,Administration.BIZKAIA)
	,BZ_C02("115-02" 	,  2,Administration.BIZKAIA)
	,BZ_C03("115-03" 	,  3,Administration.BIZKAIA)
	,BZ_C04("115-04" 	,  4,Administration.BIZKAIA)
	,BZ_C05("115-05" 	,  5,Administration.BIZKAIA)
	,BZ_C06("115-06" 	,  6,Administration.BIZKAIA)
	,BZ_C07("115-08" 	,  7,Administration.BIZKAIA)
	,BZ_TIP("115-BZ-DT" ,  0,Administration.BIZKAIA)
	
	// ----------------------------------------------------- COMMON TERRITORY
	,CT_C01("115-01" 	,  1,Administration.COMMON_TERRITORY)
	,CT_C02("115-02" 	,  2,Administration.COMMON_TERRITORY)
	,CT_C03("115-03" 	,  3,Administration.COMMON_TERRITORY)
	,CT_C04("115-07" 	,  4,Administration.COMMON_TERRITORY)
	,CT_C05("115-08" 	,  5,Administration.COMMON_TERRITORY)
	,CT_TIP("115-CT-DT" ,  0,Administration.COMMON_TERRITORY)
	
	// ------------------------------------------------------------- GIPUZKOA
	,GP_C01("115-01" 	,  1,Administration.GIPUZKOA)
	,GP_C02("115-02" 	,  2,Administration.GIPUZKOA)
	,GP_C03("115-03" 	,  3,Administration.GIPUZKOA)
	,GP_C04("115-04" 	,  4,Administration.GIPUZKOA)
	,GP_C05("115-05" 	,  5,Administration.GIPUZKOA)
	,GP_C06("115-06" 	,  6,Administration.GIPUZKOA)
	,GP_C07("115-08" 	,  7,Administration.GIPUZKOA)
	,GP_TIP("115-GP-DT" ,  0,Administration.GIPUZKOA)
	
	// -------------------------------------------------------------- NAVARRA
	,NF_C01("115-08" 	,  1,Administration.NAVARRA)
	,NF_TIP("115-NF-DT" ,  0,Administration.NAVARRA)
	;
	
    private String value;
    private int box;
    private Administration admon;
    
    private Mod115Key(String value, int box,Administration admon) {
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
	public static Mod115Key getKey(String value, Administration admon) {
		for (Mod115Key key : Mod115Key.values()) {
			if (admon == key.getAdministration() && AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}
}