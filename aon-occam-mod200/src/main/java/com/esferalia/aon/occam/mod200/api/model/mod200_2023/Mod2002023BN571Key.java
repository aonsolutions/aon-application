package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición internacional LIS. DI internac. períodos anteriores
public enum Mod2002023BN571Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002023Key[]{Mod2002023Key.BN1054,Mod2002023Key.BN1050,Mod2002023Key.BN1051,Mod2002023Key.BN1052,Mod2002023Key.BN1053},"DI internacional 2015")
	,C02(new Mod2002023Key[]{Mod2002023Key.BN1348,Mod2002023Key.BN1349,Mod2002023Key.BN1350,Mod2002023Key.BN1351,Mod2002023Key.BN1352},"DI internacional 2016")
	,C03(new Mod2002023Key[]{Mod2002023Key.BN1770,Mod2002023Key.BN1771,Mod2002023Key.BN1772,Mod2002023Key.BN1773,Mod2002023Key.BN1774},"DI internacional 2017")
	,C04(new Mod2002023Key[]{Mod2002023Key.BN1833,Mod2002023Key.BN1834,Mod2002023Key.BN1835,Mod2002023Key.BN1836,Mod2002023Key.BN1837},"DI internacional 2018")
	,C05(new Mod2002023Key[]{Mod2002023Key.BN2201,Mod2002023Key.BN2202,Mod2002023Key.BN2203,Mod2002023Key.BN2204,Mod2002023Key.BN2205},"DI internacional 2019")
	,C06(new Mod2002023Key[]{Mod2002023Key.BN2324,Mod2002023Key.BN2325,Mod2002023Key.BN2326,Mod2002023Key.BN2327,Mod2002023Key.BN2328},"DI internacional 2020")
	,C07(new Mod2002023Key[]{Mod2002023Key.BN207 ,Mod2002023Key.BN208 ,Mod2002023Key.BN209 ,Mod2002023Key.BN212 ,Mod2002023Key.BN213 },"DI internacional 2021")
	,C08(new Mod2002023Key[]{Mod2002023Key.BN490 ,Mod2002023Key.BN491 ,Mod2002023Key.BN492 ,Mod2002023Key.BN493 ,Mod2002023Key.BN620 },"DI internacional 2022")
	,C09(new Mod2002023Key[]{Mod2002023Key.BN1361,Mod2002023Key.BN1362,Mod2002023Key.BN1457,Mod2002023Key.BN1472,Mod2002023Key.BN1505},"DI internacional 2023(*)")
	,C10(new Mod2002023Key[]{Mod2002023Key.BN131 ,null 				  ,Mod2002023Key.BN132 ,Mod2002023Key.BN571 ,Mod2002023Key.BN133 },"Total")
	,C11(new Mod2002023Key[]{null  				 ,Mod2002023Key.BN103D,null  			   ,null  				,null  				 },"Tipo de gravamen 2023")
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023BN571Key(Mod2002023Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002023Key[] getKeys() {
		return keys;
	}


}

