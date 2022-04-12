package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

// Reserva de nivelación - Reducción en base imponible
public enum Mod2002020LQ1033_1Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002020Key[]{Mod2002020Key.LQ1141,Mod2002020Key.LQ1142,null                },"2015")
	,C02(new Mod2002020Key[]{Mod2002020Key.LQ1144,Mod2002020Key.LQ1145,Mod2002020Key.LQ1146},"2016")
	,C03(new Mod2002020Key[]{Mod2002020Key.LQ1455,Mod2002020Key.LQ1456,Mod2002020Key.LQ1457},"2017")
	,C04(new Mod2002020Key[]{Mod2002020Key.LQ1961,Mod2002020Key.LQ1962,Mod2002020Key.LQ1963},"2018")	
	,C05(new Mod2002020Key[]{Mod2002020Key.LQ2238,Mod2002020Key.LQ2239,Mod2002020Key.LQ2240},"2019")
	,C06(new Mod2002020Key[]{Mod2002020Key.LQ2410,Mod2002020Key.LQ2411,Mod2002020Key.LQ2412},"2020(*)")	
	,C07(new Mod2002020Key[]{Mod2002020Key.LQ1034A,Mod2002020Key.LQ1730,Mod2002020Key.LQ1731},"2020")	
	,C08(new Mod2002020Key[]{Mod2002020Key.LQ1147,Mod2002020Key.LQ1033,Mod2002020Key.LQ1149},"Total")
	;
	 
    private String description;
    private Mod2002020Key[] keys;

	private Mod2002020LQ1033_1Key(Mod2002020Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002020Key[] getKeys() {
		return keys; 
	}

}

