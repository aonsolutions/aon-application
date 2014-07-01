package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public enum Mod200BN570Key implements Serializable, IsSerializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C0001(new Mod200Key[]{Mod200Key.BN101,Mod200Key.BN102,Mod200Key.BN696,Mod200Key.BN697,null           },"D.I. interna 2006")
	,C0002(new Mod200Key[]{Mod200Key.BN104,Mod200Key.BN105,Mod200Key.BN846,Mod200Key.BN847,Mod200Key.BN848},"D.I. interna 2007")
	,C0003(new Mod200Key[]{Mod200Key.BN106,Mod200Key.BN107,Mod200Key.BN282,Mod200Key.BN283,Mod200Key.BN284},"D.I. interna 2008")
	,C0004(new Mod200Key[]{Mod200Key.BN108,Mod200Key.BN109,Mod200Key.BN702,Mod200Key.BN703,Mod200Key.BN707},"D.I. interna 2009")
	,C0005(new Mod200Key[]{Mod200Key.BN110,Mod200Key.BN111,Mod200Key.BN071,Mod200Key.BN187,Mod200Key.BN300},"D.I. interna 2010")
	,C0006(new Mod200Key[]{Mod200Key.BN112,Mod200Key.BN113,Mod200Key.BN025,Mod200Key.BN026,Mod200Key.BN027},"D.I. interna 2011")
	,C0007(new Mod200Key[]{Mod200Key.BN114,Mod200Key.BN115,Mod200Key.BN714,Mod200Key.BN715,Mod200Key.BN716},"D.I. interna 2012")
	,C0008(new Mod200Key[]{Mod200Key.BN735,Mod200Key.BN920,Mod200Key.BN736,Mod200Key.BN737,Mod200Key.BN738},"D.I. interna 2013")
	,C0009(new Mod200Key[]{Mod200Key.BN116,Mod200Key.BN103,Mod200Key.BN117,null           ,Mod200Key.BN118},"Total 2006-2013")
	;
	 
    private String description;
    
    private Mod200Key[] keys;
    
	private Mod200BN570Key(Mod200Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod200Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

