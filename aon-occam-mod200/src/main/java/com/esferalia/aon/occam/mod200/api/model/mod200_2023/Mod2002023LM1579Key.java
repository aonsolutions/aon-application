package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
public enum Mod2002023LM1579Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002023Key[]{Mod2002023Key.LM2277, Mod2002023Key.LM2278, Mod2002023Key.LM2279, null                },"2020")
	,C02(new Mod2002023Key[]{Mod2002023Key.LM2427, Mod2002023Key.LM2428, Mod2002023Key.LM2429, Mod2002023Key.LM2430},"2021")
	,C03(new Mod2002023Key[]{Mod2002023Key.LM1134, Mod2002023Key.LM1135, Mod2002023Key.LM1136, Mod2002023Key.LM1138},"2023(*)")
	,C04(new Mod2002023Key[]{Mod2002023Key.LM1423, Mod2002023Key.LM1424, Mod2002023Key.LM1425, Mod2002023Key.LM1469},"2023")
	,C05(new Mod2002023Key[]{Mod2002023Key.LM1579, Mod2002023Key.LM1580, Mod2002023Key.LM1581, Mod2002023Key.LM1582},"Total")		 
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023LM1579Key(Mod2002023Key[] keys, String description) {
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

