package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod111Key implements IFiscalModelKey {
	
	 CM_001("111-CM-01", 1)  // Deshabilitado el cálculo por diferencias (0-Habilitado, 1-Deshabilitado)
	
	// ------------------------------------------------------------------ ALAVA
	,AR_907("111-AR-907", 907)
	,AR_908("111-AR-908", 908)
	,AR_909("111-AR-909", 909)
	,AR_C50("111-AR-01" ,  50)
	,AR_C60("111-AR-02" ,  60)
	,AR_C70("111-AR-03" ,  70)
	,AR_C51("111-AR-04" ,  51)
	,AR_C61("111-AR-05" ,  61)
	,AR_C71("111-AR-06" ,  71)
	,AR_C52("111-AR-07" ,  52)
	,AR_C62("111-AR-08" ,  62)
	,AR_C72("111-AR-09" ,  72)
	,AR_C53("111-AR-10" ,  53)
	,AR_C63("111-AR-11" ,  63)
	,AR_C73("111-AR-12" ,  73)
	,AR_C54("111-AR-13" ,  54)
	,AR_C64("111-AR-14" ,  64)
	,AR_C74("111-AR-15" ,  74)
	,AR_C58("111-AR-16" ,  58)
	,AR_C68("111-AR-17" ,  68)
	,AR_C78("111-AR-18" ,  78)
	,AR_C55("111-AR-19" ,  55)
	,AR_C65("111-AR-20" ,  65)
	,AR_C75("111-AR-21" ,  75)
	,AR_C56("111-AR-22" ,  56)
	,AR_C66("111-AR-23" ,  66)
	,AR_C76("111-AR-24" ,  76)
	,AR_C57("111-AR-25" ,  57)
	,AR_C67("111-AR-26" ,  67)
	,AR_C77("111-AR-27" ,  77)
	,AR_C80("111-AR_80" ,  80)
	,AR_C81("111-AR-81" ,  81)
	,AR_C82("111-AR-28" ,  82)
	,AR_C83("111-AR-83" ,  83)
	,AR_C84("111-AR-29" ,  84)
	,AR_C85("111-AR-30" ,  85)
	,AR_C87("111-AR-31" ,  87)
	,AR_TIP("111-AR-DT" ,  0)

	// ---------------------------------------------------------------- BIZKAIA
	,BZ_C01 ("111-BZ-01" ,  1)
	,BZ_C12 ("111-BZ-02" , 12)
	,BZ_C23 ("111-BZ-03" , 23)
	,BZ_C02 ("111-BZ-04" ,  2)
	,BZ_C13 ("111-BZ-05" , 13)
	,BZ_C24 ("111-BZ-06" , 24)
	,BZ_C03 ("111-BZ-07" ,  3)
	,BZ_C14 ("111-BZ-08" , 14)
	,BZ_C25 ("111-BZ-09" , 25)
	,BZ_C04 ("111-BZ-10" ,  4)
	,BZ_C15 ("111-BZ-11" , 15)
	,BZ_C26 ("111-BZ-12" , 26)
	,BZ_C05 ("111-BZ-13" ,  5)
	,BZ_C16 ("111-BZ-14" , 16)
	,BZ_C27 ("111-BZ-15" , 27)
	,BZ_C06 ("111-BZ-16" ,  6)
	,BZ_C17 ("111-BZ-17" , 17)
	,BZ_C28 ("111-BZ-18" , 28)
	,BZ_C07 ("111-BZ-19" ,  7)
	,BZ_C18 ("111-BZ-20" , 18)
	,BZ_C29 ("111-BZ-21" , 29)
	,BZ_C50 ("111-BZ-22" , 50)
	,BZ_C51 ("111-BZ-23" , 51)
	,BZ_C52 ("111-BZ-24" , 52)
	,BZ_C08 ("111-BZ-25" ,  8)
	,BZ_C19 ("111-BZ-26" , 19)
	,BZ_C30 ("111-BZ-27" , 30)
	,BZ_C09 ("111-BZ-28" ,  9)
	,BZ_C20 ("111-BZ-29" , 20)
	,BZ_C31 ("111-BZ-30" , 31)
	,BZ_C10 ("111-BZ-31" , 10)
	,BZ_C21 ("111-BZ-32" , 21)
	,BZ_C32 ("111-BZ-33" , 32)
	,BZ_C11 ("111-BZ-34" , 11)
	,BZ_C22 ("111-BZ-35" , 22)
	,BZ_C33 ("111-BZ-36" , 33)
	,BZ_C34T("111-BZ-T34", 34)
	,BZ_C35T("111-BZ-T35", 35)
	,BZ_C36T("111-BZ-T36", 36)
	,BZ_C39 ("111-BZ-37" , 39)
	,BZ_TIP ("111-BZ-DT" ,  0)

	// ------------------------------------------------------- COMMON TERRITORY
	,CT_C01("111-CT-01" ,  1)
	,CT_C02("111-CT-02" ,  2)
	,CT_C03("111-CT-03" ,  3)
	,CT_C04("111-CT-04" ,  4)
	,CT_C05("111-CT-05" ,  5)
	,CT_C06("111-CT-06" ,  6)
	,CT_C07("111-CT-07" ,  7)
	,CT_C08("111-CT-08" ,  8)
	,CT_C09("111-CT-09" ,  9)
	,CT_C10("111-CT-10" , 10)
	,CT_C11("111-CT-11" , 11)
	,CT_C12("111-CT-12" , 12)
	,CT_C13("111-CT-13" , 13)
	,CT_C14("111-CT-14" , 14)
	,CT_C15("111-CT-15" , 15)
	,CT_C16("111-CT-16" , 16)
	,CT_C17("111-CT-17" , 17)
	,CT_C18("111-CT-18" , 18)
	,CT_C19("111-CT-19" , 19)
	,CT_C20("111-CT-20" , 20)
	,CT_C21("111-CT-21" , 21)
	,CT_C22("111-CT-22" , 22)
	,CT_C23("111-CT-23" , 23)
	,CT_C24("111-CT-24" , 24)
	,CT_C25("111-CT-25" , 25)
	,CT_C26("111-CT-26" , 26)
	,CT_C27("111-CT-27" , 27)
	,CT_C28("111-CT-28" , 28)
	,CT_C29("111-CT-29" , 29)
	,CT_C30("111-CT-30" , 30)
	,CT_TIP("111-CT-DT" ,  0)

	// -------------------------------------------------------------- GIPUZKOA
	,GP_C01("111-GP-01" ,  1)
	,GP_C02("111-GP-02" ,  2)
	,GP_C03("111-GP-03" ,  3)
	,GP_C04("111-GP-04" ,  4)
	,GP_C05("111-GP-05" ,  5)
	,GP_C06("111-GP-06" ,  6)
	,GP_C07("111-GP-07" ,  7)
	,GP_C08("111-GP-08" ,  8)
	,GP_C09("111-GP-09" ,  9)
	,GP_C10("111-GP-10" , 10)
	,GP_C11("111-GP-11" , 11)
	,GP_C12("111-GP-12" , 12)
	,GP_C13("111-GP-T13" , 13)
	,GP_C14("111-GP-T14" , 14)
	,GP_C15("111-GP-13" , 15)
	,GP_C16("111-GP-14" , 16)
	,GP_C17("111-GP-15" , 17)
	,GP_C18("111-GP-16" , 18)
	,GP_C19("111-GP-17" , 19)
	,GP_C20("111-GP-18" , 20)
	,GP_C21("111-GP-19" , 21)
	,GP_C22("111-GP-20" , 22)
	,GP_C23("111-GP-21" , 23)
	,GP_C24("111-GP-22" , 24)
	,GP_C25("111-GP-23" , 25)
	,GP_C26("111-GP-24" , 26)
	,GP_C27("111-GP-T27" , 27)
	,GP_C28("111-GP-T28" , 28)
	,GP_C29("111-GP-25" , 29)
	,GP_TIP("111-GP-DT" ,  0)

	// -------------------------------------------------------------- NAVARRA
	,NF_A1 ("111-NF-A1"  ,  1)
	,NF_TIP("111-NF-DT" ,  0)
	;

	private String value;
	private int box;

	private Mod111Key(String value, int box) {
		this.value = value;
		this.box = box;
	}
	@Override
	public String getValue() {
		return value;
	}
	public int getBox() {
		return box;
	}

	public static Mod111Key getKey(String value) {
		for (Mod111Key key : Mod111Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}

}
