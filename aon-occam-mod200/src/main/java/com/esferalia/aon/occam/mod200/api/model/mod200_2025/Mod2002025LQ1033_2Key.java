package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Reserva de nivelación - Dotación de la reserva
public enum Mod2002025LQ1033_2Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002025Key[]{Mod2002025Key.LQ2241,Mod2002025Key.LQ2242,Mod2002025Key.LQ2243,Mod2002025Key.LQ2244},"2020")
	,C02(new Mod2002025Key[]{Mod2002025Key.LQ2413,Mod2002025Key.LQ2414,Mod2002025Key.LQ2415,Mod2002025Key.LQ2416},"2021")
	,C03(new Mod2002025Key[]{Mod2002025Key.LQ1112,Mod2002025Key.LQ1113,Mod2002025Key.LQ1114,Mod2002025Key.LQ1115},"2022")
	,C04(new Mod2002025Key[]{Mod2002025Key.LQ1872,Mod2002025Key.LQ1410,Mod2002025Key.LQ1411,Mod2002025Key.LQ1412},"2023")
	,C05(new Mod2002025Key[]{Mod2002025Key.LQ2780,Mod2002025Key.LQ2782,Mod2002025Key.LQ2783,Mod2002025Key.LQ2784},"2024")
	,C06(new Mod2002025Key[]{Mod2002025Key.LQ464 ,Mod2002025Key.LQ469 ,Mod2002025Key.LQ470 ,Mod2002025Key.LQ471 },"2025(*)")
	,C07(new Mod2002025Key[]{Mod2002025Key.LQ3599,Mod2002025Key.LQ3600,Mod2002025Key.LQ3601,Mod2002025Key.LQ3602},"2025")
	,C08(new Mod2002025Key[]{Mod2002025Key.LQ1158,Mod2002025Key.LQ1159,Mod2002025Key.LQ1160,Mod2002025Key.LQ1161},"Total")
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025LQ1033_2Key(Mod2002025Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002025Key[] getKeys() {
		return keys; 
	}

}

