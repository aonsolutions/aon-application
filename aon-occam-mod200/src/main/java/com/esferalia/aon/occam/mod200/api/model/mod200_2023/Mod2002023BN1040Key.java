package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Desglose Casilla 1040
// Deducción por reversión de medidas temporales (D.T. 37ª.1 LIS)
public enum Mod2002023BN1040Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002023Key[]{Mod2002023Key.BN1166,Mod2002023Key.BN1167,Mod2002023Key.BN1437,Mod2002023Key.BN1169},"2015")
    ,C02(new Mod2002023Key[]{Mod2002023Key.BN1438,Mod2002023Key.BN1439,Mod2002023Key.BN1440,Mod2002023Key.BN1441},"2016")
	,C03(new Mod2002023Key[]{Mod2002023Key.BN1442,Mod2002023Key.BN1443,Mod2002023Key.BN1444,Mod2002023Key.BN1445},"2017")
	,C04(new Mod2002023Key[]{Mod2002023Key.BN1721,Mod2002023Key.BN1722,Mod2002023Key.BN1723,Mod2002023Key.BN1724},"2018")
	,C05(new Mod2002023Key[]{Mod2002023Key.BN1953,Mod2002023Key.BN1954,Mod2002023Key.BN1955,Mod2002023Key.BN1956},"2019")
	,C06(new Mod2002023Key[]{Mod2002023Key.BN2230,Mod2002023Key.BN2231,Mod2002023Key.BN2232,Mod2002023Key.BN2233},"2020")
	,C07(new Mod2002023Key[]{Mod2002023Key.BN2383,Mod2002023Key.BN2384,Mod2002023Key.BN2385,Mod2002023Key.BN2386},"2021")
	,C08(new Mod2002023Key[]{Mod2002023Key.BN1082,Mod2002023Key.BN1083,Mod2002023Key.BN1084,Mod2002023Key.BN1085},"2023(*)")
	,C09(new Mod2002023Key[]{Mod2002023Key.BN1377,Mod2002023Key.BN1378,Mod2002023Key.BN1379,Mod2002023Key.BN1380},"2023")
	,C10(new Mod2002023Key[]{Mod2002023Key.BN1170,Mod2002023Key.BN1171,Mod2002023Key.BN1040,Mod2002023Key.BN1173},"Total")
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023BN1040Key(Mod2002023Key[] keys, String description) {
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

