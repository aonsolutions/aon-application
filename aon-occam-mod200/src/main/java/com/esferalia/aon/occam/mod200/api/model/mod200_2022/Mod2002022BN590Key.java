package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones inversión en Canarias con límites incrementados
public enum Mod2002022BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002022Key[]{Mod2002022Key.BN854 ,Mod2002022Key.BN855 ,Mod2002022Key.BN1356},"2010: Activos fijos (Ley 20/1991)")
	 ,C02(new Mod2002022Key[]{Mod2002022Key.BN857 ,Mod2002022Key.BN858 ,Mod2002022Key.BN859 },"2011: Activos fijos (Ley 20/1991)")
	 ,C03(new Mod2002022Key[]{Mod2002022Key.BN860 ,Mod2002022Key.BN861 ,Mod2002022Key.BN862 },"2012: Activos fijos (Ley 20/1991)")
	 ,C04(new Mod2002022Key[]{Mod2002022Key.BN863 ,Mod2002022Key.BN864 ,Mod2002022Key.BN865 },"2013: Activos fijos (Ley 20/1991)")
	 ,C05(new Mod2002022Key[]{Mod2002022Key.BN883 ,Mod2002022Key.BN884 ,Mod2002022Key.BN885 },"2014: Activos fijos (Ley 20/1991)")
	 ,C06(new Mod2002022Key[]{Mod2002022Key.BN785 ,Mod2002022Key.BN789 ,Mod2002022Key.BN790 },"2015: Activos fijos (Ley 20/1991)")
	 ,C07(new Mod2002022Key[]{Mod2002022Key.BN1357,Mod2002022Key.BN1358,Mod2002022Key.BN1359},"2016: Activos fijos (Ley 20/1991)")
	 ,C08(new Mod2002022Key[]{Mod2002022Key.BN1778,Mod2002022Key.BN1779,Mod2002022Key.BN1780},"2017: Activos fijos (Ley 20/1991)")
	 ,C09(new Mod2002022Key[]{Mod2002022Key.BN852 ,Mod2002022Key.BN853 ,Mod2002022Key.BN856 },"2018: Activos fijos (Ley 20/1991)")
	 ,C10(new Mod2002022Key[]{Mod2002022Key.BN2116,Mod2002022Key.BN2117,Mod2002022Key.BN2118},"2019: Activos fijos (Ley 20/1991)")
	 ,C11(new Mod2002022Key[]{Mod2002022Key.BN2209,Mod2002022Key.BN2210,Mod2002022Key.BN2211},"2020: Activos fijos (Ley 20/1991)")
	 ,C12(new Mod2002022Key[]{Mod2002022Key.BN2332,Mod2002022Key.BN2333,Mod2002022Key.BN2334},"2021: Activos fijos (Ley 20/1991)")
	 ,C13(new Mod2002022Key[]{Mod2002022Key.BN237 ,Mod2002022Key.BN238 ,Mod2002022Key.BN239 },"2022(*): Activos fijos (Ley 20/1991)")
	 ,C14(new Mod2002022Key[]{Mod2002022Key.BN711 ,Mod2002022Key.BN712 ,Mod2002022Key.BN2077},"2022: Activos fijos (Ley 20/1991)")	 
	 ,C15(new Mod2002022Key[]{Mod2002022Key.BN2335,Mod2002022Key.BN2336,Mod2002022Key.BN2337},"2018: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C16(new Mod2002022Key[]{Mod2002022Key.BN2338,Mod2002022Key.BN2339,Mod2002022Key.BN2340},"2019: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C17(new Mod2002022Key[]{Mod2002022Key.BN2341,Mod2002022Key.BN2342,Mod2002022Key.BN2343},"2020: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C18(new Mod2002022Key[]{Mod2002022Key.BN2344,Mod2002022Key.BN2345,Mod2002022Key.BN2346},"2021: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C19(new Mod2002022Key[]{Mod2002022Key.BN244 ,Mod2002022Key.BN245 ,Mod2002022Key.BN2497},"2022(*): Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C20(new Mod2002022Key[]{Mod2002022Key.BN2078,Mod2002022Key.BN1913,Mod2002022Key.BN766 },"2022: Activos fijos en La Palma, La Gomera y El Hierro")
	 ,C21(new Mod2002022Key[]{Mod2002022Key.BN880 ,Mod2002022Key.BN881 ,null                },"2004: Inversiones en Canarias (Ley 20/1991)")
	 ,C22(new Mod2002022Key[]{Mod2002022Key.BN866 ,Mod2002022Key.BN867 ,Mod2002022Key.BN870 },"2005: Inversiones en Canarias (Ley 20/1991)")
	 ,C23(new Mod2002022Key[]{Mod2002022Key.BN939 ,Mod2002022Key.BN940 ,Mod2002022Key.BN941 },"2006: Inversiones en Canarias (Ley 20/1991)")
	 ,C24(new Mod2002022Key[]{Mod2002022Key.BN191 ,Mod2002022Key.BN192 ,Mod2002022Key.BN193 },"2007: Inversiones en Canarias (Ley 20/1991)")
	 ,C25(new Mod2002022Key[]{Mod2002022Key.BN613 ,Mod2002022Key.BN614 ,Mod2002022Key.BN701 },"2008: Inversiones en Canarias (Ley 20/1991)")
	 ,C26(new Mod2002022Key[]{Mod2002022Key.BN200 ,Mod2002022Key.BN257 ,Mod2002022Key.BN011 },"2009: Inversiones en Canarias (Ley 20/1991)")
	 ,C27(new Mod2002022Key[]{Mod2002022Key.BN037 ,Mod2002022Key.BN038 ,Mod2002022Key.BN039 },"2010: Inversiones en Canarias (Ley 20/1991)")
	 ,C28(new Mod2002022Key[]{Mod2002022Key.BN044 ,Mod2002022Key.BN045 ,Mod2002022Key.BN046 },"2011: Inversiones en Canarias (Ley 20/1991)")
	 ,C29(new Mod2002022Key[]{Mod2002022Key.BN528 ,Mod2002022Key.BN529 ,Mod2002022Key.BN530 },"2012: Inversiones en Canarias (Ley 20/1991)")
	 ,C30(new Mod2002022Key[]{Mod2002022Key.BN144 ,Mod2002022Key.BN145 ,Mod2002022Key.BN146 },"2013: Inversiones en Canarias (Ley 20/1991)")
	 ,C31(new Mod2002022Key[]{Mod2002022Key.BN147 ,Mod2002022Key.BN148 ,Mod2002022Key.BN149 },"2014: Inversiones en Canarias (Ley 20/1991)")
	 ,C32(new Mod2002022Key[]{Mod2002022Key.BN240 ,Mod2002022Key.BN241 ,Mod2002022Key.BN242 },"2015: Inversiones en Canarias (Ley 20/1991)")
	 ,C33(new Mod2002022Key[]{Mod2002022Key.BN1058,Mod2002022Key.BN1059,Mod2002022Key.BN1060},"2016: Inversiones en Canarias (Ley 20/1991)")
     ,C34(new Mod2002022Key[]{Mod2002022Key.BN791 ,Mod2002022Key.BN802 ,Mod2002022Key.BN806 },"2017: Inversiones en Canarias (Ley 20/1991)")
     ,C35(new Mod2002022Key[]{Mod2002022Key.BN1781,Mod2002022Key.BN1782,Mod2002022Key.BN1783},"2018: Inversiones en Canarias (Ley 20/1991)")
     ,C36(new Mod2002022Key[]{Mod2002022Key.BN2122,Mod2002022Key.BN2123,Mod2002022Key.BN2124},"2019: Inversiones en Canarias (Ley 20/1991)")
     ,C37(new Mod2002022Key[]{Mod2002022Key.BN2212,Mod2002022Key.BN2213,Mod2002022Key.BN2214},"2020: Inversiones en Canarias (Ley 20/1991)")
     ,C38(new Mod2002022Key[]{Mod2002022Key.BN2347,Mod2002022Key.BN2348,Mod2002022Key.BN2349},"2021: Inversiones en Canarias (Ley 20/1991)")
     ,C39(new Mod2002022Key[]{Mod2002022Key.BN217 ,Mod2002022Key.BN218 ,Mod2002022Key.BN219 },"2022(*): Inversiones en Canarias (Ley 20/1991)")
     ,C40(new Mod2002022Key[]{Mod2002022Key.BN767 ,Mod2002022Key.BN768 ,Mod2002022Key.BN769 },"2022: Inversiones en Canarias (Ley 20/1991)")
     ,C41(new Mod2002022Key[]{Mod2002022Key.BN2119,Mod2002022Key.BN2120,Mod2002022Key.BN2121},"2018: Inversiones en La Palma, La Gomera y El Hierro")
     ,C42(new Mod2002022Key[]{Mod2002022Key.BN2125,Mod2002022Key.BN2126,Mod2002022Key.BN2127},"2019: Inversiones en La Palma, La Gomera y El Hierro")
     ,C43(new Mod2002022Key[]{Mod2002022Key.BN2215,Mod2002022Key.BN2216,Mod2002022Key.BN2217},"2020: Inversiones en La Palma, La Gomera y El Hierro")
     ,C44(new Mod2002022Key[]{Mod2002022Key.BN2350,Mod2002022Key.BN2351,Mod2002022Key.BN2352},"2021: Inversiones en La Palma, La Gomera y El Hierro")
     ,C45(new Mod2002022Key[]{Mod2002022Key.BN220 ,Mod2002022Key.BN221 ,Mod2002022Key.BN222 },"2022(*): Inversiones en La Palma, La Gomera y El Hierro")
     ,C46(new Mod2002022Key[]{Mod2002022Key.BN770 ,Mod2002022Key.BN771 ,Mod2002022Key.BN774 },"2022: Inversiones en La Palma, La Gomera y El Hierro")
	 ,C47(new Mod2002022Key[]{Mod2002022Key.BN886 ,Mod2002022Key.BN590 ,Mod2002022Key.BN887 },"Total")

	 ,C48(new Mod2002022Key[]{Mod2002022Key.BN2287,null                ,null                },"2022: Deducci\u00F3n por investigaci\u00F3n y desarrollo en Canarias generada en el per\u00EDodo impositivo")	 		
	 ,C49(new Mod2002022Key[]{Mod2002022Key.BN2288,null                ,null                },"2022: Deducci\u00F3n por innovaci\u00F3n tecnol\u00F3gica en Canarias generada en el per\u00EDodo impositivo")
	 ,C50(new Mod2002022Key[]{Mod2002022Key.BN2495,null                ,null                },"2022: Productor: Deducci\u00F3n por producciones cinematogr\u00E1ficas espa\u00F1olas en Canarias generada en el per\u00EDodo impositivo")
	 ,C51(new Mod2002022Key[]{Mod2002022Key.BN2079,null                ,null                },"2022: Financiador: Deducci\u00F3n por producciones cinematogr\u00E1ficas espa\u00F1olas en Canarias generada en el per\u00EDodo impositivo")
	 ,C52(new Mod2002022Key[]{Mod2002022Key.BN2496,null                ,null                },"2022: Productor: Deducci\u00F3n por espect\u00E1culos en vivo de artes esc\u00E9nicas y musicales en Canarias generada en el per\u00EDodo impositivo")
	 ,C53(new Mod2002022Key[]{Mod2002022Key.BN2080,null                ,null                },"2022: Financiador: Deducci\u00F3n por espect\u00E1culos en vivo de artes esc\u00E9nicas y musicales en Canarias generada en el per\u00EDodo impositivo")
	 
	;
	
    private String description;
    private Mod2002022Key[] keys;
    
	private Mod2002022BN590Key(Mod2002022Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002022Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}

}

