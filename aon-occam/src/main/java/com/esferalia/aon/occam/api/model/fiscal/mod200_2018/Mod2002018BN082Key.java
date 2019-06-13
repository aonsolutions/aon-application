package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Desglose Casilla 082
// Deducciones I + D + i excluidas de límite. Opción art. 39.2 LIS
public enum Mod2002018BN082Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002018Key[]{Mod2002018Key.BN918 ,Mod2002018Key.BN919 ,Mod2002018Key.BN574 ,Mod2002018Key.BN580 ,Mod2002018Key.BN580R  },"2013: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C02(new Mod2002018Key[]{Mod2002018Key.BN589 ,Mod2002018Key.BN976 ,Mod2002018Key.BN977 ,Mod2002018Key.BN978 ,Mod2002018Key.BN978R  },"2013: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C03(new Mod2002018Key[]{Mod2002018Key.BN822 ,Mod2002018Key.BN823 ,Mod2002018Key.BN824 ,Mod2002018Key.BN231 ,Mod2002018Key.BN231R  },"2014: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C04(new Mod2002018Key[]{Mod2002018Key.BN232 ,Mod2002018Key.BN233 ,Mod2002018Key.BN850 ,Mod2002018Key.BN851 ,Mod2002018Key.BN851R  },"2014: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C05(new Mod2002018Key[]{Mod2002018Key.BN1123,Mod2002018Key.BN1124,Mod2002018Key.BN1125,Mod2002018Key.BN1126,Mod2002018Key.BN1126R },"2015: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C06(new Mod2002018Key[]{Mod2002018Key.BN1127,Mod2002018Key.BN1128,Mod2002018Key.BN1129,Mod2002018Key.BN1130,Mod2002018Key.BN1130R },"2015: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C07(new Mod2002018Key[]{Mod2002018Key.BN1426,Mod2002018Key.BN1427,Mod2002018Key.BN1428,Mod2002018Key.BN1429,Mod2002018Key.BN1429R },"2016: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C08(new Mod2002018Key[]{Mod2002018Key.BN1430,Mod2002018Key.BN1431,Mod2002018Key.BN1432,Mod2002018Key.BN1433,Mod2002018Key.BN1433R },"2016: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C09(new Mod2002018Key[]{Mod2002018Key.BN1710,Mod2002018Key.BN1711,Mod2002018Key.BN1712,Mod2002018Key.BN1713,Mod2002018Key.BN1713R },"2017: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C10(new Mod2002018Key[]{Mod2002018Key.BN1714,Mod2002018Key.BN1715,Mod2002018Key.BN1716,Mod2002018Key.BN1717,Mod2002018Key.BN1717R },"2017: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C11(new Mod2002018Key[]{Mod2002018Key.BN1968,Mod2002018Key.BN1969,Mod2002018Key.BN1970,Mod2002018Key.BN1971,Mod2002018Key.BN1971R },"2018(*): Investigaci\u00F3n y desarrollo (CTE)")
	 ,C12(new Mod2002018Key[]{Mod2002018Key.BN1972,Mod2002018Key.BN1973,Mod2002018Key.BN1974,Mod2002018Key.BN1975,Mod2002018Key.BN1975R },"2018(*): Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C13(new Mod2002018Key[]{Mod2002018Key.BN517 ,Mod2002018Key.BN081 ,Mod2002018Key.BN082 ,Mod2002018Key.BN1234A,null},"Total")
	;
	 
    private String description;
    
    private Mod2002018Key[] keys;
    
	private Mod2002018BN082Key(Mod2002018Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002018Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

