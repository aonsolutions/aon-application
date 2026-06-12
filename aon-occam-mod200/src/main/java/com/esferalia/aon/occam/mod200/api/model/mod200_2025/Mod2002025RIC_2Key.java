package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) - Inversiones anticipadas
public enum Mod2002025RIC_2Key implements Serializable, IMod200KeysProvider {

	 C01(new Mod2002025Key[]{Mod2002025Key.RC3647,null				  ,null   			   ,null                },"Inversiones anticipadas 2022")
	,C02(new Mod2002025Key[]{Mod2002025Key.RC3648,null				  ,null				   ,Mod2002025Key.RC3649},"Inversiones anticipadas 2023")
	,C03(new Mod2002025Key[]{Mod2002025Key.RC3629,null				  ,null                ,Mod2002025Key.RC3354},"Inversiones anticipadas 2024")
	,C04(new Mod2002025Key[]{null				 ,Mod2002025Key.RC3630,Mod2002025Key.RC3631,Mod2002025Key.RC3632},"Inversiones anticipadas 2025")
	;
	 
    private String description;
    private Mod2002025Key[] keys;
    
	private Mod2002025RIC_2Key(Mod2002025Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002025Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
	
}
