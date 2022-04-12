package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import java.io.Serializable;

// Información Casillas 403 y 403
// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994)
public enum Mod2002016I403D404Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002016Key[]{Mod2002016Key.RC089,Mod2002016Key.RC094,Mod2002016Key.RC095,null               },"RIC 2012. Dotaci\u00F3n y materializaciones efectuadas en 2012")
	 ,C02(new Mod2002016Key[]{Mod2002016Key.RC097,Mod2002016Key.RC098,Mod2002016Key.RC047,Mod2002016Key.RC048},"RIC 2013. Dotaci\u00F3n y materializaciones efectuadas en 2013")
	 ,C03(new Mod2002016Key[]{Mod2002016Key.RC524,Mod2002016Key.RC525,Mod2002016Key.RC526,Mod2002016Key.RC527},"RIC 2014. Dotaci\u00F3n y materializaciones efectuadas en 2014")
	 ,C04(new Mod2002016Key[]{Mod2002016Key.RC922,Mod2002016Key.RC923,Mod2002016Key.RC924,Mod2002016Key.RC925},"RIC 2015. Dotaci\u00F3n y materializaciones efectuadas en 2015")
	 ,C05(new Mod2002016Key[]{Mod2002016Key.RC927,Mod2002016Key.RC928,Mod2002016Key.RC938,Mod2002016Key.RC996},"RIC 2016. Dotaci\u00F3n y materializaciones efectuadas en 2016")
	 ,C06(new Mod2002016Key[]{null               ,Mod2002016Key.RC020,Mod2002016Key.RC021,null               },"Inversiones anticipadas de futuras dotaciones a la RIC, efectuadas en 2016")	 
	;
	 
    private String description;
    
    private Mod2002016Key[] keys;
    
	private Mod2002016I403D404Key(Mod2002016Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002016Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
	
}

