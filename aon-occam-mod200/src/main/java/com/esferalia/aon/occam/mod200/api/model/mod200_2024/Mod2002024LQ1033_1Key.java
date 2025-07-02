package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Reserva de nivelación - Reducción en base imponible
public enum Mod2002024LQ1033_1Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF
	 
	 C01(new Mod2002024Key[]{Mod2002024Key.LQ2238 ,Mod2002024Key.LQ2239,Mod2002024Key.LQ1603,null                },"2019")
	,C02(new Mod2002024Key[]{Mod2002024Key.LQ2410 ,Mod2002024Key.LQ2411,Mod2002024Key.LQ1604,Mod2002024Key.LQ2412},"2020")
	,C03(new Mod2002024Key[]{Mod2002024Key.LQ1109 ,Mod2002024Key.LQ1730,Mod2002024Key.LQ1605,Mod2002024Key.LQ1111},"2021")	
	,C04(new Mod2002024Key[]{Mod2002024Key.LQ1406 ,Mod2002024Key.LQ1404,Mod2002024Key.LQ1405,Mod2002024Key.LQ1407},"2022")
	,C05(new Mod2002024Key[]{Mod2002024Key.LQ2776 ,Mod2002024Key.LQ2777,Mod2002024Key.LQ2778,Mod2002024Key.LQ2779},"2023")
	,C06(new Mod2002024Key[]{Mod2002024Key.LQ455  ,Mod2002024Key.LQ456 ,Mod2002024Key.LQ463 ,Mod2002024Key.LQ2779},"2024(*)")
	,C07(new Mod2002024Key[]{Mod2002024Key.LQ1034A,null                ,null                ,Mod2002024Key.LQ1731},"2024")		
	,C08(new Mod2002024Key[]{Mod2002024Key.LQ1147 ,Mod2002024Key.LQ1033,Mod2002024Key.LQ1606,Mod2002024Key.LQ1149},"Total")
	;
	 
    private String description;
    private Mod2002024Key[] keys;

	private Mod2002024LQ1033_1Key(Mod2002024Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002024Key[] getKeys() {
		return keys; 
	}

}

