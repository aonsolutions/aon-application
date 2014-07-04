package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public enum Mod200BN573Key implements Serializable, IsSerializable,IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C0001(new Mod200Key[]{Mod200Key.BN163,Mod200Key.BN164,Mod200Key.BN165,Mod200Key.BN166},"Impuesto soportado por el sujeto pasivo (art. 31 L.I.S.)")
	,C0002(new Mod200Key[]{Mod200Key.BN167,Mod200Key.BN168,Mod200Key.BN169,Mod200Key.BN170},"Dividendos y participaciones en beneficios (art. 32 L.I.S.)")
	,C0003(new Mod200Key[]{Mod200Key.BN171,Mod200Key.BN172,null           ,Mod200Key.BN174},"Total 2013")
	;
	 
    private String description;
    private Mod200Key[] keys;

	private Mod200BN573Key(Mod200Key[] keys, String description) {
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

