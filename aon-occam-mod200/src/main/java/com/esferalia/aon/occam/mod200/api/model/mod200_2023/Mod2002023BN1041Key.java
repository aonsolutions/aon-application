package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Desglose Casilla 1041
// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)
public enum Mod2002023BN1041Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002023Key[]{Mod2002023Key.BN1178,Mod2002023Key.BN1179,Mod2002023Key.BN1446,Mod2002023Key.BN1181},"2015")
	,C02(new Mod2002023Key[]{Mod2002023Key.BN1447,Mod2002023Key.BN1448,Mod2002023Key.BN1449,Mod2002023Key.BN1450},"2016")
	,C03(new Mod2002023Key[]{Mod2002023Key.BN1451,Mod2002023Key.BN1452,Mod2002023Key.BN1453,Mod2002023Key.BN1454},"2017")
	,C04(new Mod2002023Key[]{Mod2002023Key.BN1725,Mod2002023Key.BN1726,Mod2002023Key.BN1727,Mod2002023Key.BN1728},"2018")
	,C05(new Mod2002023Key[]{Mod2002023Key.BN1957,Mod2002023Key.BN1958,Mod2002023Key.BN1959,Mod2002023Key.BN1960},"2019")
	,C06(new Mod2002023Key[]{Mod2002023Key.BN2234,Mod2002023Key.BN2235,Mod2002023Key.BN2236,Mod2002023Key.BN2237},"2020")
	,C07(new Mod2002023Key[]{Mod2002023Key.BN2387,Mod2002023Key.BN2388,Mod2002023Key.BN2389,Mod2002023Key.BN2390},"2021")
	,C08(new Mod2002023Key[]{Mod2002023Key.BN1086,Mod2002023Key.BN1087,Mod2002023Key.BN1088,Mod2002023Key.BN1089},"2022")
	,C09(new Mod2002023Key[]{Mod2002023Key.BN1381,Mod2002023Key.BN1382,Mod2002023Key.BN1383,Mod2002023Key.BN1384},"2023(*)")
	,C10(new Mod2002023Key[]{Mod2002023Key.BN2705,Mod2002023Key.BN2706,Mod2002023Key.BN2707,Mod2002023Key.BN2708},"2023")
	,C11(new Mod2002023Key[]{Mod2002023Key.BN1182,Mod2002023Key.BN1183,Mod2002023Key.BN1041,Mod2002023Key.BN1185},"Total")
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023BN1041Key(Mod2002023Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002023Key[] getKeys() {
		return keys; 
	}

}

