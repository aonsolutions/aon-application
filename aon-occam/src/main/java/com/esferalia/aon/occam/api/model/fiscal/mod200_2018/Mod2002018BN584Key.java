package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Deducciones disposición transitoria 24ª.1 LIS
public enum Mod2002018BN584Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002018Key[]{Mod2002018Key.BN749,Mod2002018Key.BN750,null               },"2013: Periodificaci\u00F3n")
	,C02(new Mod2002018Key[]{Mod2002018Key.BN752,Mod2002018Key.BN753,Mod2002018Key.BN754},"2014: Periodificaci\u00F3n")
	,C03(new Mod2002018Key[]{Mod2002018Key.BN755,Mod2002018Key.BN756,Mod2002018Key.BN757},"2015: Periodificaci\u00F3n")
	,C04(new Mod2002018Key[]{Mod2002018Key.BN758,Mod2002018Key.BN759,Mod2002018Key.BN760},"2016: Periodificaci\u00F3n")
	,C05(new Mod2002018Key[]{Mod2002018Key.BN761,Mod2002018Key.BN762,Mod2002018Key.BN763},"2017: Periodificaci\u00F3n")
	,C06(new Mod2002018Key[]{Mod2002018Key.BN744,Mod2002018Key.BN745,Mod2002018Key.BN746},"2018: Periodificaci\u00F3n(*)")
	,C07(new Mod2002018Key[]{Mod2002018Key.BN779,Mod2002018Key.BN783,Mod2002018Key.BN784},"2018: Periodificaci\u00F3n")
	,C08(new Mod2002018Key[]{Mod2002018Key.BN764,Mod2002018Key.BN584,Mod2002018Key.BN765},"Total") 
	;
	 
    private String description;
    
    private Mod2002018Key[] keys;
    
	private Mod2002018BN584Key(Mod2002018Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002018Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

