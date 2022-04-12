package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Reserva de capitalización
public enum Mod2002015LQ1032Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002015Key[]{Mod2002015Key.LQ1131,Mod2002015Key.LQ1132,Mod2002015Key.LQ1133},"2015(*)")
	,C02(new Mod2002015Key[]{Mod2002015Key.LQ1134,Mod2002015Key.LQ1135,Mod2002015Key.LQ1136},"2015")
	,C03(new Mod2002015Key[]{Mod2002015Key.LQ1137,null                ,Mod2002015Key.LQ1139},"Total")
	,C04(new Mod2002015Key[]{Mod2002015Key.LQ1140,null                ,null                },"Reserva de capitalizaci\u00F3n dotada en el ejercicio")
	;
	 
    private String description;
    private Mod2002015Key[] keys;

	private Mod2002015LQ1032Key(Mod2002015Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002015Key[] getKeys() {
		return keys; 
	}
}

