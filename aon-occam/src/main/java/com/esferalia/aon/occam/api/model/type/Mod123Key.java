package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod123Key implements IFiscalModelKey {
	
	 CM_001("123-CM-01", 1,null)  // Cálculo por diferencias 0-Deshabilitado 1-Habilitado
	
	// ---------------------------------------------------------------- ALAVA
	,AR_930("123-AR-930", 930,Administration.ALAVA)
	,AR_907("123-AR-907", 907,Administration.ALAVA)
	
	// AL MODIFICAR EL MODELO PARA EL 2024 ME HE DADO CUENTA QUE ESTAS CLAVES ESTAN CAMBIADAS, 
	// ASI QUE CAMBIO SOLO EL BOX, PARA QUE NO SE PIERDA EL VALOR Y LA FUNCIONALIDAD DE LA CASILLA EN AÑOS ANTERIORES
	,AR_908("123-AR-908", 909,Administration.ALAVA)  // Tipo de Autoliquidación         ESTA REALMENTE ES LA 909
	,AR_909("123-AR-909", 908,Administration.ALAVA)  // Fecha auto declaración concurso ESTA REALMENTE ES LA 908
	
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
	,AR_C21("123-21" 	,  21,Administration.ALAVA)
	,AR_C22("123-22" 	,  22,Administration.ALAVA)
	,AR_C23("123-23" 	,  23,Administration.ALAVA)
	,AR_C24("123-24" 	,  24,Administration.ALAVA)
	,AR_C25("123-25" 	,  25,Administration.ALAVA)
	,AR_C26("123-26" 	,  26,Administration.ALAVA)
	,AR_TIP("123-AR-DT" ,   0,Administration.ALAVA)
	
	// -------------------------------------------------------------- BIZKAIA
	,BZ_C01("123-01" 	,  1,Administration.BIZKAIA)
	,BZ_C02("123-02" 	,  2,Administration.BIZKAIA)
	,BZ_C03("123-03" 	,  3,Administration.BIZKAIA)
	,BZ_C04("123-07" 	,  4,Administration.BIZKAIA)
	,BZ_C05("123-08" 	,  5,Administration.BIZKAIA)
	,BZ_C06("123-13" 	,  6,Administration.BIZKAIA)
	,BZ_C07("123-14" 	,  7,Administration.BIZKAIA)
	,BZ_C08("123-15" 	,  8,Administration.BIZKAIA)
	,BZ_C09("123-16" 	,  9,Administration.BIZKAIA)
	,BZ_C10("123-17" 	, 10,Administration.BIZKAIA)
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
	,CT_C09("123-14" 	,  9,Administration.COMMON_TERRITORY)
	,CT_C10("123-15" 	, 10,Administration.COMMON_TERRITORY)
	,CT_C11("123-16" 	, 11,Administration.COMMON_TERRITORY)
	,CT_C12("123-17" 	, 12,Administration.COMMON_TERRITORY)
	,CT_C13("123-18" 	, 13,Administration.COMMON_TERRITORY)
	,CT_C14("123-19" 	, 14,Administration.COMMON_TERRITORY)	
	,CT_TIP("123-CT-DT" ,  0,Administration.COMMON_TERRITORY)
	
	// ------------------------------------------------------------- GIPUZKOA
	,GP_X00("123-GP-X0" ,  0,Administration.GIPUZKOA)
	,GP_C01("123-01" 	,  1,Administration.GIPUZKOA)
	,GP_C02("123-02" 	,  2,Administration.GIPUZKOA)
	,GP_C03("123-03" 	,  3,Administration.GIPUZKOA)
	,GP_C04("123-04" 	,  4,Administration.GIPUZKOA)
	,GP_C05("123-05" 	,  5,Administration.GIPUZKOA)
	,GP_C06("123-06" 	,  6,Administration.GIPUZKOA)
	,GP_C07("123-07" 	,  7,Administration.GIPUZKOA)
	,GP_C08("123-08" 	,  8,Administration.GIPUZKOA)
	,GP_C09("123-13" 	,  9,Administration.GIPUZKOA)
	,GP_C10("123-14" 	, 10,Administration.GIPUZKOA)
	,GP_C11("123-15" 	, 11,Administration.GIPUZKOA)
	,GP_C12("123-16" 	, 12,Administration.GIPUZKOA)
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
    @Override
	public int getBox() {
		return box;
	}
	public Administration getAdministration() {
		return admon;
	} 
	@Override
	public String getBoxFormatted() {
		return " [" + getBoxAsString() +"] ";
	}
	public String getBoxAsString() {
		return AonStringUtils.leftPad(Integer.toString(getBox()), 3, '0');
	}
	public String getBoxCode() {
		return AonStringUtils.leftPad(AonNumberUtils.toString(box), 3, AonStringUtils.ZERO); 
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