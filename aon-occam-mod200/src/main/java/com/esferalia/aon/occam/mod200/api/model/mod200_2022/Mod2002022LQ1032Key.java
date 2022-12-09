package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Reserva de capitalización
public enum Mod2002022LQ1032Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002022Key[]{Mod2002022Key.LQ1744,Mod2002022Key.LQ1745,null                },"2019")
	,C02(new Mod2002022Key[]{Mod2002022Key.LQ1985,Mod2002022Key.LQ1986,Mod2002022Key.LQ1987},"2020")
	,C03(new Mod2002022Key[]{Mod2002022Key.LQ2407,Mod2002022Key.LQ2408,Mod2002022Key.LQ2409},"2021(*)")
	,C04(new Mod2002022Key[]{Mod2002022Key.LQ1106,Mod2002022Key.LQ1107,Mod2002022Key.LQ1108},"2021")
	,C05(new Mod2002022Key[]{Mod2002022Key.LQ1137,Mod2002022Key.LQ1032,Mod2002022Key.LQ1139},"Total")
	,C06(new Mod2002022Key[]{Mod2002022Key.LQ1140,null                ,null                },"Reserva de capitalizaci\u00F3n dotada en el ejercicio")
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022LQ1032Key(Mod2002022Key[] keys, String description) {
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

