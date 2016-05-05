package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// PENDIENTE DE ADICIÓN POR LÍMITE BENEFICIO OPERATIVO NO APLICADO 
public enum Mod2002015LM539Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002015Key[]{Mod2002015Key.LM890 ,Mod2002015Key.LM891 ,Mod2002015Key.LM892 },"2012")
	 ,C02(new Mod2002015Key[]{Mod2002015Key.LM503 ,Mod2002015Key.LM522 ,Mod2002015Key.LM523 },"2013")
	 ,C03(new Mod2002015Key[]{Mod2002015Key.LM273 ,Mod2002015Key.LM274 ,Mod2002015Key.LM537 },"2014")
	 ,C04(new Mod2002015Key[]{Mod2002015Key.LM955 ,Mod2002015Key.LM956 ,Mod2002015Key.LM957 },"2015(*)")
	 ,C05(new Mod2002015Key[]{Mod2002015Key.LM1217,Mod2002015Key.LM1218,Mod2002015Key.LM1219},"2015(**)")
	 ,C06(new Mod2002015Key[]{Mod2002015Key.LM538 ,Mod2002015Key.LM539 ,Mod2002015Key.LM546 },"Total")	 
	;
	 
    private String description;
    
    private Mod2002015Key[] keys;
    
	private Mod2002015LM539Key(Mod2002015Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002015Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

