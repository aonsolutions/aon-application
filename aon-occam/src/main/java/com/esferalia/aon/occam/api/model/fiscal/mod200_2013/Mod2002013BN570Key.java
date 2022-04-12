package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;


public enum Mod2002013BN570Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C0001(new Mod2002013Key[]{Mod2002013Key.BN101,Mod2002013Key.BN102,Mod2002013Key.BN696,Mod2002013Key.BN697,null           },"D.I. interna 2006")
	,C0002(new Mod2002013Key[]{Mod2002013Key.BN104,Mod2002013Key.BN105,Mod2002013Key.BN846,Mod2002013Key.BN847,Mod2002013Key.BN848},"D.I. interna 2007")
	,C0003(new Mod2002013Key[]{Mod2002013Key.BN106,Mod2002013Key.BN107,Mod2002013Key.BN282,Mod2002013Key.BN283,Mod2002013Key.BN284},"D.I. interna 2008")
	,C0004(new Mod2002013Key[]{Mod2002013Key.BN108,Mod2002013Key.BN109,Mod2002013Key.BN702,Mod2002013Key.BN703,Mod2002013Key.BN707},"D.I. interna 2009")
	,C0005(new Mod2002013Key[]{Mod2002013Key.BN110,Mod2002013Key.BN111,Mod2002013Key.BN071,Mod2002013Key.BN187,Mod2002013Key.BN300},"D.I. interna 2010")
	,C0006(new Mod2002013Key[]{Mod2002013Key.BN112,Mod2002013Key.BN113,Mod2002013Key.BN025,Mod2002013Key.BN026,Mod2002013Key.BN027},"D.I. interna 2011")
	,C0007(new Mod2002013Key[]{Mod2002013Key.BN114,Mod2002013Key.BN115,Mod2002013Key.BN714,Mod2002013Key.BN715,Mod2002013Key.BN716},"D.I. interna 2012")
	,C0008(new Mod2002013Key[]{Mod2002013Key.BN735,Mod2002013Key.BN920,Mod2002013Key.BN736,Mod2002013Key.BN737,Mod2002013Key.BN738},"D.I. interna 2013")
	,C0009(new Mod2002013Key[]{Mod2002013Key.BN116,Mod2002013Key.BN103,Mod2002013Key.BN117,null           ,Mod2002013Key.BN118},"Total 2006-2013")
	;
	 
    private String description;
    
    private Mod2002013Key[] keys;
    
	private Mod2002013BN570Key(Mod2002013Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002013Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

