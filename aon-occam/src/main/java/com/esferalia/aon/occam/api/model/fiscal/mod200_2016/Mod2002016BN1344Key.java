package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import java.io.Serializable;

// Deducciones doble imposición interna (DT 23ª.1 LIS) de Ejercicios anteriores
public enum Mod2002016BN1344Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002016Key[]{Mod2002016Key.BN101,Mod2002016Key.BN102,Mod2002016Key.BN119,Mod2002016Key.BN120,Mod2002016Key.BN121 },"D.I. interna 2015")
	,C02(new Mod2002016Key[]{Mod2002016Key.BN122,Mod2002016Key.BN123,Mod2002016Key.BN124,Mod2002016Key.BN125,Mod2002016Key.BN126 },"D.I. interna 2016(*)")
	,C03(new Mod2002016Key[]{Mod2002016Key.BN1342,Mod2002016Key.BN103,Mod2002016Key.BN1343,null /* BN1344 */,Mod2002016Key.BN1345},"Total")
	;
	 
    private String description;
    private Mod2002016Key[] keys;

	private Mod2002016BN1344Key(Mod2002016Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002016Key[] getKeys() {
		return keys;
	}

}

