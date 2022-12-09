package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - Inversiones anticipadas
public enum Mod2002022RIC_2Key implements Serializable, IMod200KeysProvider {

	 C01(new Mod2002022Key[]{Mod2002022Key.RC2442,null				  ,null				   ,Mod2002022Key.RC2443},"Inversiones anticipadas 2017")
	,C02(new Mod2002022Key[]{Mod2002022Key.RC2444,null				  ,null				   ,Mod2002022Key.RC2445},"Inversiones anticipadas 2018")
	,C03(new Mod2002022Key[]{Mod2002022Key.RC2446,null				  ,null				   ,Mod2002022Key.RC2447},"Inversiones anticipadas 2019")
	,C04(new Mod2002022Key[]{Mod2002022Key.RC1176,null				  ,null			 	   ,Mod2002022Key.RC2451},"Inversiones anticipadas 2020")
	,C05(new Mod2002022Key[]{null				 ,Mod2002022Key.RC1177,Mod2002022Key.RC1180,Mod2002022Key.RC1184},"Inversiones anticipadas 2021")
	;
	 
    private String description;
    private Mod2002022Key[] keys;
    
	private Mod2002022RIC_2Key(Mod2002022Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002022Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}

	
}

