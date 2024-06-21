package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Limitación en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir
public enum Mod2002023LM1212Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002023Key[]{null 				 ,Mod2002023Key.LM1188,Mod2002023Key.LM1189,null 				,Mod2002023Key.LM1191},"2012")
	,C02(new Mod2002023Key[]{null 				 ,Mod2002023Key.LM1193,Mod2002023Key.LM1194,null 				,Mod2002023Key.LM1196},"2013")
	,C03(new Mod2002023Key[]{null 				 ,Mod2002023Key.LM1198,Mod2002023Key.LM1199,null 				,Mod2002023Key.LM1201},"2014")
	,C04(new Mod2002023Key[]{Mod2002023Key.LM1202,Mod2002023Key.LM1203,Mod2002023Key.LM1204,Mod2002023Key.LM1205,Mod2002023Key.LM1206},"2015")
	,C05(new Mod2002023Key[]{Mod2002023Key.LM1462,Mod2002023Key.LM1463,Mod2002023Key.LM1209,Mod2002023Key.LM1210,Mod2002023Key.LM1211},"2016")
	,C06(new Mod2002023Key[]{Mod2002023Key.LM1736,Mod2002023Key.LM1737,Mod2002023Key.LM1464,Mod2002023Key.LM1465,Mod2002023Key.LM1466},"2017")
	,C07(new Mod2002023Key[]{Mod2002023Key.LM1977,Mod2002023Key.LM1978,Mod2002023Key.LM1738,Mod2002023Key.LM1739,Mod2002023Key.LM1740},"2018")
	,C08(new Mod2002023Key[]{Mod2002023Key.LM2253,Mod2002023Key.LM2254,Mod2002023Key.LM1979,Mod2002023Key.LM1980,Mod2002023Key.LM1981},"2019")
	,C09(new Mod2002023Key[]{Mod2002023Key.LM2399,Mod2002023Key.LM2400,Mod2002023Key.LM2255,Mod2002023Key.LM2256,Mod2002023Key.LM2257},"2020")
	,C10(new Mod2002023Key[]{Mod2002023Key.LM1098,Mod2002023Key.LM1099,Mod2002023Key.LM2401,Mod2002023Key.LM2402,Mod2002023Key.LM2403},"2021")
	,C11(new Mod2002023Key[]{Mod2002023Key.LM1393,Mod2002023Key.LM1394,Mod2002023Key.LM1100,Mod2002023Key.LM1101,Mod2002023Key.LM1102},"2022")
	,C12(new Mod2002023Key[]{Mod2002023Key.LM2764,Mod2002023Key.LM2765,Mod2002023Key.LM1395,Mod2002023Key.LM1396,Mod2002023Key.LM1397},"2023(*)")
	,C13(new Mod2002023Key[]{null                ,null                ,Mod2002023Key.LM2766,Mod2002023Key.LM2767,Mod2002023Key.LM2768},"2023(**)")
	,C14(new Mod2002023Key[]{Mod2002023Key.LM1212,Mod2002023Key.LM1213,Mod2002023Key.LM1214,Mod2002023Key.LM1215,Mod2002023Key.LM1216},"Total")
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023LM1212Key(Mod2002023Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002023Key[] getKeys() {
		return keys; 
	}

	
}

