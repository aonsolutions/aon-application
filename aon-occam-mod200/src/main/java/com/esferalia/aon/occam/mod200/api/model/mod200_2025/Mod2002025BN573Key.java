package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición internacional LIS. DI internacional 2025 
public enum Mod2002025BN573Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002025Key[]{Mod2002025Key.BN163,Mod2002025Key.BN165,Mod2002025Key.BN166},"DI jur\u00EDdica: Impuesto soportado por el contribuyente (art. 31 LIS)")
	,C02(new Mod2002025Key[]{Mod2002025Key.BN167,Mod2002025Key.BN169,Mod2002025Key.BN170},"DI econ\u00F3mica: Dividendos y part. en beneficios (art. 32 LIS)")
	,C03(new Mod2002025Key[]{Mod2002025Key.BN171,Mod2002025Key.BN573,Mod2002025Key.BN174},"Total")
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025BN573Key(Mod2002025Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Mod2002025Key[] getKeys() {
		return keys;
	}

}
