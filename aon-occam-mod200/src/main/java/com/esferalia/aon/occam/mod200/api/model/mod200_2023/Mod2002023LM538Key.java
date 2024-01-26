package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Pendiente de adición por límite beneficio operativo no aplicado
public enum Mod2002023LM538Key implements Serializable, IMod200KeysProvider  {

 	 C01(new Mod2002023Key[]{Mod2002023Key.LM1467,Mod2002023Key.LM1468,null                },"2017")
	,C02(new Mod2002023Key[]{Mod2002023Key.LM1741,Mod2002023Key.LM1742,Mod2002023Key.LM1743},"2018")
	,C03(new Mod2002023Key[]{Mod2002023Key.LM1982,Mod2002023Key.LM1983,Mod2002023Key.LM1984},"2019")
	,C04(new Mod2002023Key[]{Mod2002023Key.LM2258,Mod2002023Key.LM2259,Mod2002023Key.LM2260},"2020")
	,C05(new Mod2002023Key[]{Mod2002023Key.LM2404,Mod2002023Key.LM2405,Mod2002023Key.LM2406},"2021")
	,C06(new Mod2002023Key[]{Mod2002023Key.LM1103,Mod2002023Key.LM1104,Mod2002023Key.LM1105},"2023(*)")
	,C07(new Mod2002023Key[]{Mod2002023Key.LM1398,Mod2002023Key.LM1399,Mod2002023Key.LM1400},"2023(**)")
	,C08(new Mod2002023Key[]{Mod2002023Key.LM538 ,Mod2002023Key.LM539 ,Mod2002023Key.LM546 },"Total")
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023LM538Key(Mod2002023Key[] keys, String description) {
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

