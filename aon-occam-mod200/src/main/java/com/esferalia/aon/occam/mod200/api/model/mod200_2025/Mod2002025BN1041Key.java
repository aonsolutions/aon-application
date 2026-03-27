package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Desglose Casilla 1041
// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)
public enum Mod2002025BN1041Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002025Key[]{Mod2002025Key.BN1178,Mod2002025Key.BN1179,Mod2002025Key.BN1446,Mod2002025Key.BN1181},"2015")
	,C02(new Mod2002025Key[]{Mod2002025Key.BN1447,Mod2002025Key.BN1448,Mod2002025Key.BN1449,Mod2002025Key.BN1450},"2016")
	,C03(new Mod2002025Key[]{Mod2002025Key.BN1451,Mod2002025Key.BN1452,Mod2002025Key.BN1453,Mod2002025Key.BN1454},"2017")
	,C04(new Mod2002025Key[]{Mod2002025Key.BN1725,Mod2002025Key.BN1726,Mod2002025Key.BN1727,Mod2002025Key.BN1728},"2018")
	,C05(new Mod2002025Key[]{Mod2002025Key.BN1957,Mod2002025Key.BN1958,Mod2002025Key.BN1959,Mod2002025Key.BN1960},"2019")
	,C06(new Mod2002025Key[]{Mod2002025Key.BN2234,Mod2002025Key.BN2235,Mod2002025Key.BN2236,Mod2002025Key.BN2237},"2020")
	,C07(new Mod2002025Key[]{Mod2002025Key.BN2387,Mod2002025Key.BN2388,Mod2002025Key.BN2389,Mod2002025Key.BN2390},"2021")
	,C08(new Mod2002025Key[]{Mod2002025Key.BN1086,Mod2002025Key.BN2477,Mod2002025Key.BN2478,Mod2002025Key.BN2479},"2022")
	,C09(new Mod2002025Key[]{Mod2002025Key.BN1381,Mod2002025Key.BN1382,Mod2002025Key.BN1383,Mod2002025Key.BN1384},"2023")
	,C10(new Mod2002025Key[]{Mod2002025Key.BN2705,Mod2002025Key.BN2706,Mod2002025Key.BN2707,Mod2002025Key.BN2708},"2024")
	,C11(new Mod2002025Key[]{Mod2002025Key.BN929 ,Mod2002025Key.BN992 ,Mod2002025Key.BN2070,Mod2002025Key.BN2071},"2025(*)")
	,C12(new Mod2002025Key[]{Mod2002025Key.BN3571,Mod2002025Key.BN3572,Mod2002025Key.BN3573,Mod2002025Key.BN3574},"2025")
	,C13(new Mod2002025Key[]{Mod2002025Key.BN1182,Mod2002025Key.BN1183,Mod2002025Key.BN1041,Mod2002025Key.BN1185},"Total")
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025BN1041Key(Mod2002025Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002025Key[] getKeys() {
		return keys; 
	}

}

