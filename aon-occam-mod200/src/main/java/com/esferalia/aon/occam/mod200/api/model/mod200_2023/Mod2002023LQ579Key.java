package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen de las entidades navieras en función del tonelaje (Desglose casilla 579)
public enum Mod2002023LQ579Key implements Serializable, IMod200KeysProvider  {
	 C01(Mod2002023Key.LQ0N1,Mod2002023Key.LQ0N1.getDescription())
	,C02(Mod2002023Key.LQ630,Mod2002023Key.LQ630.getDescription())
	,C03(Mod2002023Key.LQ631,Mod2002023Key.LQ631.getDescription())
	,C04(Mod2002023Key.LQ632,Mod2002023Key.LQ632.getDescription())
	;
	 
    private String description;
    private Mod2002023Key key;
    private Mod2002023Key[] keys;

	private Mod2002023LQ579Key(Mod2002023Key key, String description) {
		this.key = key;
		this.description = description;
		keys = new Mod2002023Key[]{key};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002023Key getKey() {
		return key;
	}
	
	@Override
	public Mod2002023Key[] getKeys() {
		return keys; 
	}

}