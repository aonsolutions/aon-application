package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Reserva de nivelación - Reducción en base imponible
public enum Mod2002023LQ1033_1Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF
	 
	 C01(new Mod2002023Key[]{Mod2002023Key.LQ1961 ,Mod2002023Key.LQ1962,Mod2002023Key.LQ1602,null                },"2018")	
	,C02(new Mod2002023Key[]{Mod2002023Key.LQ2238 ,Mod2002023Key.LQ2239,Mod2002023Key.LQ1603,Mod2002023Key.LQ2240},"2019")
	,C03(new Mod2002023Key[]{Mod2002023Key.LQ2410 ,Mod2002023Key.LQ2411,Mod2002023Key.LQ1604,Mod2002023Key.LQ2412},"2020")
	,C04(new Mod2002023Key[]{Mod2002023Key.LQ1109 ,Mod2002023Key.LQ1730,Mod2002023Key.LQ1605,Mod2002023Key.LQ1111},"2021")	
	,C05(new Mod2002023Key[]{Mod2002023Key.LQ1406 ,Mod2002023Key.LQ1404,Mod2002023Key.LQ1405,Mod2002023Key.LQ1407},"2022")
	,C06(new Mod2002023Key[]{Mod2002023Key.LQ2776 ,Mod2002023Key.LQ2777,Mod2002023Key.LQ2778,Mod2002023Key.LQ2779},"2023(*)")
	,C07(new Mod2002023Key[]{Mod2002023Key.LQ1034A,null                ,null                ,Mod2002023Key.LQ1731},"2023")		
	,C08(new Mod2002023Key[]{Mod2002023Key.LQ1147 ,Mod2002023Key.LQ1033,Mod2002023Key.LQ1606,Mod2002023Key.LQ1149},"Total")
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023LQ1033_1Key(Mod2002023Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002023Key[] getKeys() {
		return keys; 
	}

}

