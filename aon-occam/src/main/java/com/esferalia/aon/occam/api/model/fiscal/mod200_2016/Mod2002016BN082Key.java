package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import java.io.Serializable;

// Deducciones I + D + i excluidas de límite. Opción art. 39.2 LIS
public enum Mod2002016BN082Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002016Key[]{Mod2002016Key.BN918 ,Mod2002016Key.BN919 ,Mod2002016Key.BN574 ,Mod2002016Key.BN580 },"2013: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C02(new Mod2002016Key[]{Mod2002016Key.BN589 ,Mod2002016Key.BN976 ,Mod2002016Key.BN977 ,Mod2002016Key.BN978 },"2013: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C03(new Mod2002016Key[]{Mod2002016Key.BN822 ,Mod2002016Key.BN823 ,Mod2002016Key.BN824 ,Mod2002016Key.BN231 },"2014: Investigaci\u00F3n y desarrollo (CTE)")
	 ,C04(new Mod2002016Key[]{Mod2002016Key.BN232 ,Mod2002016Key.BN233 ,Mod2002016Key.BN850 ,Mod2002016Key.BN851 },"2014: Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C05(new Mod2002016Key[]{Mod2002016Key.BN1123,Mod2002016Key.BN1124,Mod2002016Key.BN1125,Mod2002016Key.BN1126},"2015(*): Investigaci\u00F3n y desarrollo (CTE)")
	 ,C06(new Mod2002016Key[]{Mod2002016Key.BN1127,Mod2002016Key.BN1128,Mod2002016Key.BN1129,Mod2002016Key.BN1130},"2015(*): Innovaci\u00F3n tecnol\u00F3gica (ITE)")
	 ,C07(new Mod2002016Key[]{Mod2002016Key.BN517 ,Mod2002016Key.BN081 ,null /* BN082 */    ,Mod2002016Key.BN1234},"Total")
	;
	 
    private String description;
    
    private Mod2002016Key[] keys;
    
	private Mod2002016BN082Key(Mod2002016Key[] keys, String description) {
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

