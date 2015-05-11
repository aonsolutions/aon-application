package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;


public enum Mod200BN571Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	
	 C0001(new Mod200Key[]{Mod200Key.BN119,Mod200Key.BN120,Mod200Key.BN121,Mod200Key.BN122},"Intersoc. al 50% (art. 30,1 y 3 L.I.S.)")
	,C0002(new Mod200Key[]{Mod200Key.BN123,Mod200Key.BN124,Mod200Key.BN125,Mod200Key.BN126},"Intersoc. al 100% (art. 30,2 y 3 L.I.S.)")
	,C0003(new Mod200Key[]{Mod200Key.BN127,Mod200Key.BN128,Mod200Key.BN129,Mod200Key.BN130},"Plusval\u00EDas fuente interna (art. 30.5 L.I.S.)")
	,C0004(new Mod200Key[]{Mod200Key.BN131,Mod200Key.BN132,null           ,Mod200Key.BN133},"Total 2013")
	;
	 
    private String description;
    private Mod200Key[] keys;

	private Mod200BN571Key(Mod200Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod200Key[] getKeys() {
		return keys;
	}

}

