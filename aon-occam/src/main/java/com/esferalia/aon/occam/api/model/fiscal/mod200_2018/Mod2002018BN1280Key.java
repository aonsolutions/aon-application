package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Deducciones doble imposición interna (DT 23ª.1 LIS) generada en el ejercicio
public enum Mod2002018BN1280Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002018Key[]{Mod2002018Key.BN127 ,Mod2002018Key.BN128 ,Mod2002018Key.BN129 },"D.I. interna 2018")
	,C02(new Mod2002018Key[]{Mod2002018Key.BN1346,Mod2002018Key.BN1280,Mod2002018Key.BN1347},"Total")
	;
	 
    private String description;
    private Mod2002018Key[] keys;

	private Mod2002018BN1280Key(Mod2002018Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002018Key[] getKeys() {
		return keys;
	}

}

