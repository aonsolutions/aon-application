package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
public enum Mod2002024LM1579Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002024Key[]{Mod2002024Key.LM1134, Mod2002024Key.LM1135, Mod2002024Key.LM1136, null                },"2022")
	,C02(new Mod2002024Key[]{Mod2002024Key.LM1423, Mod2002024Key.LM1424, Mod2002024Key.LM1425, Mod2002024Key.LM1469},"2023")	
	,C03(new Mod2002024Key[]{Mod2002024Key.LM2796, Mod2002024Key.LM2797, Mod2002024Key.LM2798, Mod2002024Key.LM2799},"2024(*)")
	,C04(new Mod2002024Key[]{Mod2002024Key.LM817 , Mod2002024Key.LM878 , Mod2002024Key.LM879 , Mod2002024Key.LM882 },"2024")
	,C05(new Mod2002024Key[]{Mod2002024Key.LM1579, Mod2002024Key.LM1580, Mod2002024Key.LM1581, Mod2002024Key.LM1582},"Total")		 
	;
	 
    private String description;
    private Mod2002024Key[] keys;

	private Mod2002024LM1579Key(Mod2002024Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002024Key[] getKeys() {
		return keys; 
	}
	
}

