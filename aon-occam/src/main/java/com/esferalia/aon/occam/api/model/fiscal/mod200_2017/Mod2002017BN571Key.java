package com.esferalia.aon.occam.api.model.fiscal.mod200_2017;

import java.io.Serializable;

// Deducciones doble imposición internacional LIS. DI internac. períodos anteriores
public enum Mod2002017BN571Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002017Key[]{Mod2002017Key.BN1054,Mod2002017Key.BN1050,Mod2002017Key.BN1051,Mod2002017Key.BN1052,Mod2002017Key.BN1053},"DI internacional 2015")
	,C02(new Mod2002017Key[]{Mod2002017Key.BN1348,Mod2002017Key.BN1349,Mod2002017Key.BN1350,Mod2002017Key.BN1351,Mod2002017Key.BN1352},"DI internacional 2016")
	,C03(new Mod2002017Key[]{Mod2002017Key.BN1770,Mod2002017Key.BN1771,Mod2002017Key.BN1772,Mod2002017Key.BN1773,Mod2002017Key.BN1774},"DI internacional 2017(*)")
	,C04(new Mod2002017Key[]{Mod2002017Key.BN131 ,null 				  ,Mod2002017Key.BN132 ,null /* BN571 */    ,Mod2002017Key.BN133 },"Total")
	,C05(new Mod2002017Key[]{null  				 ,Mod2002017Key.BN103D,null  			   ,null  				,null  				 },"Tipo de gravamen 2017")
	;
	 
    private String description;
    private Mod2002017Key[] keys;

	private Mod2002017BN571Key(Mod2002017Key[] keys, String description) {
	    this.keys = keys; 
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002017Key[] getKeys() {
		return keys;
	}

}

