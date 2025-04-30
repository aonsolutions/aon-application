package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Reserva de nivelación - Dotación de la reserva
public enum Mod2002024LQ1033_2Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002024Key[]{Mod2002024Key.LQ1964,Mod2002024Key.LQ1965,Mod2002024Key.LQ1966,Mod2002024Key.LQ1967},"2019")
	,C02(new Mod2002024Key[]{Mod2002024Key.LQ2241,Mod2002024Key.LQ2242,Mod2002024Key.LQ2243,Mod2002024Key.LQ2244},"2020")
	,C03(new Mod2002024Key[]{Mod2002024Key.LQ2413,Mod2002024Key.LQ2414,Mod2002024Key.LQ2415,Mod2002024Key.LQ2416},"2021")
	,C04(new Mod2002024Key[]{Mod2002024Key.LQ1112,Mod2002024Key.LQ1113,Mod2002024Key.LQ1114,Mod2002024Key.LQ1115},"2022")
	,C05(new Mod2002024Key[]{Mod2002024Key.LQ1872,Mod2002024Key.LQ1410,Mod2002024Key.LQ1411,Mod2002024Key.LQ1412},"2023")
	,C06(new Mod2002024Key[]{Mod2002024Key.LQ2780,Mod2002024Key.LQ2782,Mod2002024Key.LQ2783,Mod2002024Key.LQ2784},"2024(*)")
	,C07(new Mod2002024Key[]{Mod2002024Key.LQ464 ,Mod2002024Key.LQ469 ,Mod2002024Key.LQ470 ,Mod2002024Key.LQ471 },"2024")
	,C08(new Mod2002024Key[]{Mod2002024Key.LQ1158,Mod2002024Key.LQ1159,Mod2002024Key.LQ1160,Mod2002024Key.LQ1161},"Total")
	;
	 
    private String description;
    private Mod2002024Key[] keys;

	private Mod2002024LQ1033_2Key(Mod2002024Key[] keys, String description) {
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

