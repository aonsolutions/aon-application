package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - RIC
public enum Mod2002020RIC_1Key implements Serializable, IMod200KeysProvider {
	
	 C01(new Mod2002020Key[]{Mod2002020Key.RC089,Mod2002020Key.RC094,Mod2002020Key.RC095,Mod2002020Key.RC2437,Mod2002020Key.RC093},"RIC 2016")
	,C02(new Mod2002020Key[]{Mod2002020Key.RC097,Mod2002020Key.RC098,Mod2002020Key.RC047,Mod2002020Key.RC2438,Mod2002020Key.RC048},"RIC 2017")
	,C03(new Mod2002020Key[]{Mod2002020Key.RC524,Mod2002020Key.RC525,Mod2002020Key.RC526,Mod2002020Key.RC2439,Mod2002020Key.RC527},"RIC 2018")
	,C04(new Mod2002020Key[]{Mod2002020Key.RC922,Mod2002020Key.RC923,Mod2002020Key.RC924,Mod2002020Key.RC2440,Mod2002020Key.RC925},"RIC 2019")
	,C05(new Mod2002020Key[]{null				,Mod2002020Key.RC928,Mod2002020Key.RC938,Mod2002020Key.RC2441,Mod2002020Key.RC996},"RIC 2020")
	;
	 
    private String description;
    private Mod2002020Key[] keys;
    
	private Mod2002020RIC_1Key(Mod2002020Key[] keys, String description) {
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

