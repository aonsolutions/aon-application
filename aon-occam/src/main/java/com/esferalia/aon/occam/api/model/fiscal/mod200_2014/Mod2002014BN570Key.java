package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014BN570Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C0001(new Mod2002014Key[]{Mod2002014Key.BN101,Mod2002014Key.BN102,Mod2002014Key.BN696,Mod2002014Key.BN697,null           },"D.I. interna 2006")
	,C0002(new Mod2002014Key[]{Mod2002014Key.BN104,Mod2002014Key.BN105,Mod2002014Key.BN846,Mod2002014Key.BN847,Mod2002014Key.BN848},"D.I. interna 2007")
	,C0003(new Mod2002014Key[]{Mod2002014Key.BN106,Mod2002014Key.BN107,Mod2002014Key.BN282,Mod2002014Key.BN283,Mod2002014Key.BN284},"D.I. interna 2008")
	,C0004(new Mod2002014Key[]{Mod2002014Key.BN108,Mod2002014Key.BN109,Mod2002014Key.BN702,Mod2002014Key.BN703,Mod2002014Key.BN707},"D.I. interna 2009")
	,C0005(new Mod2002014Key[]{Mod2002014Key.BN110,Mod2002014Key.BN111,Mod2002014Key.BN071,Mod2002014Key.BN187,Mod2002014Key.BN300},"D.I. interna 2010")
	,C0006(new Mod2002014Key[]{Mod2002014Key.BN112,Mod2002014Key.BN113,Mod2002014Key.BN025,Mod2002014Key.BN026,Mod2002014Key.BN027},"D.I. interna 2011")
	,C0007(new Mod2002014Key[]{Mod2002014Key.BN114,Mod2002014Key.BN115,Mod2002014Key.BN714,Mod2002014Key.BN715,Mod2002014Key.BN716},"D.I. interna 2012")
	,C0008(new Mod2002014Key[]{Mod2002014Key.BN735,Mod2002014Key.BN920,Mod2002014Key.BN736,Mod2002014Key.BN737,Mod2002014Key.BN738},"D.I. interna 2013")
	,C0009(new Mod2002014Key[]{Mod2002014Key.BN116,Mod2002014Key.BN103,Mod2002014Key.BN117,null           ,Mod2002014Key.BN118},"Total 2006-2013")
	;
	 
    private String description;
    
    private Mod2002014Key[] keys;
    
	private Mod2002014BN570Key(Mod2002014Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002014Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

