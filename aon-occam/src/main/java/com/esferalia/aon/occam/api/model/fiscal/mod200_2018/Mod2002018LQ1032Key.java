package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Reserva de capitalización
public enum Mod2002018LQ1032Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002018Key[]{Mod2002018Key.LQ1134,Mod2002018Key.LQ1135,null                },"2016")
	,C02(new Mod2002018Key[]{Mod2002018Key.LQ1470,Mod2002018Key.LQ1471,Mod2002018Key.LQ1472},"2017")	
	,C03(new Mod2002018Key[]{Mod2002018Key.LQ1744,Mod2002018Key.LQ1745,Mod2002018Key.LQ1746},"2018(*)")
	,C04(new Mod2002018Key[]{Mod2002018Key.LQ1985,Mod2002018Key.LQ1986,Mod2002018Key.LQ1987},"2018")
	,C05(new Mod2002018Key[]{Mod2002018Key.LQ1137,Mod2002018Key.LQ1032,Mod2002018Key.LQ1139},"Total")
	,C06(new Mod2002018Key[]{Mod2002018Key.LQ1140,null                ,null                },"Reserva de capitalizaci\u00F3n dotada en el ejercicio")
	;
	 
    private String description;
    private Mod2002018Key[] keys;

	private Mod2002018LQ1032Key(Mod2002018Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002018Key[] getKeys() {
		return keys; 
	}
}

