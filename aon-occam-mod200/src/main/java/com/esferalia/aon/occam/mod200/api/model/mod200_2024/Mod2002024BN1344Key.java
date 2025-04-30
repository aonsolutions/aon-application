package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición interna (DT 23ª.1 LIS) de Ejercicios anteriores
public enum Mod2002024BN1344Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002024Key[]{Mod2002024Key.BN101 	,Mod2002024Key.BN102 	,Mod2002024Key.BN119 	,Mod2002024Key.BN120	,Mod2002024Key.BN121 	},"D.I. interna 2015")
	,C02(new Mod2002024Key[]{Mod2002024Key.BN122 	,Mod2002024Key.BN123	,Mod2002024Key.BN124 	,Mod2002024Key.BN125	,Mod2002024Key.BN126 	},"D.I. interna 2016")
	,C03(new Mod2002024Key[]{Mod2002024Key.BN1595 	,Mod2002024Key.BN1596 	,Mod2002024Key.BN1597 	,Mod2002024Key.BN1598	,Mod2002024Key.BN1599 	},"D.I. interna 2017")
	,C04(new Mod2002024Key[]{Mod2002024Key.BN1828 	,Mod2002024Key.BN1829 	,Mod2002024Key.BN1830 	,Mod2002024Key.BN1831	,Mod2002024Key.BN1832 	},"D.I. interna 2018")
	,C05(new Mod2002024Key[]{Mod2002024Key.BN2196 	,Mod2002024Key.BN2197 	,Mod2002024Key.BN2198 	,Mod2002024Key.BN2199	,Mod2002024Key.BN2200 	},"D.I. interna 2019")
	,C06(new Mod2002024Key[]{Mod2002024Key.BN2319 	,Mod2002024Key.BN2320 	,Mod2002024Key.BN2321 	,Mod2002024Key.BN2322	,Mod2002024Key.BN2323 	},"D.I. interna 2020")
	,C07(new Mod2002024Key[]{Mod2002024Key.BN199 	,Mod2002024Key.BN203 	,Mod2002024Key.BN204 	,Mod2002024Key.BN205	,Mod2002024Key.BN206 	},"D.I. interna 2021")
	,C08(new Mod2002024Key[]{Mod2002024Key.BN394 	,Mod2002024Key.BN436 	,Mod2002024Key.BN437 	,Mod2002024Key.BN438	,Mod2002024Key.BN2076	},"D.I. interna 2022")
	,C09(new Mod2002024Key[]{Mod2002024Key.BN1270	,Mod2002024Key.BN1271	,Mod2002024Key.BN1299	,Mod2002024Key.BN1318   ,Mod2002024Key.BN1360	},"D.I. interna 2023")
	,C10(new Mod2002024Key[]{Mod2002024Key.BN467 	,Mod2002024Key.BN586 	,Mod2002024Key.BN259 	,Mod2002024Key.BN260    ,Mod2002024Key.BN261 	},"D.I. interna 2024(*)")
	,C11(new Mod2002024Key[]{Mod2002024Key.BN1342	,null				  	,Mod2002024Key.BN1343	,Mod2002024Key.BN1344 	,Mod2002024Key.BN1345	},"Total")
	,C12(new Mod2002024Key[]{null				 	,Mod2002024Key.BN103B	,null					,null 			  		,null 			   		},"Tipo de gravamen 2024")
	;
	 
    private String description;
    private Mod2002024Key[] keys;

	private Mod2002024BN1344Key(Mod2002024Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002024Key[] getKeys() {
		return keys;
	}


}
