package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Deducciones doble imposición internacional LIS. DI internac. períodos anteriores
public enum Mod2002015BN571Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002015Key[]{Mod2002015Key.BN1054,Mod2002015Key.BN1050,Mod2002015Key.BN1051,Mod2002015Key.BN1052,Mod2002015Key.BN1053},"DI internacional 2015(*)")
	,C02(new Mod2002015Key[]{Mod2002015Key.BN131 ,null /* BN571 */    ,Mod2002015Key.BN132 ,null /* BN571 */    ,Mod2002015Key.BN133 },"Total 2015")
	;
	 
    private String description;
    private Mod2002015Key[] keys;

	private Mod2002015BN571Key(Mod2002015Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002015Key[] getKeys() {
		return keys;
	}

}

