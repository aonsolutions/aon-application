package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public enum Mod200BN584Key implements Serializable, IsSerializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	 C0001(new Mod200Key[]{Mod200Key.BN749,Mod200Key.BN750,null           },"2008: Periodificaci\u00F3n")
	,C0002(new Mod200Key[]{Mod200Key.BN752,Mod200Key.BN753,Mod200Key.BN754},"2009: Periodificaci\u00F3n")
	,C0003(new Mod200Key[]{Mod200Key.BN755,Mod200Key.BN756,Mod200Key.BN757},"2010: Periodificaci\u00F3n")
	,C0004(new Mod200Key[]{Mod200Key.BN758,Mod200Key.BN759,Mod200Key.BN760},"2011: Periodificaci\u00F3n")
	,C0005(new Mod200Key[]{Mod200Key.BN761,Mod200Key.BN762,Mod200Key.BN763},"2012: Periodificaci\u00F3n")
	,C0006(new Mod200Key[]{Mod200Key.BN744,Mod200Key.BN745,Mod200Key.BN746},"2013: Periodificaci\u00F3n")
	,C0007(new Mod200Key[]{Mod200Key.BN764,null           ,Mod200Key.BN765},"Total deducciones disposici\u00F3n transitoria octava L.I.S.") 
	;
	 
    private String description;
    
    private Mod200Key[] keys;
    
	private Mod200BN584Key(Mod200Key[] keys, String description) {
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

