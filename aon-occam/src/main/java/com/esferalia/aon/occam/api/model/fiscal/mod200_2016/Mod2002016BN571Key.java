package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import java.io.Serializable;

// Deducciones doble imposición internacional LIS. DI internac. períodos anteriores
public enum Mod2002016BN571Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002016Key[]{Mod2002016Key.BN1054,Mod2002016Key.BN1050,Mod2002016Key.BN1051,Mod2002016Key.BN1052,Mod2002016Key.BN1053},"DI internacional 2015(*)")
	,C02(new Mod2002016Key[]{Mod2002016Key.BN131 ,null /* BN571 */    ,Mod2002016Key.BN132 ,null /* BN571 */    ,Mod2002016Key.BN133 },"Total 2015")
	;
	 
    private String description;
    private Mod2002016Key[] keys;

	private Mod2002016BN571Key(Mod2002016Key[] keys, String description) {
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

