package com.esferalia.aon.occam.api.model.fiscal.mod200_2017;

import java.io.Serializable;

// Deducciones doble imposición interna (DT 23ª.1 LIS) de Ejercicios anteriores
public enum Mod2002017BN1344Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002017Key[]{Mod2002017Key.BN101 	,Mod2002017Key.BN102 	,Mod2002017Key.BN119 	,Mod2002017Key.BN120	,Mod2002017Key.BN121 	},"D.I. interna 2015")
	,C02(new Mod2002017Key[]{Mod2002017Key.BN122 	,Mod2002017Key.BN123	,Mod2002017Key.BN124 	,Mod2002017Key.BN125	,Mod2002017Key.BN126 	},"D.I. interna 2016")
	,C03(new Mod2002017Key[]{Mod2002017Key.BN1595 	,Mod2002017Key.BN1596 	,Mod2002017Key.BN1597 	,Mod2002017Key.BN1598	,Mod2002017Key.BN1599 	},"D.I. interna 2017(*)")
	,C04(new Mod2002017Key[]{Mod2002017Key.BN1342	,null				  	,Mod2002017Key.BN1343	,null /* BN1344 */  	,Mod2002017Key.BN1345	},"Total")
	,C05(new Mod2002017Key[]{null				 	,Mod2002017Key.BN103B	,null					,null 			  		,null 			   		},"Tipo de gravamen 2017")
	;
	 
    private String description;
    private Mod2002017Key[] keys;

	private Mod2002017BN1344Key(Mod2002017Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002017Key[] getKeys() {
		return keys;
	}

}
