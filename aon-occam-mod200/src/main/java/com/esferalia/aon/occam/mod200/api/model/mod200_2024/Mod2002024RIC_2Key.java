package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - Inversiones anticipadas
public enum Mod2002024RIC_2Key implements Serializable, IMod200KeysProvider {

	 C01(new Mod2002024Key[]{Mod2002024Key.RC1176,null				  ,null			 	   ,null                },"Inversiones anticipadas 2020")
	,C02(new Mod2002024Key[]{null				 ,null				  ,null			 	   ,null                },"Inversiones anticipadas 2021")	
	,C03(new Mod2002024Key[]{null				 ,null				  ,null   			   ,null                },"Inversiones anticipadas 2022")
	,C04(new Mod2002024Key[]{null				 ,null				  ,null				   ,null                },"Inversiones anticipadas 2023")
	,C05(new Mod2002024Key[]{null				 ,Mod2002024Key.RC3352,Mod2002024Key.RC3353,Mod2002024Key.RC3354},"Inversiones anticipadas 2024")
	;
	 
    private String description;
    private Mod2002024Key[] keys;
    
	private Mod2002024RIC_2Key(Mod2002024Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002024Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
	
}
