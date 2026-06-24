package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en las Illes Balears (DA 70 Ley 31/2022) - Inversiones anticipadas
public enum Mod2002025RIIB_2Key implements Serializable, IMod200KeysProvider {

	 C01(new Mod2002025Key[]{Mod2002025Key.RB2362, null                , null                , Mod2002025Key.RB2941},"Inversiones anticipadas 2023")
	,C02(new Mod2002025Key[]{Mod2002025Key.RB3642, null                , null                , Mod2002025Key.RB2374},"Inversiones anticipadas 2024")
	,C03(new Mod2002025Key[]{null                , Mod2002025Key.RB3643, Mod2002025Key.RB3644, Mod2002025Key.RB3645},"Inversiones anticipadas 2025")
	;
	 
    private String description;
    private Mod2002025Key[] keys;
    
	private Mod2002025RIIB_2Key(Mod2002025Key[] keys, String description) {
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
