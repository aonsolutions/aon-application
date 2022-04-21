package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición internacional LIS. DI internac. períodos anteriores
public enum Mod2002020BN571Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002020Key[]{Mod2002020Key.BN1054,Mod2002020Key.BN1050,Mod2002020Key.BN1051,Mod2002020Key.BN1052,Mod2002020Key.BN1053},"DI internacional 2015")
	,C02(new Mod2002020Key[]{Mod2002020Key.BN1348,Mod2002020Key.BN1349,Mod2002020Key.BN1350,Mod2002020Key.BN1351,Mod2002020Key.BN1352},"DI internacional 2016")
	,C03(new Mod2002020Key[]{Mod2002020Key.BN1770,Mod2002020Key.BN1771,Mod2002020Key.BN1772,Mod2002020Key.BN1773,Mod2002020Key.BN1774},"DI internacional 2017")
	,C04(new Mod2002020Key[]{Mod2002020Key.BN1833,Mod2002020Key.BN1834,Mod2002020Key.BN1835,Mod2002020Key.BN1836,Mod2002020Key.BN1837},"DI internacional 2018")
	,C05(new Mod2002020Key[]{Mod2002020Key.BN2201,Mod2002020Key.BN2202,Mod2002020Key.BN2203,Mod2002020Key.BN2204,Mod2002020Key.BN2205},"DI internacional 2019")
	,C06(new Mod2002020Key[]{Mod2002020Key.BN2324,Mod2002020Key.BN2325,Mod2002020Key.BN2326,Mod2002020Key.BN2327,Mod2002020Key.BN2328},"DI internacional 2020(*)")
	,C07(new Mod2002020Key[]{Mod2002020Key.BN131 ,null 				  ,Mod2002020Key.BN132 ,Mod2002020Key.BN571 ,Mod2002020Key.BN133 },"Total")
	,C08(new Mod2002020Key[]{null  				 ,Mod2002020Key.BN103D,null  			   ,null  				,null  				 },"Tipo de gravamen 2020")
	;
	 
    private String description;
    private Mod2002020Key[] keys;

	private Mod2002020BN571Key(Mod2002020Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002020Key[] getKeys() {
		return keys;
	}


}

