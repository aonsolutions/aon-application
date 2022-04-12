package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014LQ579Key implements Serializable, IMod200KeysProvider  {
	 C0001(Mod2002014Key.LQ0N1,Mod2002014Key.LQ0N1.getDescription())
	,C0002(Mod2002014Key.LQ630,Mod2002014Key.LQ630.getDescription())
	,C0003(Mod2002014Key.LQ631,Mod2002014Key.LQ631.getDescription())
	,C0004(Mod2002014Key.LQ632,Mod2002014Key.LQ632.getDescription())
	;
	 
    private String description;
    private Mod2002014Key key;
    private Mod2002014Key[] keys;

	private Mod2002014LQ579Key(Mod2002014Key key, String description) {
		this.key = key;
		this.description = description;
		keys = new Mod2002014Key[]{key};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002014Key getKey() {
		return key;
	}
	
	@Override
	public Mod2002014Key[] getKeys() {
		return keys; 
	}
}

