package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Pendiente de adición por límite beneficio operativo no aplicado
public enum Mod2002024LM538Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002024Key[]{Mod2002024Key.LM1741,Mod2002024Key.LM1742,null                },"2018")
	,C02(new Mod2002024Key[]{Mod2002024Key.LM1982,Mod2002024Key.LM1983,Mod2002024Key.LM1984},"2019")
	,C03(new Mod2002024Key[]{Mod2002024Key.LM2258,Mod2002024Key.LM2259,Mod2002024Key.LM2260},"2020")
	,C04(new Mod2002024Key[]{Mod2002024Key.LM2404,Mod2002024Key.LM2405,Mod2002024Key.LM2406},"2021")
	,C05(new Mod2002024Key[]{Mod2002024Key.LM1103,Mod2002024Key.LM1104,Mod2002024Key.LM1105},"2022")
	,C06(new Mod2002024Key[]{Mod2002024Key.LM1398,Mod2002024Key.LM1399,Mod2002024Key.LM1400},"2024(*)")	
	,C07(new Mod2002024Key[]{Mod2002024Key.LM2769,Mod2002024Key.LM2770,Mod2002024Key.LM2772},"2024(**)")	
	,C08(new Mod2002024Key[]{Mod2002024Key.LM538 ,Mod2002024Key.LM539 ,Mod2002024Key.LM546 },"Total")
	;
	 
    private String description;
    private Mod2002024Key[] keys;

	private Mod2002024LM538Key(Mod2002024Key[] keys, String description) {
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

