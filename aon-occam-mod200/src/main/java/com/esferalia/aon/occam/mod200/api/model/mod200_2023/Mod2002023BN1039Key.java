package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Desglose Casilla 1039
// Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS)
public enum Mod2002023BN1039Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002023Key[]{Mod2002023Key.BN1931 ,Mod2002023Key.BN1932 ,Mod2002023Key.BN1933 ,Mod2002023Key.BN1937},"2015")    
	,C02(new Mod2002023Key[]{Mod2002023Key.BN1938 ,Mod2002023Key.BN1939 ,Mod2002023Key.BN1940 ,Mod2002023Key.BN1941},"2016")    
	,C03(new Mod2002023Key[]{Mod2002023Key.BN1942 ,Mod2002023Key.BN1943 ,Mod2002023Key.BN1944 ,Mod2002023Key.BN1945},"2017")    
	,C04(new Mod2002023Key[]{Mod2002023Key.BN1946 ,Mod2002023Key.BN1947 ,Mod2002023Key.BN1948 ,Mod2002023Key.BN1949},"2018")    
	,C05(new Mod2002023Key[]{Mod2002023Key.BN2109 ,Mod2002023Key.BN2110 ,Mod2002023Key.BN2111 ,Mod2002023Key.BN2112},"2019")    
	,C06(new Mod2002023Key[]{Mod2002023Key.BN2128 ,Mod2002023Key.BN2129 ,Mod2002023Key.BN2130 ,Mod2002023Key.BN2131},"2020")    
	,C07(new Mod2002023Key[]{Mod2002023Key.BN2132 ,Mod2002023Key.BN2133 ,Mod2002023Key.BN2134 ,Mod2002023Key.BN2135},"2021")    
	,C08(new Mod2002023Key[]{Mod2002023Key.BN2136 ,Mod2002023Key.BN2137 ,Mod2002023Key.BN2138 ,Mod2002023Key.BN2139},"2023(*)") 
	,C09(new Mod2002023Key[]{Mod2002023Key.BN2140 ,Mod2002023Key.BN2141 ,Mod2002023Key.BN2142 ,Mod2002023Key.BN2143},"2023")    
	,C10(new Mod2002023Key[]{Mod2002023Key.BN2144 ,Mod2002023Key.BN1039 ,Mod2002023Key.BN1892 ,Mod2002023Key.BN2147},"Total")   
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023BN1039Key(Mod2002023Key[] keys, String description) {
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

