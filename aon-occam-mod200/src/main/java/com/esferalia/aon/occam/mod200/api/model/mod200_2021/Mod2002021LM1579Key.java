package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
public enum Mod2002021LM1579Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002021Key[]{Mod2002021Key.LM2109, Mod2002021Key.LM2110, Mod2002021Key.LM2111, null                },"2019")
	,C02(new Mod2002021Key[]{Mod2002021Key.LM2277, Mod2002021Key.LM2278, Mod2002021Key.LM2279, Mod2002021Key.LM2280},"2020")
	,C03(new Mod2002021Key[]{Mod2002021Key.LM2427, Mod2002021Key.LM2428, Mod2002021Key.LM2429, Mod2002021Key.LM2430},"2021(*)")
	,C04(new Mod2002021Key[]{Mod2002021Key.LM1134, Mod2002021Key.LM1135, Mod2002021Key.LM1136, Mod2002021Key.LM1138},"2021")
	,C05(new Mod2002021Key[]{Mod2002021Key.LM1579, Mod2002021Key.LM1580, Mod2002021Key.LM1581, Mod2002021Key.LM1582},"Total")		 
	;
	 
    private String description;
    private Mod2002021Key[] keys;

	private Mod2002021LM1579Key(Mod2002021Key[] keys, String description) {
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

