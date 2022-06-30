package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Limitación en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir
public enum Mod2002020LM1212Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002020Key[]{null 				 ,Mod2002020Key.LM1188,Mod2002020Key.LM1189,null 				 ,Mod2002020Key.LM1191},"2012")
	,C02(new Mod2002020Key[]{null 				 ,Mod2002020Key.LM1193,Mod2002020Key.LM1194,null 				 ,Mod2002020Key.LM1196},"2013")
	,C03(new Mod2002020Key[]{null 				 ,Mod2002020Key.LM1198,Mod2002020Key.LM1199,null 				 ,Mod2002020Key.LM1201},"2014")
	,C04(new Mod2002020Key[]{Mod2002020Key.LM1202,Mod2002020Key.LM1203,Mod2002020Key.LM1204,Mod2002020Key.LM1205 ,Mod2002020Key.LM1206},"2015")
	,C05(new Mod2002020Key[]{Mod2002020Key.LM1462,Mod2002020Key.LM1463,Mod2002020Key.LM1209,Mod2002020Key.LM1210 ,Mod2002020Key.LM1211},"2016")
	,C06(new Mod2002020Key[]{Mod2002020Key.LM1736,Mod2002020Key.LM1737,Mod2002020Key.LM1464,Mod2002020Key.LM1465 ,Mod2002020Key.LM1466},"2017")
	,C07(new Mod2002020Key[]{Mod2002020Key.LM1977,Mod2002020Key.LM1978,Mod2002020Key.LM1738,Mod2002020Key.LM1739 ,Mod2002020Key.LM1740},"2018")
	,C08(new Mod2002020Key[]{Mod2002020Key.LM2253,Mod2002020Key.LM2254,Mod2002020Key.LM1979,Mod2002020Key.LM1980 ,Mod2002020Key.LM1981},"2019")
	,C09(new Mod2002020Key[]{Mod2002020Key.LM2399,Mod2002020Key.LM2400,Mod2002020Key.LM2255,Mod2002020Key.LM2256 ,Mod2002020Key.LM2257},"2020(*)")
	,C10(new Mod2002020Key[]{null				 ,null				  ,Mod2002020Key.LM2401,Mod2002020Key.LM2402 ,Mod2002020Key.LM2403},"2020(**)")
	,C11(new Mod2002020Key[]{Mod2002020Key.LM1212,Mod2002020Key.LM1213,Mod2002020Key.LM1214,Mod2002020Key.LM1215 ,Mod2002020Key.LM1216},"Total")
	;
	 
    private String description;
    private Mod2002020Key[] keys;

	private Mod2002020LM1212Key(Mod2002020Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002020Key[] getKeys() {
		return keys; 
	}

	
}

