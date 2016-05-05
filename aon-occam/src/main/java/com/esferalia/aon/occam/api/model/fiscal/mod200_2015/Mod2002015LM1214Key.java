package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// LIMITACIÓN EN LA DEDUCIBILIDAD DE GASTOS FINANCIEROS. GASTOS FINANCIEROS PENDIENTES DE DEDUCIR
public enum Mod2002015LM1214Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002015Key[]{Mod2002015Key.LM1187,Mod2002015Key.LM1188,Mod2002015Key.LM1189,Mod2002015Key.LM1190,Mod2002015Key.LM1191},"2012")
	 ,C02(new Mod2002015Key[]{Mod2002015Key.LM1192,Mod2002015Key.LM1193,Mod2002015Key.LM1194,Mod2002015Key.LM1195,Mod2002015Key.LM1196},"2013")
	 ,C03(new Mod2002015Key[]{Mod2002015Key.LM1197,Mod2002015Key.LM1198,Mod2002015Key.LM1199,Mod2002015Key.LM1200,Mod2002015Key.LM1201},"2014")
	 ,C04(new Mod2002015Key[]{Mod2002015Key.LM1202,Mod2002015Key.LM1203,Mod2002015Key.LM1204,Mod2002015Key.LM1205,Mod2002015Key.LM1206},"2015(*)")
	 ,C05(new Mod2002015Key[]{Mod2002015Key.LM1207,Mod2002015Key.LM1208,Mod2002015Key.LM1209,Mod2002015Key.LM1210,Mod2002015Key.LM1211},"2015(**)")
	 ,C06(new Mod2002015Key[]{Mod2002015Key.LM1212,Mod2002015Key.LM1213,Mod2002015Key.LM1214,Mod2002015Key.LM1215,Mod2002015Key.LM1216},"Total")	 
	;
	 
    private String description;
    
    private Mod2002015Key[] keys;
    
	private Mod2002015LM1214Key(Mod2002015Key[] keys, String description) {
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

