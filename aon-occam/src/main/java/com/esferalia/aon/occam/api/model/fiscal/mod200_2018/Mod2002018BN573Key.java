package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Deducciones doble imposición internacional LIS. DI internacional 2018 
public enum Mod2002018BN573Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002018Key[]{Mod2002018Key.BN163,Mod2002018Key.BN165,Mod2002018Key.BN166},"DI jur\u00EDdica: Imp. soportado por el contribuyente (art. 31 LIS)")
	,C02(new Mod2002018Key[]{Mod2002018Key.BN167,Mod2002018Key.BN169,Mod2002018Key.BN170},"DI econ\u00F3mica: Dividendos y part. en beneficios (art. 32 LIS)")
	,C03(new Mod2002018Key[]{Mod2002018Key.BN171,Mod2002018Key.BN573,Mod2002018Key.BN174},"Total 2018")
	;
	 
    private String description;
    private Mod2002018Key[] keys;

	private Mod2002018BN573Key(Mod2002018Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public Mod2002018Key[] getKeys() {
		return keys;
	}

}

