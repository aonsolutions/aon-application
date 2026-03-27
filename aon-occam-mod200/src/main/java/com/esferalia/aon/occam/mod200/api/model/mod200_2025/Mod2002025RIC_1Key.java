package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - RIC
public enum Mod2002025RIC_1Key implements Serializable, IMod200KeysProvider {
	
	 C01(new Mod2002025Key[]{Mod2002025Key.RC1165,Mod2002025Key.RC928 ,Mod2002025Key.RC938 ,null                ,Mod2002025Key.RC1985,null                },"RIC 2020")
	,C02(new Mod2002025Key[]{Mod2002025Key.RC1744,Mod2002025Key.RC1168,Mod2002025Key.RC1172,null                ,Mod2002025Key.RC1986,Mod2002025Key.RC1175},"RIC 2021")
	,C03(new Mod2002025Key[]{Mod2002025Key.RC2807,Mod2002025Key.RC1745,Mod2002025Key.RC1746,null                ,Mod2002025Key.RC2430,Mod2002025Key.RC1821},"RIC 2022")
	,C04(new Mod2002025Key[]{Mod2002025Key.RC2975,Mod2002025Key.RC2808,Mod2002025Key.RC2809,null                ,Mod2002025Key.RC2977,Mod2002025Key.RC2822},"RIC 2023")
	,C05(new Mod2002025Key[]{Mod2002025Key.RC3623,Mod2002025Key.RC3624,Mod2002025Key.RC3625,null                ,Mod2002025Key.RC3626,Mod2002025Key.RC3313},"RIC 2024")
	,C06(new Mod2002025Key[]{null				 ,null                ,null                ,Mod2002025Key.RC3627,null                ,Mod2002025Key.RC3628},"RIC 2025")
	;
	 
    private String description;
    private Mod2002025Key[] keys;
    
	private Mod2002025RIC_1Key(Mod2002025Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002025Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}

	
}

