package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición interna (DT 23ª.1 LIS) de Ejercicios anteriores
public enum Mod2002022BN1344Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002022Key[]{Mod2002022Key.BN101 	,Mod2002022Key.BN102 	,Mod2002022Key.BN119 	,Mod2002022Key.BN120	,Mod2002022Key.BN121 	},"D.I. interna 2015")
	,C02(new Mod2002022Key[]{Mod2002022Key.BN122 	,Mod2002022Key.BN123	,Mod2002022Key.BN124 	,Mod2002022Key.BN125	,Mod2002022Key.BN126 	},"D.I. interna 2016")
	,C03(new Mod2002022Key[]{Mod2002022Key.BN1595 	,Mod2002022Key.BN1596 	,Mod2002022Key.BN1597 	,Mod2002022Key.BN1598	,Mod2002022Key.BN1599 	},"D.I. interna 2017")
	,C04(new Mod2002022Key[]{Mod2002022Key.BN1828 	,Mod2002022Key.BN1829 	,Mod2002022Key.BN1830 	,Mod2002022Key.BN1831	,Mod2002022Key.BN1832 	},"D.I. interna 2018")
	,C05(new Mod2002022Key[]{Mod2002022Key.BN2196 	,Mod2002022Key.BN2197 	,Mod2002022Key.BN2198 	,Mod2002022Key.BN2199	,Mod2002022Key.BN2200 	},"D.I. interna 2019")
	,C06(new Mod2002022Key[]{Mod2002022Key.BN2319 	,Mod2002022Key.BN2320 	,Mod2002022Key.BN2321 	,Mod2002022Key.BN2322	,Mod2002022Key.BN2323 	},"D.I. interna 2020")
	,C07(new Mod2002022Key[]{Mod2002022Key.BN199 	,Mod2002022Key.BN203 	,Mod2002022Key.BN204 	,Mod2002022Key.BN205	,Mod2002022Key.BN206 	},"D.I. interna 2021(*)")
	,C08(new Mod2002022Key[]{Mod2002022Key.BN1342	,null				  	,Mod2002022Key.BN1343	,Mod2002022Key.BN1344 	,Mod2002022Key.BN1345	},"Total")
	,C09(new Mod2002022Key[]{null				 	,Mod2002022Key.BN103B	,null					,null 			  		,null 			   		},"Tipo de gravamen 2021")
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022BN1344Key(Mod2002022Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002022Key[] getKeys() {
		return keys;
	}


}
