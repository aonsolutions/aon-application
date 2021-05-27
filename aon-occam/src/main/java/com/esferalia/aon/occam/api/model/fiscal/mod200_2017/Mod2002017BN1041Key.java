package com.esferalia.aon.occam.api.model.fiscal.mod200_2017;

import java.io.Serializable;

// Desglose Casilla 1041
// Deducción por reversión de medidas temporales (D.T. 37ª.2 LIS)
public enum Mod2002017BN1041Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002017Key[]{Mod2002017Key.BN1178,Mod2002017Key.BN1179,Mod2002017Key.BN1446,Mod2002017Key.BN1181},"2015")
	,C02(new Mod2002017Key[]{Mod2002017Key.BN1447,Mod2002017Key.BN1448,Mod2002017Key.BN1449,Mod2002017Key.BN1450},"2016")
	,C03(new Mod2002017Key[]{Mod2002017Key.BN1451,Mod2002017Key.BN1452,Mod2002017Key.BN1453,Mod2002017Key.BN1454},"2017(*)")
	,C04(new Mod2002017Key[]{Mod2002017Key.BN1725,Mod2002017Key.BN1726,Mod2002017Key.BN1727,Mod2002017Key.BN1728},"2017")
	,C05(new Mod2002017Key[]{Mod2002017Key.BN1182,Mod2002017Key.BN1183,null /* BN1041 */   ,Mod2002017Key.BN1185},"Total")
	;
	 
    private String description;
    private Mod2002017Key[] keys;

	private Mod2002017BN1041Key(Mod2002017Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002017Key[] getKeys() {
		return keys; 
	}
}

