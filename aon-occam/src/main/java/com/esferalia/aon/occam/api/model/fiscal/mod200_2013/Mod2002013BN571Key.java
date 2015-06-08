package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;


public enum Mod2002013BN571Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	
	 C0001(new Mod2002013Key[]{Mod2002013Key.BN119,Mod2002013Key.BN120,Mod2002013Key.BN121,Mod2002013Key.BN122},"Intersoc. al 50% (art. 30,1 y 3 L.I.S.)")
	,C0002(new Mod2002013Key[]{Mod2002013Key.BN123,Mod2002013Key.BN124,Mod2002013Key.BN125,Mod2002013Key.BN126},"Intersoc. al 100% (art. 30,2 y 3 L.I.S.)")
	,C0003(new Mod2002013Key[]{Mod2002013Key.BN127,Mod2002013Key.BN128,Mod2002013Key.BN129,Mod2002013Key.BN130},"Plusval\u00EDas fuente interna (art. 30.5 L.I.S.)")
	,C0004(new Mod2002013Key[]{Mod2002013Key.BN131,Mod2002013Key.BN132,null           ,Mod2002013Key.BN133},"Total 2013")
	;
	 
    private String description;
    private Mod2002013Key[] keys;

	private Mod2002013BN571Key(Mod2002013Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002013Key[] getKeys() {
		return keys;
	}

}

