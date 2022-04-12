package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - Inversiones anticipadas
public enum Mod2002020RIC_2Key implements Serializable, IMod200KeysProvider {

	 C01(new Mod2002020Key[]{Mod2002020Key.RC2442,null				  ,null				   	,Mod2002020Key.RC2443},"Inversiones anticipadas 2017")
	,C02(new Mod2002020Key[]{Mod2002020Key.RC2444,null				  ,null				   	,Mod2002020Key.RC2445},"Inversiones anticipadas 2018")
	,C03(new Mod2002020Key[]{Mod2002020Key.RC2446,null				  ,null					,Mod2002020Key.RC2447},"Inversiones anticipadas 2019")
	,C04(new Mod2002020Key[]{null				 ,Mod2002020Key.RC2449,Mod2002020Key.RC2450 ,Mod2002020Key.RC2451},"Inversiones anticipadas 2020")
	;
	 
    private String description;
    private Mod2002020Key[] keys;
    
	private Mod2002020RIC_2Key(Mod2002020Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002020Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}

	
}

