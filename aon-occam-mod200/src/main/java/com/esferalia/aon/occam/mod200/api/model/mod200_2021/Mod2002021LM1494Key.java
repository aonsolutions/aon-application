package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
// vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible
public enum Mod2002021LM1494Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002021Key[]{Mod2002021Key.LM1473,Mod2002021Key.LM1408,Mod2002021Key.LM1474,Mod2002021Key.LM1475,Mod2002021Key.LM1476,Mod2002021Key.LM1409},"2007 y anteriores")
	,C02(new Mod2002021Key[]{Mod2002021Key.LM1477,Mod2002021Key.LM1478,Mod2002021Key.LM1481,Mod2002021Key.LM1482,Mod2002021Key.LM1483,Mod2002021Key.LM1484},"2008 a 2015")
	,C03(new Mod2002021Key[]{Mod2002021Key.LM1485,Mod2002021Key.LM1486,Mod2002021Key.LM1487,Mod2002021Key.LM1488,Mod2002021Key.LM1489,Mod2002021Key.LM1490},"2016")
	,C04(new Mod2002021Key[]{Mod2002021Key.LM1491,Mod2002021Key.LM1747,Mod2002021Key.LM1748,Mod2002021Key.LM1492,Mod2002021Key.LM1493,Mod2002021Key.LM1749},"2017")
    ,C05(new Mod2002021Key[]{Mod2002021Key.LM1750,Mod2002021Key.LM1988,Mod2002021Key.LM1989,Mod2002021Key.LM1751,Mod2002021Key.LM1752,Mod2002021Key.LM1990},"2018")
	,C06(new Mod2002021Key[]{Mod2002021Key.LM1991,Mod2002021Key.LM2261,Mod2002021Key.LM2262,Mod2002021Key.LM1992,Mod2002021Key.LM1993,Mod2002021Key.LM2263},"2019")
	,C07(new Mod2002021Key[]{Mod2002021Key.LM2264,Mod2002021Key.LM2431,Mod2002021Key.LM2432,Mod2002021Key.LM2265,Mod2002021Key.LM2266,Mod2002021Key.LM2433},"2020")
	,C08(new Mod2002021Key[]{Mod2002021Key.LM2434,Mod2002021Key.LM1143,Mod2002021Key.LM1148,Mod2002021Key.LM2435,Mod2002021Key.LM2436,Mod2002021Key.LM1192},"2021(*)")
	,C09(new Mod2002021Key[]{Mod2002021Key.LM1162,null                , null               ,Mod2002021Key.LM1163,Mod2002021Key.LM1164,null                },"2021")
	,C10(new Mod2002021Key[]{Mod2002021Key.LM1494,Mod2002021Key.LM1495,Mod2002021Key.LM1496,Mod2002021Key.LM1497,Mod2002021Key.LM1498,Mod2002021Key.LM1499},"Total")
	;
	 
    private String description;
    private Mod2002021Key[] keys;

	private Mod2002021LM1494Key(Mod2002021Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002021Key[] getKeys() {
		return keys; 
	}

	
}

