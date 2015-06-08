package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014BN573Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C0001(new Mod2002014Key[]{Mod2002014Key.BN163,Mod2002014Key.BN164,Mod2002014Key.BN165,Mod2002014Key.BN166},"Impuesto soportado por el sujeto pasivo (art. 31 L.I.S.)")
	,C0002(new Mod2002014Key[]{Mod2002014Key.BN167,Mod2002014Key.BN168,Mod2002014Key.BN169,Mod2002014Key.BN170},"Dividendos y participaciones en beneficios (art. 32 L.I.S.)")
	,C0003(new Mod2002014Key[]{Mod2002014Key.BN171,Mod2002014Key.BN172,null           ,Mod2002014Key.BN174},"Total 2013")
	;
	 
    private String description;
    private Mod2002014Key[] keys;

	private Mod2002014BN573Key(Mod2002014Key[] keys, String description) {
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

