package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014BN082Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	  C0001(new Mod2002014Key[]{Mod2002014Key.BN918,Mod2002014Key.BN919,Mod2002014Key.BN574,Mod2002014Key.BN580},Mod2002014Key.BN918.getDescription())
	 ,C0002(new Mod2002014Key[]{Mod2002014Key.BN589,Mod2002014Key.BN976,Mod2002014Key.BN977,Mod2002014Key.BN978},Mod2002014Key.BN589.getDescription())
	 ,C0003(new Mod2002014Key[]{Mod2002014Key.BN822,Mod2002014Key.BN823,Mod2002014Key.BN824,Mod2002014Key.BN231},Mod2002014Key.BN822.getDescription())
	 ,C0004(new Mod2002014Key[]{Mod2002014Key.BN232,Mod2002014Key.BN233,Mod2002014Key.BN850,Mod2002014Key.BN851},Mod2002014Key.BN232.getDescription())
	 ,C0005(new Mod2002014Key[]{Mod2002014Key.BN517,Mod2002014Key.BN081,null               ,Mod2002014Key.BN083},"Total")
	;
	 
    private String description;
    
    private Mod2002014Key[] keys;
    
	private Mod2002014BN082Key(Mod2002014Key[] keys, String description) {
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

