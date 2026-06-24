package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de buques y empresas navieras en Canarias: desglose de la compensación de bases imponibles negativas
public enum Mod2002025LQ243Key implements Serializable, IMod200KeysProvider {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002025Key[]{Mod2002025Key.LQ168 , Mod2002025Key.LQ172 , Mod2002025Key.LQ173 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2021")
	,C02(new Mod2002025Key[]{Mod2002025Key.LQ175 , Mod2002025Key.LQ176 , Mod2002025Key.LQ177 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2021")
	,C03(new Mod2002025Key[]{Mod2002025Key.LQ178 , Mod2002025Key.LQ179 , Mod2002025Key.LQ198 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2022")
	,C04(new Mod2002025Key[]{Mod2002025Key.LQ202 , Mod2002025Key.LQ214 , Mod2002025Key.LQ215 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2022")
	,C05(new Mod2002025Key[]{Mod2002025Key.LQ987 , Mod2002025Key.LQ988 , Mod2002025Key.LQ989 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2023")
	,C06(new Mod2002025Key[]{Mod2002025Key.LQ1010, Mod2002025Key.LQ1177, Mod2002025Key.LQ1200}, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2023")
	,C07(new Mod2002025Key[]{Mod2002025Key.LQ033 , Mod2002025Key.LQ047 , Mod2002025Key.LQ091 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2024")
	,C08(new Mod2002025Key[]{Mod2002025Key.LQ092 , Mod2002025Key.LQ097 , Mod2002025Key.LQ098 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2024")
	,C09(new Mod2002025Key[]{Mod2002025Key.LQ3405, Mod2002025Key.LQ3406, Mod2002025Key.LQ3407}, "Compensaci\u00F3n de base imponible especial a\u00F1o 2025(*)")
	,C10(new Mod2002025Key[]{Mod2002025Key.LQ3408, Mod2002025Key.LQ3409, Mod2002025Key.LQ3410}, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2025(*)")
	,C11(new Mod2002025Key[]{Mod2002025Key.LQ1886, Mod2002025Key.LQ1887, Mod2002025Key.LQ1888}, "Subtotal de compensaci\u00F3n de base imponible especial") 
	,C12(new Mod2002025Key[]{Mod2002025Key.LQ1889, Mod2002025Key.LQ1890, Mod2002025Key.LQ1891}, "Subtotal de compensaci\u00F3n de base imponible resto actividades")
	,C13(new Mod2002025Key[]{Mod2002025Key.LQ216 , Mod2002025Key.LQ243 , Mod2002025Key.LQ265 }, "Total")
	,C14(new Mod2002025Key[]{Mod2002025Key.LQ266 , null		           , Mod2002025Key.LQ267 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2025")
	,C15(new Mod2002025Key[]{Mod2002025Key.LQ290 , null            	   , Mod2002025Key.LQ344 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2025")
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025LQ243Key(Mod2002025Key[] keys, String description) {
		this.keys = keys;
		this.description = description;	
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public Mod2002025Key[] getKeys() {
		return keys; 
	}

}
