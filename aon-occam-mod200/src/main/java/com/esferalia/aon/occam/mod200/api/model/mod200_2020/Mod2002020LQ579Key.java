package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen de las entidades navieras en función del tonelaje (Desglose casilla 579)
public enum Mod2002020LQ579Key implements Serializable, IMod200KeysProvider  {
	 C0001(Mod2002020Key.LQ0N1,Mod2002020Key.LQ0N1.getDescription())
	,C0002(Mod2002020Key.LQ630,Mod2002020Key.LQ630.getDescription())
	,C0003(Mod2002020Key.LQ631,Mod2002020Key.LQ631.getDescription())
	,C0004(Mod2002020Key.LQ632,Mod2002020Key.LQ632.getDescription())
	;
	 
    private String description;
    private Mod2002020Key key;
    private Mod2002020Key[] keys;

	private Mod2002020LQ579Key(Mod2002020Key key, String description) {
		this.key = key;
		this.description = description;
		keys = new Mod2002020Key[]{key};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002020Key getKey() {
		return key;
	}
	
	@Override
	public Mod2002020Key[] getKeys() {
		return keys; 
	}

}

