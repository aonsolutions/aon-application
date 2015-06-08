package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;


public enum Mod2002013BN584Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	 C0001(new Mod2002013Key[]{Mod2002013Key.BN749,Mod2002013Key.BN750,null           },"2008: Periodificaci\u00F3n")
	,C0002(new Mod2002013Key[]{Mod2002013Key.BN752,Mod2002013Key.BN753,Mod2002013Key.BN754},"2009: Periodificaci\u00F3n")
	,C0003(new Mod2002013Key[]{Mod2002013Key.BN755,Mod2002013Key.BN756,Mod2002013Key.BN757},"2010: Periodificaci\u00F3n")
	,C0004(new Mod2002013Key[]{Mod2002013Key.BN758,Mod2002013Key.BN759,Mod2002013Key.BN760},"2011: Periodificaci\u00F3n")
	,C0005(new Mod2002013Key[]{Mod2002013Key.BN761,Mod2002013Key.BN762,Mod2002013Key.BN763},"2012: Periodificaci\u00F3n")
	,C0006(new Mod2002013Key[]{Mod2002013Key.BN744,Mod2002013Key.BN745,Mod2002013Key.BN746},"2013: Periodificaci\u00F3n")
	,C0007(new Mod2002013Key[]{Mod2002013Key.BN764,null           ,Mod2002013Key.BN765},"Total deducciones disposici\u00F3n transitoria octava L.I.S.") 
	;
	 
    private String description;
    
    private Mod2002013Key[] keys;
    
	private Mod2002013BN584Key(Mod2002013Key[] keys, String description) {
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

