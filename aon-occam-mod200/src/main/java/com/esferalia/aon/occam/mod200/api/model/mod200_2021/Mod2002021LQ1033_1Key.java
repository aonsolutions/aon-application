package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Reserva de nivelación - Reducción en base imponible
public enum Mod2002021LQ1033_1Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002021Key[]{Mod2002021Key.LQ1144 ,Mod2002021Key.LQ1145,Mod2002021Key.LQ1600,null                },"2016")
	,C02(new Mod2002021Key[]{Mod2002021Key.LQ1455 ,Mod2002021Key.LQ1456,Mod2002021Key.LQ1601,Mod2002021Key.LQ1457},"2017")
	,C03(new Mod2002021Key[]{Mod2002021Key.LQ1961 ,Mod2002021Key.LQ1962,Mod2002021Key.LQ1602,Mod2002021Key.LQ1963},"2018")	
	,C04(new Mod2002021Key[]{Mod2002021Key.LQ2238 ,Mod2002021Key.LQ2239,Mod2002021Key.LQ1603,Mod2002021Key.LQ2240},"2019")
	,C05(new Mod2002021Key[]{Mod2002021Key.LQ2410 ,Mod2002021Key.LQ2411,Mod2002021Key.LQ1604,Mod2002021Key.LQ2412},"2020")
	,C06(new Mod2002021Key[]{Mod2002021Key.LQ1109 ,Mod2002021Key.LQ1730,Mod2002021Key.LQ1605,Mod2002021Key.LQ1111},"2021(*)")
	,C07(new Mod2002021Key[]{Mod2002021Key.LQ1034A,null                ,null                ,Mod2002021Key.LQ1731},"2021")	
	,C08(new Mod2002021Key[]{Mod2002021Key.LQ1147 ,Mod2002021Key.LQ1033,Mod2002021Key.LQ1606,Mod2002021Key.LQ1149},"Total")
	;
	 
    private String description;
    private Mod2002021Key[] keys;

	private Mod2002021LQ1033_1Key(Mod2002021Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002021Key[] getKeys() {
		return keys; 
	}

}

