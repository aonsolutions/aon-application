package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;


public enum Mod2002013LQ579Key implements Serializable, IMod200KeysProvider  {
	 C0001(Mod2002013Key.LQ0N1,Mod2002013Key.LQ0N1.getDescription())
	,C0002(Mod2002013Key.LQ630,Mod2002013Key.LQ630.getDescription())
	,C0003(Mod2002013Key.LQ631,Mod2002013Key.LQ631.getDescription())
	,C0004(Mod2002013Key.LQ632,Mod2002013Key.LQ632.getDescription())
	;
	 
    private String description;
    private Mod2002013Key key;
    private Mod2002013Key[] keys;

	private Mod2002013LQ579Key(Mod2002013Key key, String description) {
		this.key = key;
		this.description = description;
		keys = new Mod2002013Key[]{key};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002013Key getKey() {
		return key;
	}
	
	@Override
	public Mod2002013Key[] getKeys() {
		return keys; 
	}
}

