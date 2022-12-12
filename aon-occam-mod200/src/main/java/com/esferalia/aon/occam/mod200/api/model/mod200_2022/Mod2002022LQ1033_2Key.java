package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Reserva de nivelación - Dotación de la reserva
public enum Mod2002022LQ1033_2Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002022Key[]{Mod2002022Key.LQ1154,Mod2002022Key.LQ1155,Mod2002022Key.LQ1156,Mod2002022Key.LQ1157},"2016")
	,C02(new Mod2002022Key[]{Mod2002022Key.LQ1458,Mod2002022Key.LQ1459,Mod2002022Key.LQ1460,Mod2002022Key.LQ1461},"2017")
	,C03(new Mod2002022Key[]{Mod2002022Key.LQ1732,Mod2002022Key.LQ1733,Mod2002022Key.LQ1734,Mod2002022Key.LQ1735},"2018")
	,C04(new Mod2002022Key[]{Mod2002022Key.LQ1964,Mod2002022Key.LQ1965,Mod2002022Key.LQ1966,Mod2002022Key.LQ1967},"2019")
	,C05(new Mod2002022Key[]{Mod2002022Key.LQ2241,Mod2002022Key.LQ2242,Mod2002022Key.LQ2243,Mod2002022Key.LQ2244},"2020")
	,C06(new Mod2002022Key[]{Mod2002022Key.LQ2413,Mod2002022Key.LQ2414,Mod2002022Key.LQ2415,Mod2002022Key.LQ2416},"2021(*)")
	,C07(new Mod2002022Key[]{Mod2002022Key.LQ1112,Mod2002022Key.LQ1113,Mod2002022Key.LQ1114,Mod2002022Key.LQ1115},"2021")
	,C08(new Mod2002022Key[]{Mod2002022Key.LQ1158,Mod2002022Key.LQ1159,Mod2002022Key.LQ1160,Mod2002022Key.LQ1161},"Total")
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022LQ1033_2Key(Mod2002022Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002022Key[] getKeys() {
		return keys; 
	}

}

