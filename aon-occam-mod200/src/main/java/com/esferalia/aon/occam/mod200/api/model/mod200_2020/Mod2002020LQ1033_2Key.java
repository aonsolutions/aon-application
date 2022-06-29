package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Reserva de nivelación - Dotación de la reserva
public enum Mod2002020LQ1033_2Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002020Key[]{Mod2002020Key.LQ1150,Mod2002020Key.LQ1151,Mod2002020Key.LQ1152,Mod2002020Key.LQ1153},"2015")
	,C02(new Mod2002020Key[]{Mod2002020Key.LQ1154,Mod2002020Key.LQ1155,Mod2002020Key.LQ1156,Mod2002020Key.LQ1157},"2016")
	,C03(new Mod2002020Key[]{Mod2002020Key.LQ1458,Mod2002020Key.LQ1459,Mod2002020Key.LQ1460,Mod2002020Key.LQ1461},"2017")
	,C04(new Mod2002020Key[]{Mod2002020Key.LQ1732,Mod2002020Key.LQ1733,Mod2002020Key.LQ1734,Mod2002020Key.LQ1735},"2018")
	,C05(new Mod2002020Key[]{Mod2002020Key.LQ1964,Mod2002020Key.LQ1965,Mod2002020Key.LQ1966,Mod2002020Key.LQ1967},"2019")
	,C06(new Mod2002020Key[]{Mod2002020Key.LQ2241,Mod2002020Key.LQ2242,Mod2002020Key.LQ2243,Mod2002020Key.LQ2244},"2020(*)")
	,C07(new Mod2002020Key[]{Mod2002020Key.LQ2413,Mod2002020Key.LQ2414,Mod2002020Key.LQ2415,Mod2002020Key.LQ2416},"2020")
	,C08(new Mod2002020Key[]{Mod2002020Key.LQ1158,Mod2002020Key.LQ1159,Mod2002020Key.LQ1160,Mod2002020Key.LQ1161},"Total")
	;
	 
    private String description;
    private Mod2002020Key[] keys;

	private Mod2002020LQ1033_2Key(Mod2002020Key[] keys, String description) {
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

