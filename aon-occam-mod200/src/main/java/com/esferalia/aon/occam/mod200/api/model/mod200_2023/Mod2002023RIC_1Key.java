package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - RIC
public enum Mod2002023RIC_1Key implements Serializable, IMod200KeysProvider {
	
	 C01(new Mod2002023Key[]{Mod2002023Key.RC524 ,Mod2002023Key.RC525 ,Mod2002023Key.RC526 ,Mod2002023Key.RC2439,null                },"RIC 2018")
	,C02(new Mod2002023Key[]{Mod2002023Key.RC922 ,Mod2002023Key.RC923 ,Mod2002023Key.RC924 ,Mod2002023Key.RC2440,Mod2002023Key.RC925 },"RIC 2019")
	,C03(new Mod2002023Key[]{Mod2002023Key.RC1165,Mod2002023Key.RC928 ,Mod2002023Key.RC938 ,Mod2002023Key.RC2441,Mod2002023Key.RC996 },"RIC 2020")
	,C04(new Mod2002023Key[]{Mod2002023Key.RC1744,Mod2002023Key.RC1168,Mod2002023Key.RC1172,Mod2002023Key.RC1174,Mod2002023Key.RC1175},"RIC 2021")
	,C05(new Mod2002023Key[]{Mod2002023Key.RC2807,Mod2002023Key.RC1745,Mod2002023Key.RC1746,Mod2002023Key.RC1820,Mod2002023Key.RC1821},"RIC 2022")
	,C06(new Mod2002023Key[]{null				 ,Mod2002023Key.RC2808,Mod2002023Key.RC2809,Mod2002023Key.RC2810,Mod2002023Key.RC2822},"RIC 2023")
	;
	 
    private String description;
    private Mod2002023Key[] keys;
    
	private Mod2002023RIC_1Key(Mod2002023Key[] keys, String description) {
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

