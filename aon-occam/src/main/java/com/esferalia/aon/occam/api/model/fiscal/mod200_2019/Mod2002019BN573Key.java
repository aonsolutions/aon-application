package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import java.io.Serializable;

// Deducciones doble imposición internacional LIS. DI internacional 2018 
public enum Mod2002019BN573Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002019Key[]{Mod2002019Key.BN163,Mod2002019Key.BN165,Mod2002019Key.BN166},"DI jur\u00EDdica: Imp. soportado por el contribuyente (art. 31 LIS)")
	,C02(new Mod2002019Key[]{Mod2002019Key.BN167,Mod2002019Key.BN169,Mod2002019Key.BN170},"DI econ\u00F3mica: Dividendos y part. en beneficios (art. 32 LIS)")
	,C03(new Mod2002019Key[]{Mod2002019Key.BN171,Mod2002019Key.BN573,Mod2002019Key.BN174},"Total 2019")
	;
	 
    private String description;
    private Mod2002019Key[] keys;

	private Mod2002019BN573Key(Mod2002019Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public Mod2002019Key[] getKeys() {
		return keys;
	}

}

