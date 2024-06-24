package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de buques y empresas navieras en Canarias: desglose de la compensación de bases imponibles negativas
public enum Mod2002023LQ243Key implements Serializable, IMod200KeysProvider  {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002023Key[]{Mod2002023Key.LQ168 , Mod2002023Key.LQ172 , Mod2002023Key.LQ173 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2021")
	,C02(new Mod2002023Key[]{Mod2002023Key.LQ175 , Mod2002023Key.LQ176 , Mod2002023Key.LQ177 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2021")
	,C03(new Mod2002023Key[]{Mod2002023Key.LQ178 , Mod2002023Key.LQ179 , Mod2002023Key.LQ198 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2022")
	,C04(new Mod2002023Key[]{Mod2002023Key.LQ202 , Mod2002023Key.LQ214 , Mod2002023Key.LQ215 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2022")
	,C05(new Mod2002023Key[]{Mod2002023Key.LQ987 , Mod2002023Key.LQ988 , Mod2002023Key.LQ989 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2023(*)")
	,C06(new Mod2002023Key[]{Mod2002023Key.LQ1010, Mod2002023Key.LQ1177, Mod2002023Key.LQ1200}, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2023(*)")
	,C07(new Mod2002023Key[]{Mod2002023Key.LQ1886, Mod2002023Key.LQ1887, Mod2002023Key.LQ1888}, "Subtotal de compensaci\u00F3n de base imponible especial") 
	,C08(new Mod2002023Key[]{Mod2002023Key.LQ1889, Mod2002023Key.LQ1890, Mod2002023Key.LQ1891}, "Subtotal de compensaci\u00F3n de base imponible resto actividades")
	,C09(new Mod2002023Key[]{Mod2002023Key.LQ216 , Mod2002023Key.LQ243 , Mod2002023Key.LQ265 }, "Total")
	,C10(new Mod2002023Key[]{Mod2002023Key.LQ266 , null		           , Mod2002023Key.LQ267 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2023")
	,C11(new Mod2002023Key[]{Mod2002023Key.LQ290 , null            	   , Mod2002023Key.LQ344 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2023")
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023LQ243Key(Mod2002023Key[] keys, String description) {
		this.keys = keys;
		this.description = description;	
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public Mod2002023Key[] getKeys() {
		return keys; 
	}

}
