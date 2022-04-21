package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - RIC
public enum Mod2002021RIC_1Key implements Serializable, IMod200KeysProvider {
	
	 C01(new Mod2002021Key[]{Mod2002021Key.RC089 ,Mod2002021Key.RC094 ,Mod2002021Key.RC095 ,Mod2002021Key.RC2437,null                },"RIC 2016")
	,C02(new Mod2002021Key[]{Mod2002021Key.RC097 ,Mod2002021Key.RC098 ,Mod2002021Key.RC047 ,Mod2002021Key.RC2438,null                },"RIC 2017")
	,C03(new Mod2002021Key[]{Mod2002021Key.RC524 ,Mod2002021Key.RC525 ,Mod2002021Key.RC526 ,Mod2002021Key.RC2439,Mod2002021Key.RC527 },"RIC 2018")
	,C04(new Mod2002021Key[]{Mod2002021Key.RC922 ,Mod2002021Key.RC923 ,Mod2002021Key.RC924 ,Mod2002021Key.RC2440,Mod2002021Key.RC925 },"RIC 2019")
	,C05(new Mod2002021Key[]{Mod2002021Key.RC1165,Mod2002021Key.RC928 ,Mod2002021Key.RC938 ,Mod2002021Key.RC2441,Mod2002021Key.RC996 },"RIC 2020")
	,C06(new Mod2002021Key[]{null				 ,Mod2002021Key.RC1168,Mod2002021Key.RC1172,Mod2002021Key.RC1174,Mod2002021Key.RC1175},"RIC 2021")
	;
	 
    private String description;
    private Mod2002021Key[] keys;
    
	private Mod2002021RIC_1Key(Mod2002021Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002021Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}

	
}

