package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Reserva de nivelación
public enum Mod2002015LQ1033_2Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	// FALTA  por ver como serán los calculos de este desglose, para ver si puedo poner	
	// los dos apartados en el mismo enumerado o es necesario poner dos enumerados
	
	// Dotación de la reserva                
	 C01(new Mod2002015Key[]{Mod2002015Key.LQ1150,Mod2002015Key.LQ1151,Mod2002015Key.LQ1152,Mod2002015Key.LQ1153},"2015(*)")
	,C02(new Mod2002015Key[]{Mod2002015Key.LQ1154,Mod2002015Key.LQ1155,Mod2002015Key.LQ1156,Mod2002015Key.LQ1157},"2015")
	,C03(new Mod2002015Key[]{Mod2002015Key.LQ1158,Mod2002015Key.LQ1159,Mod2002015Key.LQ1160,Mod2002015Key.LQ1161},"Total")
	
	;
	 
    private String description;
    private Mod2002015Key[] keys;

	private Mod2002015LQ1033_2Key(Mod2002015Key[] keys, String description) {
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

