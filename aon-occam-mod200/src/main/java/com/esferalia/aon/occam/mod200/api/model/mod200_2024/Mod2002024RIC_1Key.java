package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - RIC
public enum Mod2002024RIC_1Key implements Serializable, IMod200KeysProvider {
	
	 C01(new Mod2002024Key[]{Mod2002024Key.RC524 ,Mod2002024Key.RC525 ,Mod2002024Key.RC526 ,Mod2002024Key.RC2439,Mod2002024Key.RC1936,null                },"RIC 2018")
	,C02(new Mod2002024Key[]{Mod2002024Key.RC922 ,Mod2002024Key.RC923 ,Mod2002024Key.RC924 ,Mod2002024Key.RC2440,Mod2002024Key.RC1963,Mod2002024Key.RC925 },"RIC 2019")
	,C03(new Mod2002024Key[]{Mod2002024Key.RC1165,Mod2002024Key.RC928 ,Mod2002024Key.RC938 ,Mod2002024Key.RC2441,Mod2002024Key.RC1985,Mod2002024Key.RC996 },"RIC 2020")
	,C04(new Mod2002024Key[]{Mod2002024Key.RC1744,Mod2002024Key.RC1168,Mod2002024Key.RC1172,Mod2002024Key.RC1174,Mod2002024Key.RC1986,Mod2002024Key.RC1175},"RIC 2021")
	,C05(new Mod2002024Key[]{Mod2002024Key.RC2807,Mod2002024Key.RC1745,Mod2002024Key.RC1746,Mod2002024Key.RC1820,Mod2002024Key.RC2430,Mod2002024Key.RC1821},"RIC 2022")
	,C06(new Mod2002024Key[]{null				 ,Mod2002024Key.RC2808,Mod2002024Key.RC2809,Mod2002024Key.RC2810,null                ,Mod2002024Key.RC2822},"RIC 2024")
	;
	 
    private String description;
    private Mod2002024Key[] keys;
    
	private Mod2002024RIC_1Key(Mod2002024Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002024Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}

	
}

