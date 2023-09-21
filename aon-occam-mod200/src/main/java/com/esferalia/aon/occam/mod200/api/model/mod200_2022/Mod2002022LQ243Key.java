package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de buques y empresas navieras en Canarias: desglose de la compensación de bases imponibles negativas
public enum Mod2002022LQ243Key implements Serializable, IMod200KeysProvider  {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002022Key[]{Mod2002022Key.LQ168 , Mod2002022Key.LQ172 , Mod2002022Key.LQ173 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2021")
	,C02(new Mod2002022Key[]{Mod2002022Key.LQ175 , Mod2002022Key.LQ176 , Mod2002022Key.LQ177 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2021")
	,C03(new Mod2002022Key[]{Mod2002022Key.LQ178 , Mod2002022Key.LQ179 , Mod2002022Key.LQ198 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2022(*)")
	,C04(new Mod2002022Key[]{Mod2002022Key.LQ202 , Mod2002022Key.LQ214 , Mod2002022Key.LQ215 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2022(*)")
	,C05(new Mod2002022Key[]{Mod2002022Key.LQ1886, Mod2002022Key.LQ1887, Mod2002022Key.LQ1888}, "Subtotal de compensaci\u00F3n de base imponible especial") 
	,C06(new Mod2002022Key[]{Mod2002022Key.LQ1889, Mod2002022Key.LQ1890, Mod2002022Key.LQ1891}, "Subtotal de compensaci\u00F3n de base imponible resto actividades")
	,C07(new Mod2002022Key[]{Mod2002022Key.LQ216 , Mod2002022Key.LQ243 , Mod2002022Key.LQ265 }, "Total")
	,C08(new Mod2002022Key[]{Mod2002022Key.LQ266 , null		           , Mod2002022Key.LQ267 }, "Compensaci\u00F3n de base imponible especial a\u00F1o 2022")
	,C09(new Mod2002022Key[]{Mod2002022Key.LQ290 , Mod2002022Key.LQ2465, Mod2002022Key.LQ344 }, "Compensaci\u00F3n de base imponible resto actividades a\u00F1o 2022")
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022LQ243Key(Mod2002022Key[] keys, String description) {
		this.keys = keys;
		this.description = description;	
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public Mod2002022Key[] getKeys() {
		return keys; 
	}

}
