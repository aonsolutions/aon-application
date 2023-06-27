package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Pendiente de adición por límite beneficio operativo no aplicado
public enum Mod2002022LM538Key implements Serializable, IMod200KeysProvider  {

 	 C01(new Mod2002022Key[]{Mod2002022Key.LM1467,Mod2002022Key.LM1468,null                },"2017")
	,C02(new Mod2002022Key[]{Mod2002022Key.LM1741,Mod2002022Key.LM1742,Mod2002022Key.LM1743},"2018")
	,C03(new Mod2002022Key[]{Mod2002022Key.LM1982,Mod2002022Key.LM1983,Mod2002022Key.LM1984},"2019")
	,C04(new Mod2002022Key[]{Mod2002022Key.LM2258,Mod2002022Key.LM2259,Mod2002022Key.LM2260},"2020")
	,C05(new Mod2002022Key[]{Mod2002022Key.LM2404,Mod2002022Key.LM2405,Mod2002022Key.LM2406},"2021")
	,C06(new Mod2002022Key[]{Mod2002022Key.LM1103,Mod2002022Key.LM1104,Mod2002022Key.LM1105},"2022(*)")
	,C07(new Mod2002022Key[]{Mod2002022Key.LM1398,Mod2002022Key.LM1399,Mod2002022Key.LM1400},"2022(**)")
	,C08(new Mod2002022Key[]{Mod2002022Key.LM538 ,Mod2002022Key.LM539 ,Mod2002022Key.LM546 },"Total")
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022LM538Key(Mod2002022Key[] keys, String description) {
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

