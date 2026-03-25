package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
// vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible
public enum Mod2002025LM1494Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002025Key[]{Mod2002025Key.LM1473,Mod2002025Key.LM1408,Mod2002025Key.LM1474,Mod2002025Key.LM1475,Mod2002025Key.LM1476,Mod2002025Key.LM1409},"2007 y anteriores")
	,C02(new Mod2002025Key[]{Mod2002025Key.LM1477,Mod2002025Key.LM1478,Mod2002025Key.LM1481,Mod2002025Key.LM1482,Mod2002025Key.LM1483,Mod2002025Key.LM1484},"2008 a 2015")
	,C03(new Mod2002025Key[]{Mod2002025Key.LM1485,Mod2002025Key.LM1486,Mod2002025Key.LM1487,Mod2002025Key.LM1488,Mod2002025Key.LM1489,Mod2002025Key.LM1490},"2016")
	,C04(new Mod2002025Key[]{Mod2002025Key.LM1491,Mod2002025Key.LM1747,Mod2002025Key.LM1748,Mod2002025Key.LM1492,Mod2002025Key.LM1493,Mod2002025Key.LM1749},"2017")
    ,C05(new Mod2002025Key[]{Mod2002025Key.LM1750,Mod2002025Key.LM1988,Mod2002025Key.LM1989,Mod2002025Key.LM1751,Mod2002025Key.LM1752,Mod2002025Key.LM1990},"2018")
	,C06(new Mod2002025Key[]{Mod2002025Key.LM1991,Mod2002025Key.LM2261,Mod2002025Key.LM2262,Mod2002025Key.LM1992,Mod2002025Key.LM1993,Mod2002025Key.LM2263},"2019")
	,C07(new Mod2002025Key[]{Mod2002025Key.LM2264,Mod2002025Key.LM2431,Mod2002025Key.LM2432,Mod2002025Key.LM2265,Mod2002025Key.LM2266,Mod2002025Key.LM2433},"2020")
	,C08(new Mod2002025Key[]{Mod2002025Key.LM2434,Mod2002025Key.LM1143,Mod2002025Key.LM1148,Mod2002025Key.LM2435,Mod2002025Key.LM2436,Mod2002025Key.LM1192},"2021")
	,C09(new Mod2002025Key[]{Mod2002025Key.LM1162,Mod2002025Key.LM1470,Mod2002025Key.LM1471,Mod2002025Key.LM1163,Mod2002025Key.LM1164,Mod2002025Key.LM1915},"2022")
	,C10(new Mod2002025Key[]{Mod2002025Key.LM1479,Mod2002025Key.LM2800,Mod2002025Key.LM2802,Mod2002025Key.LM1480,Mod2002025Key.LM1500,Mod2002025Key.LM2803},"2023")
	,C11(new Mod2002025Key[]{Mod2002025Key.LM2804,Mod2002025Key.LM1217,Mod2002025Key.LM1218,Mod2002025Key.LM2805,Mod2002025Key.LM2806,Mod2002025Key.LM1219},"2025(*)")
	,C12(new Mod2002025Key[]{Mod2002025Key.LM1626,null                , null               ,Mod2002025Key.LM1627,Mod2002025Key.LM1628,null                },"2025")
	,C13(new Mod2002025Key[]{Mod2002025Key.LM1494,Mod2002025Key.LM1495,Mod2002025Key.LM1496,Mod2002025Key.LM1497,Mod2002025Key.LM1498,Mod2002025Key.LM1499},"Total")
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025LM1494Key(Mod2002025Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002025Key[] getKeys() {
		return keys; 
	}

	
}

