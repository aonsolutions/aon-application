package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;


public enum Mod200BN082Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	  C0001(new Mod200Key[]{Mod200Key.BN918,Mod200Key.BN919,Mod200Key.BN574,Mod200Key.BN580},"2013: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C0002(new Mod200Key[]{Mod200Key.BN589,Mod200Key.BN976,Mod200Key.BN977,Mod200Key.BN978},"2013: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C0003(new Mod200Key[]{Mod200Key.BN517,Mod200Key.BN081,null           ,Mod200Key.BN083},"Total")
	;
	 
    private String description;
    
    private Mod200Key[] keys;
    
	private Mod200BN082Key(Mod200Key[] keys, String description) {
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

