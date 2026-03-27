package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Desglose Casilla 2314
// Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994)
public enum Mod2002025BN2314Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002025Key[]{Mod2002025Key.BN2148 ,Mod2002025Key.BN2149 ,Mod2002025Key.BN2150 ,Mod2002025Key.BN2151},"2015")    
	,C02(new Mod2002025Key[]{Mod2002025Key.BN2152 ,Mod2002025Key.BN2153 ,Mod2002025Key.BN2154 ,Mod2002025Key.BN2155},"2016")    
	,C03(new Mod2002025Key[]{Mod2002025Key.BN2156 ,Mod2002025Key.BN2157 ,Mod2002025Key.BN2158 ,Mod2002025Key.BN2159},"2017")    
	,C04(new Mod2002025Key[]{Mod2002025Key.BN2160 ,Mod2002025Key.BN2161 ,Mod2002025Key.BN2162 ,Mod2002025Key.BN2163},"2018")    
	,C05(new Mod2002025Key[]{Mod2002025Key.BN2164 ,Mod2002025Key.BN2165 ,Mod2002025Key.BN2166 ,Mod2002025Key.BN2167},"2019")    
	,C06(new Mod2002025Key[]{Mod2002025Key.BN2168 ,Mod2002025Key.BN2169 ,Mod2002025Key.BN2170 ,Mod2002025Key.BN2171},"2020")    
	,C07(new Mod2002025Key[]{Mod2002025Key.BN2172 ,Mod2002025Key.BN2173 ,Mod2002025Key.BN2174 ,Mod2002025Key.BN2175},"2021")    
	,C08(new Mod2002025Key[]{Mod2002025Key.BN1309 ,Mod2002025Key.BN1310 ,Mod2002025Key.BN1311 ,Mod2002025Key.BN1312},"2022") 
	,C09(new Mod2002025Key[]{Mod2002025Key.BN1313 ,Mod2002025Key.BN1314 ,Mod2002025Key.BN1315 ,Mod2002025Key.BN1316},"2023")
	,C10(new Mod2002025Key[]{Mod2002025Key.BN2445 ,Mod2002025Key.BN2466 ,Mod2002025Key.BN2467 ,Mod2002025Key.BN2468},"2024")
	,C11(new Mod2002025Key[]{Mod2002025Key.BN349  ,Mod2002025Key.BN350  ,Mod2002025Key.BN366  ,Mod2002025Key.BN367 },"2025(*)")
	,C12(new Mod2002025Key[]{Mod2002025Key.BN3539 ,Mod2002025Key.BN3540 ,Mod2002025Key.BN3541 ,Mod2002025Key.BN3542},"2025")
	,C13(new Mod2002025Key[]{Mod2002025Key.BN1317 ,Mod2002025Key.BN2314 ,Mod2002025Key.BN1319 ,Mod2002025Key.BN1322},"Total")  
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025BN2314Key(Mod2002025Key[] keys, String description) {
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

