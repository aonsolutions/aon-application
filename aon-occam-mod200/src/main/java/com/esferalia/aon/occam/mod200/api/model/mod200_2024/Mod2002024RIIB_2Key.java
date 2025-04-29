package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en las Illes Balears (DA 70 Ley 31/2022) - Inversiones anticipadas
public enum Mod2002024RIIB_2Key implements Serializable, IMod200KeysProvider {

	C01(new Mod2002024Key[]{null,Mod2002024Key.RB2919,Mod2002024Key.RB2920,Mod2002024Key.RB2941},"Inversiones anticipadas 2024")
	;
	 
    private String description;
    private Mod2002024Key[] keys;
    
	private Mod2002024RIIB_2Key(Mod2002024Key[] keys, String description) {
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
