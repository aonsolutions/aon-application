package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Desglose Casilla 1040
// Deducción por reversión de medidas temporales (D.T. 37ª.1 LIS)
public enum Mod2002018BN1040Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002018Key[]{Mod2002018Key.BN1166,Mod2002018Key.BN1167,Mod2002018Key.BN1437,Mod2002018Key.BN1169},"2015")
    ,C02(new Mod2002018Key[]{Mod2002018Key.BN1438,Mod2002018Key.BN1439,Mod2002018Key.BN1440,Mod2002018Key.BN1441},"2016")
	,C03(new Mod2002018Key[]{Mod2002018Key.BN1442,Mod2002018Key.BN1443,Mod2002018Key.BN1444,Mod2002018Key.BN1445},"2017")
	,C04(new Mod2002018Key[]{Mod2002018Key.BN1721,Mod2002018Key.BN1722,Mod2002018Key.BN1723,Mod2002018Key.BN1724},"2018(*)")
	,C05(new Mod2002018Key[]{Mod2002018Key.BN1953,Mod2002018Key.BN1954,Mod2002018Key.BN1955,Mod2002018Key.BN1956},"2018")
	,C06(new Mod2002018Key[]{Mod2002018Key.BN1170,Mod2002018Key.BN1171,Mod2002018Key.BN1040,Mod2002018Key.BN1173},"Total")
	;
	 
    private String description;
    private Mod2002018Key[] keys;

	private Mod2002018BN1040Key(Mod2002018Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002018Key[] getKeys() {
		return keys; 
	}
}

