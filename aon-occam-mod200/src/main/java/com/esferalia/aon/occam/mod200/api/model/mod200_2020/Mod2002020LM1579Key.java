package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
public enum Mod2002020LM1579Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002020Key[]{Mod2002020Key.LM1763, Mod2002020Key.LM1764, Mod2002020Key.LM1765, null                },"2018")
	,C02(new Mod2002020Key[]{Mod2002020Key.LM2109, Mod2002020Key.LM2110, Mod2002020Key.LM2111, Mod2002020Key.LM2112},"2019")
	,C03(new Mod2002020Key[]{Mod2002020Key.LM2277, Mod2002020Key.LM2278, Mod2002020Key.LM2279, Mod2002020Key.LM2280},"2020(*)")
	,C04(new Mod2002020Key[]{Mod2002020Key.LM2427, Mod2002020Key.LM2428, Mod2002020Key.LM2429, Mod2002020Key.LM2430},"2020")		 
	,C05(new Mod2002020Key[]{Mod2002020Key.LM1579, Mod2002020Key.LM1580, Mod2002020Key.LM1581, Mod2002020Key.LM1582},"Total")		 
	
	;
	 
    private String description;
    private Mod2002020Key[] keys;

	private Mod2002020LM1579Key(Mod2002020Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002020Key[] getKeys() {
		return keys; 
	}
	
}

