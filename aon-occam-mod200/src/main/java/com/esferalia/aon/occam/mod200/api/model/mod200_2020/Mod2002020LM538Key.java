package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

// Pendiente de adición por límite beneficio operativo no aplicado
public enum Mod2002020LM538Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002020Key[]{Mod2002020Key.LM955 ,Mod2002020Key.LM956 ,null                },"2015")
	,C02(new Mod2002020Key[]{Mod2002020Key.LM1217,Mod2002020Key.LM1218,Mod2002020Key.LM1219},"2016")
	,C03(new Mod2002020Key[]{Mod2002020Key.LM1467,Mod2002020Key.LM1468,Mod2002020Key.LM1469},"2017")
	,C04(new Mod2002020Key[]{Mod2002020Key.LM1741,Mod2002020Key.LM1742,Mod2002020Key.LM1743},"2018")
	,C05(new Mod2002020Key[]{Mod2002020Key.LM1982,Mod2002020Key.LM1983,Mod2002020Key.LM1984},"2019")
	,C06(new Mod2002020Key[]{Mod2002020Key.LM2258,Mod2002020Key.LM2259,Mod2002020Key.LM2260},"2020(*)")
	,C07(new Mod2002020Key[]{Mod2002020Key.LM2404,Mod2002020Key.LM2405,Mod2002020Key.LM2406},"2020(**)")		 
	,C08(new Mod2002020Key[]{Mod2002020Key.LM538 ,Mod2002020Key.LM539 ,Mod2002020Key.LM546 },"Total")
	;
	 
    private String description;
    private Mod2002020Key[] keys;

	private Mod2002020LM538Key(Mod2002020Key[] keys, String description) {
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

