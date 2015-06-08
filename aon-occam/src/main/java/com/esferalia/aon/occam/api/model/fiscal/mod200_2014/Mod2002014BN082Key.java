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
	  C0001(new Mod2002014Key[]{Mod2002014Key.BN918,Mod2002014Key.BN919,Mod2002014Key.BN574,Mod2002014Key.BN580},"2013: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C0002(new Mod2002014Key[]{Mod2002014Key.BN589,Mod2002014Key.BN976,Mod2002014Key.BN977,Mod2002014Key.BN978},"2013: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C0003(new Mod2002014Key[]{Mod2002014Key.BN517,Mod2002014Key.BN081,null           ,Mod2002014Key.BN083},"Total")
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

