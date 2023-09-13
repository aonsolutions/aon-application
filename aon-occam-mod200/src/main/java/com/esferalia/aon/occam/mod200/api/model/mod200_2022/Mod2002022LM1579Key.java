package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
public enum Mod2002022LM1579Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002022Key[]{Mod2002022Key.LM2277, Mod2002022Key.LM2278, Mod2002022Key.LM2279, null                },"2020")
	,C02(new Mod2002022Key[]{Mod2002022Key.LM2427, Mod2002022Key.LM2428, Mod2002022Key.LM2429, Mod2002022Key.LM2430},"2021")
	,C03(new Mod2002022Key[]{Mod2002022Key.LM1134, Mod2002022Key.LM1135, Mod2002022Key.LM1136, Mod2002022Key.LM1138},"2022(*)")
	,C04(new Mod2002022Key[]{Mod2002022Key.LM1423, Mod2002022Key.LM1424, Mod2002022Key.LM1425, Mod2002022Key.LM1469},"2022")
	,C05(new Mod2002022Key[]{Mod2002022Key.LM1579, Mod2002022Key.LM1580, Mod2002022Key.LM1581, Mod2002022Key.LM1582},"Total")		 
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022LM1579Key(Mod2002022Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002022Key[] getKeys() {
		return keys; 
	}
	
}

