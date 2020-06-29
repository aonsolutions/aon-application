package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import java.io.Serializable;

// Reserva de nivelación - Reducción en base imponible
public enum Mod2002019LQ1033_1Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002019Key[]{Mod2002019Key.LQ1141,Mod2002019Key.LQ1142,Mod2002019Key.LQ1143},"2015")
	,C02(new Mod2002019Key[]{Mod2002019Key.LQ1144,Mod2002019Key.LQ1145,Mod2002019Key.LQ1146},"2016")
	,C03(new Mod2002019Key[]{Mod2002019Key.LQ1455,Mod2002019Key.LQ1456,Mod2002019Key.LQ1457},"2017")
	,C04(new Mod2002019Key[]{Mod2002019Key.LQ1961,Mod2002019Key.LQ1962,Mod2002019Key.LQ1963},"2018")	
	,C05(new Mod2002019Key[]{Mod2002019Key.LQ2238,Mod2002019Key.LQ2239,Mod2002019Key.LQ2240},"2019(*)")
	,C06(new Mod2002019Key[]{Mod2002019Key.LQ1034,Mod2002019Key.LQ1730,Mod2002019Key.LQ1731},"2019")	
	,C07(new Mod2002019Key[]{Mod2002019Key.LQ1147,Mod2002019Key.LQ1033,Mod2002019Key.LQ1149},"Total")
	;
	 
    private String description;
    private Mod2002019Key[] keys;

	private Mod2002019LQ1033_1Key(Mod2002019Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002019Key[] getKeys() {
		return keys; 
	}
}

