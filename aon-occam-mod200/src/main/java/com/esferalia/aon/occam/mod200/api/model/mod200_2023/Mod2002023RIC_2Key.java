package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - Inversiones anticipadas
public enum Mod2002023RIC_2Key implements Serializable, IMod200KeysProvider {

	 C01(new Mod2002023Key[]{Mod2002023Key.RC2446,null				  ,null				   ,null                },"Inversiones anticipadas 2019")
	,C02(new Mod2002023Key[]{Mod2002023Key.RC1176,null				  ,null			 	   ,Mod2002023Key.RC2451},"Inversiones anticipadas 2020")
	,C03(new Mod2002023Key[]{Mod2002023Key.RC1823,null				  ,null			 	   ,Mod2002023Key.RC1184},"Inversiones anticipadas 2021")	
	,C04(new Mod2002023Key[]{Mod2002023Key.RC2823,Mod2002023Key.RC1523,Mod2002023Key.RC130 ,Mod2002023Key.RC1600},"Inversiones anticipadas 2022")
	,C05(new Mod2002023Key[]{null				 ,Mod2002023Key.RC2824,Mod2002023Key.RC2825,Mod2002023Key.RC2826},"Inversiones anticipadas 2023")
	;
	 
    private String description;
    private Mod2002023Key[] keys;
    
	private Mod2002023RIC_2Key(Mod2002023Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002023Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
	
}
