package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Desglose Casilla 082
// Deducciones I + D + i excluidas de límite. Opción art. 39.2 LIS
public enum Mod2002024BN082Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002024Key[]{Mod2002024Key.BN918 ,Mod2002024Key.BN919 ,Mod2002024Key.BN574 ,Mod2002024Key.BN580 ,Mod2002024Key.BN580R  },"2013: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C02(new Mod2002024Key[]{Mod2002024Key.BN589 ,Mod2002024Key.BN976 ,Mod2002024Key.BN977 ,Mod2002024Key.BN978 ,Mod2002024Key.BN978R  },"2013: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C03(new Mod2002024Key[]{Mod2002024Key.BN822 ,Mod2002024Key.BN823 ,Mod2002024Key.BN824 ,Mod2002024Key.BN231 ,Mod2002024Key.BN231R  },"2014: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C04(new Mod2002024Key[]{Mod2002024Key.BN232 ,Mod2002024Key.BN233 ,Mod2002024Key.BN850 ,Mod2002024Key.BN851 ,Mod2002024Key.BN851R  },"2014: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C05(new Mod2002024Key[]{Mod2002024Key.BN1123,Mod2002024Key.BN1124,Mod2002024Key.BN1125,Mod2002024Key.BN1126,Mod2002024Key.BN1126R },"2015: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C06(new Mod2002024Key[]{Mod2002024Key.BN1127,Mod2002024Key.BN1128,Mod2002024Key.BN1129,Mod2002024Key.BN1130,Mod2002024Key.BN1130R },"2015: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C07(new Mod2002024Key[]{Mod2002024Key.BN1426,Mod2002024Key.BN1427,Mod2002024Key.BN1428,Mod2002024Key.BN1429,Mod2002024Key.BN1429R },"2016: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C08(new Mod2002024Key[]{Mod2002024Key.BN1430,Mod2002024Key.BN1431,Mod2002024Key.BN1432,Mod2002024Key.BN1433,Mod2002024Key.BN1433R },"2016: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C09(new Mod2002024Key[]{Mod2002024Key.BN1710,Mod2002024Key.BN1711,Mod2002024Key.BN1712,Mod2002024Key.BN1713,Mod2002024Key.BN1713R },"2017: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C10(new Mod2002024Key[]{Mod2002024Key.BN1714,Mod2002024Key.BN1715,Mod2002024Key.BN1716,Mod2002024Key.BN1717,Mod2002024Key.BN1717R },"2017: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C11(new Mod2002024Key[]{Mod2002024Key.BN1968,Mod2002024Key.BN1969,Mod2002024Key.BN1970,Mod2002024Key.BN1971,Mod2002024Key.BN1971R },"2018: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C12(new Mod2002024Key[]{Mod2002024Key.BN1972,Mod2002024Key.BN1973,Mod2002024Key.BN1974,Mod2002024Key.BN1975,Mod2002024Key.BN1975R },"2018: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C13(new Mod2002024Key[]{Mod2002024Key.BN2245,Mod2002024Key.BN2246,Mod2002024Key.BN2247,Mod2002024Key.BN2248,Mod2002024Key.BN2248R },"2019: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C14(new Mod2002024Key[]{Mod2002024Key.BN2249,Mod2002024Key.BN2250,Mod2002024Key.BN2251,Mod2002024Key.BN2252,Mod2002024Key.BN2252R },"2019: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C15(new Mod2002024Key[]{Mod2002024Key.BN2391,Mod2002024Key.BN2392,Mod2002024Key.BN2393,Mod2002024Key.BN2394,Mod2002024Key.BN2394R },"2020: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C16(new Mod2002024Key[]{Mod2002024Key.BN2395,Mod2002024Key.BN2396,Mod2002024Key.BN2397,Mod2002024Key.BN2398,Mod2002024Key.BN2398R },"2020: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C17(new Mod2002024Key[]{Mod2002024Key.BN1090,Mod2002024Key.BN1091,Mod2002024Key.BN1092,Mod2002024Key.BN1093,Mod2002024Key.BN1093R },"2021: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C18(new Mod2002024Key[]{Mod2002024Key.BN1094,Mod2002024Key.BN1095,Mod2002024Key.BN1096,Mod2002024Key.BN1097,Mod2002024Key.BN1097R },"2021: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C19(new Mod2002024Key[]{Mod2002024Key.BN1385,Mod2002024Key.BN1386,Mod2002024Key.BN1387,Mod2002024Key.BN1388,Mod2002024Key.BN1388R },"2022: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C20(new Mod2002024Key[]{Mod2002024Key.BN1389,Mod2002024Key.BN1390,Mod2002024Key.BN1391,Mod2002024Key.BN1392,Mod2002024Key.BN1392R },"2022: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C21(new Mod2002024Key[]{Mod2002024Key.BN2709,Mod2002024Key.BN2710,Mod2002024Key.BN2757,Mod2002024Key.BN2758,Mod2002024Key.BN2758R },"2024(*): Investigaci\u00F3n y desarrollo (CTE)")
	 ,C22(new Mod2002024Key[]{Mod2002024Key.BN2759,Mod2002024Key.BN2760,Mod2002024Key.BN2762,Mod2002024Key.BN2763,Mod2002024Key.BN2763R },"2024(*): Innovaci\u00F3n tecnol\u00F3gica (ITE)")
 	 ,C23(new Mod2002024Key[]{Mod2002024Key.BN517 ,Mod2002024Key.BN081 ,Mod2002024Key.BN082 ,Mod2002024Key.BN1234A,null                 },"Total")
 	 ,C24(new Mod2002024Key[]{Mod2002024Key.BN814 ,null                ,null                ,null                 ,null                 },"Gastos de investigaci\u00F3n y desarrollo del per\u00EDodo impositivo") 
 	 ,C25(new Mod2002024Key[]{Mod2002024Key.BN1935,null                ,null                ,null                 ,null                 },"Importe anual de la deducci\u00F3n por gastos de investigaci\u00F3n y desarrollo aplicada o abonada por el resto del grupo")
 	 ,C26(new Mod2002024Key[]{Mod2002024Key.BN130 ,null                ,null                ,null                 ,null                 },"Importe anual de la deducci\u00F3n por gastos en actividades de innovaci\u00F3n tecnol\u00F3gica aplicada o abonada por el resto del grupo")
	;
	 
    private String description;    
    private Mod2002024Key[] keys;
    
	private Mod2002024BN082Key(Mod2002024Key[] keys, String description) {
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

