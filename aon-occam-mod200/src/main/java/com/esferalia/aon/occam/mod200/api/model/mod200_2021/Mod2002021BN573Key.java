package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición internacional LIS. DI internacional 2021 
public enum Mod2002021BN573Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002021Key[]{Mod2002021Key.BN163,Mod2002021Key.BN165,Mod2002021Key.BN166},"DI jur\u00EDdica: Imp. soportado por el contribuyente (art. 31 LIS)")
	,C02(new Mod2002021Key[]{Mod2002021Key.BN167,Mod2002021Key.BN169,Mod2002021Key.BN170},"DI econ\u00F3mica: Dividendos y part. en beneficios (art. 32 LIS)")
	,C03(new Mod2002021Key[]{Mod2002021Key.BN171,Mod2002021Key.BN573,Mod2002021Key.BN174},"Total")
	;
	 
    private String description;
    private Mod2002021Key[] keys;

	private Mod2002021BN573Key(Mod2002021Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Mod2002021Key[] getKeys() {
		return keys;
	}


}

