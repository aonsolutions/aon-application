package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Desglose casilla 2315
// Deducción por inversiones y gastos realizados por las autoridades portuarias (art. 38 bis LIS) 
public enum Mod2002024BN2315Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
    
     C01(new Mod2002024Key[]{Mod2002024Key.BN1284 ,Mod2002024Key.BN1287 ,Mod2002024Key.BN1288 },"2020")    
    ,C02(new Mod2002024Key[]{Mod2002024Key.BN1289 ,Mod2002024Key.BN1290 ,Mod2002024Key.BN1291 },"2021")    
    ,C03(new Mod2002024Key[]{Mod2002024Key.BN1292 ,Mod2002024Key.BN1293 ,Mod2002024Key.BN1294 },"2022") 
    ,C04(new Mod2002024Key[]{Mod2002024Key.BN1295 ,Mod2002024Key.BN1296 ,Mod2002024Key.BN1297 },"2024(*)")
    ,C05(new Mod2002024Key[]{Mod2002024Key.BN2312 ,Mod2002024Key.BN2313 ,Mod2002024Key.BN2353 },"2024")
    ,C06(new Mod2002024Key[]{Mod2002024Key.BN1298 ,Mod2002024Key.BN2315 ,Mod2002024Key.BN1304 },"Total")    
	;	
	 
    private String description;
    private Mod2002024Key[] keys;
    
	private Mod2002024BN2315Key(Mod2002024Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}

	public Mod2002024Key[] getKeys() {
		return keys;
	}
	
	public String getDescription() {
		return description;
	}

}

