package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones disposición transitoria 24ª.1 LIS
public enum Mod2002023BN584Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002023Key[]{Mod2002023Key.BN749,Mod2002023Key.BN750,null               },"2017: Periodificaci\u00F3n")
	,C02(new Mod2002023Key[]{Mod2002023Key.BN752,Mod2002023Key.BN753,Mod2002023Key.BN754},"2018: Periodificaci\u00F3n")
	,C03(new Mod2002023Key[]{Mod2002023Key.BN755,Mod2002023Key.BN756,Mod2002023Key.BN757},"2019: Periodificaci\u00F3n")
	,C04(new Mod2002023Key[]{Mod2002023Key.BN758,Mod2002023Key.BN759,Mod2002023Key.BN760},"2020: Periodificaci\u00F3n")
	,C05(new Mod2002023Key[]{Mod2002023Key.BN761,Mod2002023Key.BN762,Mod2002023Key.BN763},"2021: Periodificaci\u00F3n")
	,C06(new Mod2002023Key[]{Mod2002023Key.BN744,Mod2002023Key.BN745,Mod2002023Key.BN746},"2023(*): Periodificaci\u00F3n")
	,C07(new Mod2002023Key[]{Mod2002023Key.BN779,Mod2002023Key.BN783,Mod2002023Key.BN784},"2023: Periodificaci\u00F3n")
	,C08(new Mod2002023Key[]{Mod2002023Key.BN764,Mod2002023Key.BN584,Mod2002023Key.BN765},"Total") 
	;
	 
    private String description;
    private Mod2002023Key[] keys;
    
	private Mod2002023BN584Key(Mod2002023Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002023Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}

}

