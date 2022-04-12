package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import java.io.Serializable;

// Deducciones doble imposición internacional LIS. DI internacional 2016 
public enum Mod2002016BN573Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002016Key[]{Mod2002016Key.BN163,Mod2002016Key.BN165,Mod2002016Key.BN166},"DI jur\u00EDdica: Imp. soportado por el contribuyente (art. 31 LIS)")
	,C02(new Mod2002016Key[]{Mod2002016Key.BN167,Mod2002016Key.BN169,Mod2002016Key.BN170},"DI econ\u00F3mica: Dividendos y part. en beneficios (art. 32 LIS)")
	,C03(new Mod2002016Key[]{Mod2002016Key.BN171,null /*BN573*/		,Mod2002016Key.BN174},"Total 2016")
	;
	 
    private String description;
    private Mod2002016Key[] keys;

	private Mod2002016BN573Key(Mod2002016Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public Mod2002016Key[] getKeys() {
		return keys;
	}

}

