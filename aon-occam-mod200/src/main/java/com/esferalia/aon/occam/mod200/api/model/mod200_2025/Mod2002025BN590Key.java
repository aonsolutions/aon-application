package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones inversión en Canarias con límites incrementados
public enum Mod2002025BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002025Key[]{Mod2002025Key.BN854 ,Mod2002025Key.BN855 ,Mod2002025Key.BN1356},"2010: Activos fijos (Ley 20/1991)")
	 ,C02(new Mod2002025Key[]{Mod2002025Key.BN857 ,Mod2002025Key.BN858 ,Mod2002025Key.BN859 },"2011: Activos fijos (Ley 20/1991)")
	 ,C03(new Mod2002025Key[]{Mod2002025Key.BN860 ,Mod2002025Key.BN861 ,Mod2002025Key.BN862 },"2012: Activos fijos (Ley 20/1991)")
	 ,C04(new Mod2002025Key[]{Mod2002025Key.BN863 ,Mod2002025Key.BN864 ,Mod2002025Key.BN865 },"2013: Activos fijos (Ley 20/1991)")
	 ,C05(new Mod2002025Key[]{Mod2002025Key.BN883 ,Mod2002025Key.BN884 ,Mod2002025Key.BN885 },"2014: Activos fijos (Ley 20/1991)")
	 ,C06(new Mod2002025Key[]{Mod2002025Key.BN785 ,Mod2002025Key.BN789 ,Mod2002025Key.BN790 },"2015: Activos fijos (Ley 20/1991)")
	 ,C07(new Mod2002025Key[]{Mod2002025Key.BN1357,Mod2002025Key.BN1358,Mod2002025Key.BN1359},"2016: Activos fijos (Ley 20/1991)")
	 ,C08(new Mod2002025Key[]{Mod2002025Key.BN1778,Mod2002025Key.BN1779,Mod2002025Key.BN1780},"2017: Activos fijos (Ley 20/1991)")
	 ,C09(new Mod2002025Key[]{Mod2002025Key.BN852 ,Mod2002025Key.BN853 ,Mod2002025Key.BN856 },"2018: Activos fijos (Ley 20/1991)")
	 ,C10(new Mod2002025Key[]{Mod2002025Key.BN2116,Mod2002025Key.BN2117,Mod2002025Key.BN2118},"2019: Activos fijos (Ley 20/1991)")
	 ,C11(new Mod2002025Key[]{Mod2002025Key.BN2209,Mod2002025Key.BN2210,Mod2002025Key.BN2211},"2020: Activos fijos (Ley 20/1991)")
	 ,C12(new Mod2002025Key[]{Mod2002025Key.BN2332,Mod2002025Key.BN2333,Mod2002025Key.BN2334},"2021: Activos fijos (Ley 20/1991)")
	 ,C13(new Mod2002025Key[]{Mod2002025Key.BN237 ,Mod2002025Key.BN238 ,Mod2002025Key.BN239 },"2022: Activos fijos (Ley 20/1991)")
	 ,C14(new Mod2002025Key[]{Mod2002025Key.BN711 ,Mod2002025Key.BN712 ,Mod2002025Key.BN2077},"2023: Activos fijos (Ley 20/1991)")
	 ,C15(new Mod2002025Key[]{Mod2002025Key.BN1614,Mod2002025Key.BN1615,Mod2002025Key.BN1616},"2025(*): Activos fijos (Ley 20/1991)")
	 ,C16(new Mod2002025Key[]{Mod2002025Key.BN262 ,Mod2002025Key.BN263 ,Mod2002025Key.BN264 },"2025: Activos fijos (Ley 20/1991)")
	 ,C17(new Mod2002025Key[]{Mod2002025Key.BN2335,Mod2002025Key.BN2336,Mod2002025Key.BN2337},"2018: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C18(new Mod2002025Key[]{Mod2002025Key.BN2338,Mod2002025Key.BN2339,Mod2002025Key.BN2340},"2019: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C19(new Mod2002025Key[]{Mod2002025Key.BN2341,Mod2002025Key.BN2342,Mod2002025Key.BN2343},"2020: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C20(new Mod2002025Key[]{Mod2002025Key.BN2344,Mod2002025Key.BN2345,Mod2002025Key.BN2346},"2021: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C21(new Mod2002025Key[]{Mod2002025Key.BN244 ,Mod2002025Key.BN245 ,Mod2002025Key.BN2497},"2022: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C22(new Mod2002025Key[]{Mod2002025Key.BN2078,Mod2002025Key.BN1913,Mod2002025Key.BN766 },"2023: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C23(new Mod2002025Key[]{Mod2002025Key.BN1763,Mod2002025Key.BN1800,Mod2002025Key.BN1801},"2025(*): Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C24(new Mod2002025Key[]{Mod2002025Key.BN268 ,Mod2002025Key.BN269 ,Mod2002025Key.BN270 },"2025: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C25(new Mod2002025Key[]{Mod2002025Key.BN939 ,Mod2002025Key.BN940 ,null                },"2006: Inversiones en Canarias (Ley 20/1991)")
	 ,C26(new Mod2002025Key[]{Mod2002025Key.BN191 ,Mod2002025Key.BN192 ,Mod2002025Key.BN193 },"2007: Inversiones en Canarias (Ley 20/1991)")
	 ,C27(new Mod2002025Key[]{Mod2002025Key.BN613 ,Mod2002025Key.BN614 ,Mod2002025Key.BN701 },"2008: Inversiones en Canarias (Ley 20/1991)")
	 ,C28(new Mod2002025Key[]{Mod2002025Key.BN200 ,Mod2002025Key.BN257 ,Mod2002025Key.BN011 },"2009: Inversiones en Canarias (Ley 20/1991)")
	 ,C29(new Mod2002025Key[]{Mod2002025Key.BN037 ,Mod2002025Key.BN038 ,Mod2002025Key.BN039 },"2010: Inversiones en Canarias (Ley 20/1991)")
	 ,C30(new Mod2002025Key[]{Mod2002025Key.BN044 ,Mod2002025Key.BN045 ,Mod2002025Key.BN046 },"2011: Inversiones en Canarias (Ley 20/1991)")
	 ,C31(new Mod2002025Key[]{Mod2002025Key.BN528 ,Mod2002025Key.BN529 ,Mod2002025Key.BN530 },"2012: Inversiones en Canarias (Ley 20/1991)")
	 ,C32(new Mod2002025Key[]{Mod2002025Key.BN144 ,Mod2002025Key.BN145 ,Mod2002025Key.BN146 },"2013: Inversiones en Canarias (Ley 20/1991)")
	 ,C33(new Mod2002025Key[]{Mod2002025Key.BN147 ,Mod2002025Key.BN148 ,Mod2002025Key.BN149 },"2014: Inversiones en Canarias (Ley 20/1991)")
	 ,C34(new Mod2002025Key[]{Mod2002025Key.BN240 ,Mod2002025Key.BN241 ,Mod2002025Key.BN242 },"2015: Inversiones en Canarias (Ley 20/1991)")
	 ,C35(new Mod2002025Key[]{Mod2002025Key.BN1058,Mod2002025Key.BN1059,Mod2002025Key.BN1060},"2016: Inversiones en Canarias (Ley 20/1991)")
     ,C36(new Mod2002025Key[]{Mod2002025Key.BN791 ,Mod2002025Key.BN802 ,Mod2002025Key.BN806 },"2017: Inversiones en Canarias (Ley 20/1991)")
     ,C37(new Mod2002025Key[]{Mod2002025Key.BN1781,Mod2002025Key.BN1782,Mod2002025Key.BN1783},"2018: Inversiones en Canarias (Ley 20/1991)")
     ,C38(new Mod2002025Key[]{Mod2002025Key.BN2122,Mod2002025Key.BN2123,Mod2002025Key.BN2124},"2019: Inversiones en Canarias (Ley 20/1991)")
     ,C39(new Mod2002025Key[]{Mod2002025Key.BN2212,Mod2002025Key.BN2213,Mod2002025Key.BN2214},"2020: Inversiones en Canarias (Ley 20/1991)")
     ,C40(new Mod2002025Key[]{Mod2002025Key.BN2347,Mod2002025Key.BN2348,Mod2002025Key.BN2349},"2021: Inversiones en Canarias (Ley 20/1991)")
     ,C41(new Mod2002025Key[]{Mod2002025Key.BN217 ,Mod2002025Key.BN218 ,Mod2002025Key.BN219 },"2022: Inversiones en Canarias (Ley 20/1991)")
     ,C42(new Mod2002025Key[]{Mod2002025Key.BN767 ,Mod2002025Key.BN768 ,Mod2002025Key.BN769 },"2023: Inversiones en Canarias (Ley 20/1991)")
     ,C43(new Mod2002025Key[]{Mod2002025Key.BN1802,Mod2002025Key.BN1803,Mod2002025Key.BN1804},"2025(*): Inversiones en Canarias (Ley 20/1991)")
     ,C44(new Mod2002025Key[]{Mod2002025Key.BN271 ,Mod2002025Key.BN273 ,Mod2002025Key.BN274 },"2025: Inversiones en Canarias (Ley 20/1991)")
     ,C45(new Mod2002025Key[]{Mod2002025Key.BN2119,Mod2002025Key.BN2120,Mod2002025Key.BN2121},"2018: Inversiones en La Palma, La Gomera y El Hierro")
     ,C46(new Mod2002025Key[]{Mod2002025Key.BN2125,Mod2002025Key.BN2126,Mod2002025Key.BN2127},"2019: Inversiones en La Palma, La Gomera y El Hierro")
     ,C47(new Mod2002025Key[]{Mod2002025Key.BN2215,Mod2002025Key.BN2216,Mod2002025Key.BN2217},"2020: Inversiones en La Palma, La Gomera y El Hierro")
     ,C48(new Mod2002025Key[]{Mod2002025Key.BN2350,Mod2002025Key.BN2351,Mod2002025Key.BN2352},"2021: Inversiones en La Palma, La Gomera y El Hierro")
     ,C49(new Mod2002025Key[]{Mod2002025Key.BN220 ,Mod2002025Key.BN221 ,Mod2002025Key.BN222 },"2022: Inversiones en La Palma, La Gomera y El Hierro")
     ,C50(new Mod2002025Key[]{Mod2002025Key.BN770 ,Mod2002025Key.BN771 ,Mod2002025Key.BN774 },"2023: Inversiones en La Palma, La Gomera y El Hierro")
     ,C51(new Mod2002025Key[]{Mod2002025Key.BN1805,Mod2002025Key.BN1806,Mod2002025Key.BN1847},"2025(*): Inversiones en La Palma, La Gomera y El Hierro")
     ,C52(new Mod2002025Key[]{Mod2002025Key.BN294 ,Mod2002025Key.BN295 ,Mod2002025Key.BN296 },"2025: Inversiones en La Palma, La Gomera y El Hierro")
	 ,C53(new Mod2002025Key[]{Mod2002025Key.BN886 ,Mod2002025Key.BN590 ,Mod2002025Key.BN887 },"Total")
        
	 ,C54(new Mod2002025Key[]{Mod2002025Key.BN2287,null                ,null                },"2025: Deducci\u00F3n por investigaci\u00F3n y desarrollo en Canarias generada en el per\u00EDodo impositivo")	 		
	 ,C55(new Mod2002025Key[]{Mod2002025Key.BN2288,null                ,null                },"2025: Deducci\u00F3n por innovaci\u00F3n tecnol\u00F3gica en Canarias generada en el per\u00EDodo impositivo")
	 ,C56(new Mod2002025Key[]{Mod2002025Key.BN2495,null                ,null                },"2025: Productor: Deducci\u00F3n por producciones cinematogr\u00E1ficas espa\u00F1olas en Canarias generada en el per\u00EDodo impositivo")
	 ,C57(new Mod2002025Key[]{Mod2002025Key.BN2079,null                ,null                },"2025: Financiador: Deducci\u00F3n por producciones cinematogr\u00E1ficas espa\u00F1olas en Canarias generada en el per\u00EDodo impositivo")
	 ,C58(new Mod2002025Key[]{Mod2002025Key.BN2496,null                ,null                },"2025: Productor: Deducci\u00F3n por espect\u00E1culos en vivo de artes esc\u00E9nicas y musicales en Canarias generada en el per\u00EDodo impositivo")
	 ,C59(new Mod2002025Key[]{Mod2002025Key.BN2080,null                ,null                },"2025: Financiador: Deducci\u00F3n por espect\u00E1culos en vivo de artes esc\u00E9nicas y musicales en Canarias generada en el per\u00EDodo impositivo")
	 
	;
	
    private String description;
    private Mod2002025Key[] keys;
    
	private Mod2002025BN590Key(Mod2002025Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002025Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}

}

