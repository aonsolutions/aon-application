package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en las Illes Balears (DA 70 Ley 31/2022) - RIIB
public enum Mod2002025RIIB_1Key implements Serializable, IMod200KeysProvider {
	
	 C01(new Mod2002025Key[]{Mod2002025Key.RB1707, Mod2002025Key.RB2914, Mod2002025Key.RB2915, null				   , Mod2002025Key.RB1936, Mod2002025Key.RB2917},"RIIB 2023")
	,C02(new Mod2002025Key[]{null				 , null				   , null				 , Mod2002025Key.RB1708, null				 , Mod2002025Key.RB1709},"RIIB 2025")
	;
	 
    private String description;
    private Mod2002025Key[] keys;
    
	private Mod2002025RIIB_1Key(Mod2002025Key[] keys, String description) {
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

