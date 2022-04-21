package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Desglose Casilla 1041
// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)
public enum Mod2002021BN1041Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002021Key[]{Mod2002021Key.BN1178,Mod2002021Key.BN1179,Mod2002021Key.BN1446,Mod2002021Key.BN1181},"2015")
	,C02(new Mod2002021Key[]{Mod2002021Key.BN1447,Mod2002021Key.BN1448,Mod2002021Key.BN1449,Mod2002021Key.BN1450},"2016")
	,C03(new Mod2002021Key[]{Mod2002021Key.BN1451,Mod2002021Key.BN1452,Mod2002021Key.BN1453,Mod2002021Key.BN1454},"2017")
	,C04(new Mod2002021Key[]{Mod2002021Key.BN1725,Mod2002021Key.BN1726,Mod2002021Key.BN1727,Mod2002021Key.BN1728},"2018")
	,C05(new Mod2002021Key[]{Mod2002021Key.BN1957,Mod2002021Key.BN1958,Mod2002021Key.BN1959,Mod2002021Key.BN1960},"2019")
	,C06(new Mod2002021Key[]{Mod2002021Key.BN2234,Mod2002021Key.BN2235,Mod2002021Key.BN2236,Mod2002021Key.BN2237},"2020")
	,C07(new Mod2002021Key[]{Mod2002021Key.BN2387,Mod2002021Key.BN2388,Mod2002021Key.BN2389,Mod2002021Key.BN2390},"2021(*)")
	,C08(new Mod2002021Key[]{Mod2002021Key.BN1086,Mod2002021Key.BN1087,Mod2002021Key.BN1088,Mod2002021Key.BN1089},"2021")
	,C09(new Mod2002021Key[]{Mod2002021Key.BN1182,Mod2002021Key.BN1183,Mod2002021Key.BN1041,Mod2002021Key.BN1185},"Total")
	;
	 
    private String description;
    private Mod2002021Key[] keys;

	private Mod2002021BN1041Key(Mod2002021Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002021Key[] getKeys() {
		return keys; 
	}

}

