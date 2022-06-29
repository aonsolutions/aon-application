package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Pendiente de adición por límite beneficio operativo no aplicado
public enum Mod2002021LM538Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002021Key[]{Mod2002021Key.LM1217,Mod2002021Key.LM1218,null                },"2016")
	,C02(new Mod2002021Key[]{Mod2002021Key.LM1467,Mod2002021Key.LM1468,Mod2002021Key.LM1469},"2017")
	,C03(new Mod2002021Key[]{Mod2002021Key.LM1741,Mod2002021Key.LM1742,Mod2002021Key.LM1743},"2018")
	,C04(new Mod2002021Key[]{Mod2002021Key.LM1982,Mod2002021Key.LM1983,Mod2002021Key.LM1984},"2019")
	,C05(new Mod2002021Key[]{Mod2002021Key.LM2258,Mod2002021Key.LM2259,Mod2002021Key.LM2260},"2020")
	,C06(new Mod2002021Key[]{Mod2002021Key.LM2404,Mod2002021Key.LM2405,Mod2002021Key.LM2406},"2021(*)")
	,C07(new Mod2002021Key[]{Mod2002021Key.LM1103,Mod2002021Key.LM1104,Mod2002021Key.LM1105},"2021(**)")
	,C08(new Mod2002021Key[]{Mod2002021Key.LM538 ,Mod2002021Key.LM539 ,Mod2002021Key.LM546 },"Total")
	;
	 
    private String description;
    private Mod2002021Key[] keys;

	private Mod2002021LM538Key(Mod2002021Key[] keys, String description) {
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

