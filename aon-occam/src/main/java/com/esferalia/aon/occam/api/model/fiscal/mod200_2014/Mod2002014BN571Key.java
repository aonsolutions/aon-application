package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014BN571Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	
	 C0001(new Mod2002014Key[]{Mod2002014Key.BN119,Mod2002014Key.BN120,Mod2002014Key.BN121,Mod2002014Key.BN122},"Intersoc. al 50% (art. 30,1 y 3 L.I.S.)")
	,C0002(new Mod2002014Key[]{Mod2002014Key.BN123,Mod2002014Key.BN124,Mod2002014Key.BN125,Mod2002014Key.BN126},"Intersoc. al 100% (art. 30,2 y 3 L.I.S.)")
	,C0003(new Mod2002014Key[]{Mod2002014Key.BN127,Mod2002014Key.BN128,Mod2002014Key.BN129,Mod2002014Key.BN130},"Plusval\u00EDas fuente interna (art. 30.5 L.I.S.)")
	,C0004(new Mod2002014Key[]{Mod2002014Key.BN131,Mod2002014Key.BN132,null           ,Mod2002014Key.BN133},"Total 2014")
	;
	 
    private String description;
    private Mod2002014Key[] keys;

	private Mod2002014BN571Key(Mod2002014Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002014Key[] getKeys() {
		return keys;
	}

}

