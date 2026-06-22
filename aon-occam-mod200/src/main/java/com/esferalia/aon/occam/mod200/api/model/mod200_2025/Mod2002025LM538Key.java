package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Pendiente de adición por límite beneficio operativo no aplicado
public enum Mod2002025LM538Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002025Key[]{Mod2002025Key.LM2258,Mod2002025Key.LM2259,null                },"2020")
	,C02(new Mod2002025Key[]{Mod2002025Key.LM2404,Mod2002025Key.LM2405,Mod2002025Key.LM2406},"2021")
	,C03(new Mod2002025Key[]{Mod2002025Key.LM1103,Mod2002025Key.LM1104,Mod2002025Key.LM1105},"2022")
	,C04(new Mod2002025Key[]{Mod2002025Key.LM1398,Mod2002025Key.LM1399,Mod2002025Key.LM1400},"2023")	
	,C05(new Mod2002025Key[]{Mod2002025Key.LM2769,Mod2002025Key.LM2770,Mod2002025Key.LM2772},"2024")
	,C06(new Mod2002025Key[]{Mod2002025Key.LM2447,Mod2002025Key.LM2465,Mod2002025Key.LM2972},"2025(*)")
	,C07(new Mod2002025Key[]{Mod2002025Key.LM3588,Mod2002025Key.LM3589,Mod2002025Key.LM3590},"2025(**)")
	,C08(new Mod2002025Key[]{Mod2002025Key.LM538 ,Mod2002025Key.LM539 ,Mod2002025Key.LM546 },"Total")
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025LM538Key(Mod2002025Key[] keys, String description) {
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

