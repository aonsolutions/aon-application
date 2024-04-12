package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en las Illes Balears (DA 70 Ley 31/2022) - RIIB
public enum Mod2002023RIIB_1Key implements Serializable, IMod200KeysProvider {
	
	C01(new Mod2002023Key[]{null,Mod2002023Key.RB2914,Mod2002023Key.RB2915,Mod2002023Key.RB2916,Mod2002023Key.RB2917},"RIIB 2023")
	;
	 
    private String description;
    private Mod2002023Key[] keys;
    
	private Mod2002023RIIB_1Key(Mod2002023Key[] keys, String description) {
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

