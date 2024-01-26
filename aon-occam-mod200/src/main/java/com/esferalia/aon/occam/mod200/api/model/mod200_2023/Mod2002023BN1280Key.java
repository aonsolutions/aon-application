package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición interna (DT 23ª.1 LIS) generada en el ejercicio
public enum Mod2002023BN1280Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002023Key[]{Mod2002023Key.BN127 ,Mod2002023Key.BN128 ,Mod2002023Key.BN129 },"D.I. interna 2023")
	,C02(new Mod2002023Key[]{Mod2002023Key.BN1346,Mod2002023Key.BN1280,Mod2002023Key.BN1347},"Total")
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023BN1280Key(Mod2002023Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002023Key[] getKeys() {
		return keys;
	}


}

