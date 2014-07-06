package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public enum Mod200BN579Key implements Serializable, IsSerializable, IMod200KeysProvider  {
	 C0001(Mod200Key.LQ0N1,Mod200Key.LQ0N1.getDescription())
	,C0002(Mod200Key.LQ630,Mod200Key.LQ630.getDescription())
	,C0003(Mod200Key.LQ631,Mod200Key.LQ631.getDescription())
	,C0004(Mod200Key.LQ632,Mod200Key.LQ632.getDescription())
	;
	 
    private String description;
    private Mod200Key key;
    private Mod200Key[] keys;

	private Mod200BN579Key(Mod200Key key, String description) {
		this.key = key;
		this.description = description;
		keys = new Mod200Key[]{key};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod200Key getKey() {
		return key;
	}
	
	@Override
	public Mod200Key[] getKeys() {
		return keys; 
	}
}

