package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import java.io.Serializable;

// Información Casillas 403 y 404
// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994)
public enum Mod2002019I403D404Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002019Key[]{Mod2002019Key.RC089,Mod2002019Key.RC094,Mod2002019Key.RC095,null               },"RIC 2015. Dotaci\u00F3n y materializaciones efectuadas en 2015")
	 ,C02(new Mod2002019Key[]{Mod2002019Key.RC097,Mod2002019Key.RC098,Mod2002019Key.RC047,Mod2002019Key.RC048},"RIC 2016. Dotaci\u00F3n y materializaciones efectuadas en 2016")
	 ,C03(new Mod2002019Key[]{Mod2002019Key.RC524,Mod2002019Key.RC525,Mod2002019Key.RC526,Mod2002019Key.RC527},"RIC 2017. Dotaci\u00F3n y materializaciones efectuadas en 2017")
	 ,C04(new Mod2002019Key[]{Mod2002019Key.RC922,Mod2002019Key.RC923,Mod2002019Key.RC924,Mod2002019Key.RC925},"RIC 2018. Dotaci\u00F3n y materializaciones efectuadas en 2018")
	 ,C05(new Mod2002019Key[]{Mod2002019Key.RC927,Mod2002019Key.RC928,Mod2002019Key.RC938,Mod2002019Key.RC996},"RIC 2019. Dotaci\u00F3n y materializaciones efectuadas en 2019")
	 ,C06(new Mod2002019Key[]{null               ,Mod2002019Key.RC020,Mod2002019Key.RC021,null               },"Inversiones anticipadas de futuras dotaciones a la RIC, efectuadas en 2019")	 
	;
	 
    private String description;
    
    private Mod2002019Key[] keys;
    
	private Mod2002019I403D404Key(Mod2002019Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002019Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
	
}

