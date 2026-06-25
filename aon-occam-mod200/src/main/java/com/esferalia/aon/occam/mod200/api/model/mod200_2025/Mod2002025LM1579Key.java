package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
public enum Mod2002025LM1579Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002025Key[]{Mod2002025Key.LM1423, Mod2002025Key.LM1424, Mod2002025Key.LM1425, null                },"2023")	
	,C02(new Mod2002025Key[]{Mod2002025Key.LM2796, Mod2002025Key.LM2797, Mod2002025Key.LM2798, Mod2002025Key.LM2799},"2024")
	,C03(new Mod2002025Key[]{Mod2002025Key.LM817 , Mod2002025Key.LM878 , Mod2002025Key.LM879 , Mod2002025Key.LM882 },"2025(*)")
	,C04(new Mod2002025Key[]{Mod2002025Key.LM3613, Mod2002025Key.LM3614, Mod2002025Key.LM3615, Mod2002025Key.LM3616},"2025")
	,C05(new Mod2002025Key[]{Mod2002025Key.LM1579, Mod2002025Key.LM1580, Mod2002025Key.LM1581, Mod2002025Key.LM1582},"Total")		 
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025LM1579Key(Mod2002025Key[] keys, String description) {
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

