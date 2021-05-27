package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import java.io.Serializable;

// Régimen de las entidades navieras en función del tonelaje (Desglose casilla 579)
public enum Mod2002019LQ579Key implements Serializable, IMod200KeysProvider  {
	 C0001(Mod2002019Key.LQ0N1,Mod2002019Key.LQ0N1.getDescription())
	,C0002(Mod2002019Key.LQ630,Mod2002019Key.LQ630.getDescription())
	,C0003(Mod2002019Key.LQ631,Mod2002019Key.LQ631.getDescription())
	,C0004(Mod2002019Key.LQ632,Mod2002019Key.LQ632.getDescription())
	;
	 
    private String description;
    private Mod2002019Key key;
    private Mod2002019Key[] keys;

	private Mod2002019LQ579Key(Mod2002019Key key, String description) {
		this.key = key;
		this.description = description;
		keys = new Mod2002019Key[]{key};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002019Key getKey() {
		return key;
	}
	
	@Override
	public Mod2002019Key[] getKeys() {
		return keys; 
	}
}

