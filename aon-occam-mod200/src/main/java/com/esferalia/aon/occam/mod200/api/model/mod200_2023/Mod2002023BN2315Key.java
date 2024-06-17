package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Desglose casilla 2315
// Deducción por inversiones y gastos realizados por las autoridades portuarias (art. 38 bis LIS) 
public enum Mod2002023BN2315Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
    
     C01(new Mod2002023Key[]{Mod2002023Key.BN1284 ,Mod2002023Key.BN1287 ,Mod2002023Key.BN1288 },"2020")    
    ,C02(new Mod2002023Key[]{Mod2002023Key.BN1289 ,Mod2002023Key.BN1290 ,Mod2002023Key.BN1291 },"2021")    
    ,C03(new Mod2002023Key[]{Mod2002023Key.BN1292 ,Mod2002023Key.BN1293 ,Mod2002023Key.BN1294 },"2022") 
    ,C04(new Mod2002023Key[]{Mod2002023Key.BN1295 ,Mod2002023Key.BN1296 ,Mod2002023Key.BN1297 },"2023(*)")
    ,C05(new Mod2002023Key[]{Mod2002023Key.BN2312 ,Mod2002023Key.BN2313 ,Mod2002023Key.BN2353 },"2023")
    ,C06(new Mod2002023Key[]{Mod2002023Key.BN1298 ,Mod2002023Key.BN2315 ,Mod2002023Key.BN1304 },"Total")    
	;	
	 
    private String description;
    private Mod2002023Key[] keys;
    
	private Mod2002023BN2315Key(Mod2002023Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}

	public Mod2002023Key[] getKeys() {
		return keys;
	}
	
	public String getDescription() {
		return description;
	}

}

