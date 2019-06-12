package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Deducciones doble imposición interna (DT 23ª.1 LIS) de Ejercicios anteriores
public enum Mod2002018BN1344Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002018Key[]{Mod2002018Key.BN101 	,Mod2002018Key.BN102 	,Mod2002018Key.BN119 	,Mod2002018Key.BN120	,Mod2002018Key.BN121 	},"D.I. interna 2015")
	,C02(new Mod2002018Key[]{Mod2002018Key.BN122 	,Mod2002018Key.BN123	,Mod2002018Key.BN124 	,Mod2002018Key.BN125	,Mod2002018Key.BN126 	},"D.I. interna 2016")
	,C03(new Mod2002018Key[]{Mod2002018Key.BN1595 	,Mod2002018Key.BN1596 	,Mod2002018Key.BN1597 	,Mod2002018Key.BN1598	,Mod2002018Key.BN1599 	},"D.I. interna 2017")
	,C04(new Mod2002018Key[]{Mod2002018Key.BN1828 	,Mod2002018Key.BN1829 	,Mod2002018Key.BN1830 	,Mod2002018Key.BN1831	,Mod2002018Key.BN1832 	},"D.I. interna 2018(*)")
	,C05(new Mod2002018Key[]{Mod2002018Key.BN1342	,null				  	,Mod2002018Key.BN1343	,Mod2002018Key.BN1344 	,Mod2002018Key.BN1345	},"Total")
	,C06(new Mod2002018Key[]{null				 	,Mod2002018Key.BN103B	,null					,null 			  		,null 			   		},"Tipo de gravamen 2018")
	;
	 
    private String description;
    private Mod2002018Key[] keys;

	private Mod2002018BN1344Key(Mod2002018Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002018Key[] getKeys() {
		return keys;
	}

}
