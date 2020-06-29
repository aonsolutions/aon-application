package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import java.io.Serializable;

// Deducciones inversión en Canarias.
public enum Mod2002019BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002019Key[]{Mod2002019Key.BN854 ,Mod2002019Key.BN855 ,Mod2002019Key.BN1356},"2010: Activos fijos (Ley 20/1991)")
	 ,C02(new Mod2002019Key[]{Mod2002019Key.BN857 ,Mod2002019Key.BN858 ,Mod2002019Key.BN859 },"2011: Activos fijos (Ley 20/1991)")
	 ,C03(new Mod2002019Key[]{Mod2002019Key.BN860 ,Mod2002019Key.BN861 ,Mod2002019Key.BN862 },"2012: Activos fijos (Ley 20/1991)")
	 ,C04(new Mod2002019Key[]{Mod2002019Key.BN863 ,Mod2002019Key.BN864 ,Mod2002019Key.BN865 },"2013: Activos fijos (Ley 20/1991)")
	 ,C05(new Mod2002019Key[]{Mod2002019Key.BN883 ,Mod2002019Key.BN884 ,Mod2002019Key.BN885 },"2014: Activos fijos (Ley 20/1991)")
	 ,C06(new Mod2002019Key[]{Mod2002019Key.BN785 ,Mod2002019Key.BN789 ,Mod2002019Key.BN790 },"2015: Activos fijos (Ley 20/1991)")
	 ,C07(new Mod2002019Key[]{Mod2002019Key.BN1357,Mod2002019Key.BN1358,Mod2002019Key.BN1359},"2016: Activos fijos (Ley 20/1991)")
	 ,C08(new Mod2002019Key[]{Mod2002019Key.BN1778,Mod2002019Key.BN1779,Mod2002019Key.BN1780},"2017: Activos fijos (Ley 20/1991)")
	 ,C09(new Mod2002019Key[]{Mod2002019Key.BN852 ,Mod2002019Key.BN853 ,Mod2002019Key.BN856 },"2018: Activos fijos (Ley 20/1991)")
	 ,C10(new Mod2002019Key[]{Mod2002019Key.BN2116,Mod2002019Key.BN2117,Mod2002019Key.BN2118},"2019(*): Activos fijos (Ley 20/1991)")
	 ,C11(new Mod2002019Key[]{Mod2002019Key.BN2209,Mod2002019Key.BN2210,Mod2002019Key.BN2211},"2019: Activos fijos (Ley 20/1991)")	 
	 ,C12(new Mod2002019Key[]{Mod2002019Key.BN871 ,Mod2002019Key.BN872 ,null                },"2001: Inversiones en Canarias (Ley 20/1991)")
	 ,C13(new Mod2002019Key[]{Mod2002019Key.BN874 ,Mod2002019Key.BN875 ,Mod2002019Key.BN876 },"2002: Inversiones en Canarias (Ley 20/1991)")
	 ,C14(new Mod2002019Key[]{Mod2002019Key.BN877 ,Mod2002019Key.BN878 ,Mod2002019Key.BN879 },"2003: Inversiones en Canarias (Ley 20/1991)")
	 ,C15(new Mod2002019Key[]{Mod2002019Key.BN880 ,Mod2002019Key.BN881 ,Mod2002019Key.BN882 },"2004: Inversiones en Canarias (Ley 20/1991)")
	 ,C16(new Mod2002019Key[]{Mod2002019Key.BN866 ,Mod2002019Key.BN867 ,Mod2002019Key.BN870 },"2005: Inversiones en Canarias (Ley 20/1991)")
	 ,C17(new Mod2002019Key[]{Mod2002019Key.BN939 ,Mod2002019Key.BN940 ,Mod2002019Key.BN941 },"2006: Inversiones en Canarias (Ley 20/1991)")
	 ,C18(new Mod2002019Key[]{Mod2002019Key.BN191 ,Mod2002019Key.BN192 ,Mod2002019Key.BN193 },"2007: Inversiones en Canarias (Ley 20/1991)")
	 ,C19(new Mod2002019Key[]{Mod2002019Key.BN613 ,Mod2002019Key.BN614 ,Mod2002019Key.BN701 },"2008: Inversiones en Canarias (Ley 20/1991)")
	 ,C20(new Mod2002019Key[]{Mod2002019Key.BN200 ,Mod2002019Key.BN257 ,Mod2002019Key.BN011 },"2009: Inversiones en Canarias (Ley 20/1991)")
	 ,C21(new Mod2002019Key[]{Mod2002019Key.BN037 ,Mod2002019Key.BN038 ,Mod2002019Key.BN039 },"2010: Inversiones en Canarias (Ley 20/1991)")
	 ,C22(new Mod2002019Key[]{Mod2002019Key.BN044 ,Mod2002019Key.BN045 ,Mod2002019Key.BN046 },"2011: Inversiones en Canarias (Ley 20/1991)")
	 ,C23(new Mod2002019Key[]{Mod2002019Key.BN528 ,Mod2002019Key.BN529 ,Mod2002019Key.BN530 },"2012: Inversiones en Canarias (Ley 20/1991)")
	 ,C24(new Mod2002019Key[]{Mod2002019Key.BN144 ,Mod2002019Key.BN145 ,Mod2002019Key.BN146 },"2013: Inversiones en Canarias (Ley 20/1991)")
	 ,C25(new Mod2002019Key[]{Mod2002019Key.BN147 ,Mod2002019Key.BN148 ,Mod2002019Key.BN149 },"2014: Inversiones en Canarias (Ley 20/1991)")
	 ,C26(new Mod2002019Key[]{Mod2002019Key.BN240 ,Mod2002019Key.BN241 ,Mod2002019Key.BN242 },"2015: Inversiones en Canarias (Ley 20/1991)")
	 ,C27(new Mod2002019Key[]{Mod2002019Key.BN1058,Mod2002019Key.BN1059,Mod2002019Key.BN1060},"2016: Inversiones en Canarias (Ley 20/1991)")
     ,C28(new Mod2002019Key[]{Mod2002019Key.BN791 ,Mod2002019Key.BN802 ,Mod2002019Key.BN806 },"2017: Inversiones en Canarias (Ley 20/1991)")
     ,C29(new Mod2002019Key[]{Mod2002019Key.BN1781,Mod2002019Key.BN1782,Mod2002019Key.BN1783},"2018: Inversiones en Canarias (Ley 20/1991)")
     ,C30(new Mod2002019Key[]{Mod2002019Key.BN2122,Mod2002019Key.BN2123,Mod2002019Key.BN2124},"2019(*): Inversiones en Canarias (Ley 20/1991)")
     ,C31(new Mod2002019Key[]{Mod2002019Key.BN2212,Mod2002019Key.BN2213,Mod2002019Key.BN2214},"2019: Inversiones en Canarias (Ley 20/1991)")
     ,C32(new Mod2002019Key[]{Mod2002019Key.BN2119,Mod2002019Key.BN2120,Mod2002019Key.BN2121},"2018: Inversiones en La Palma, La Gomera y El Hierro")
     ,C33(new Mod2002019Key[]{Mod2002019Key.BN2125,Mod2002019Key.BN2126,Mod2002019Key.BN2127},"2019(*): Inversiones en La Palma, La Gomera y El Hierro")
     ,C34(new Mod2002019Key[]{Mod2002019Key.BN2215,Mod2002019Key.BN2216,Mod2002019Key.BN2217},"2019: Inversiones en La Palma, La Gomera y El Hierro")     
	 ,C35(new Mod2002019Key[]{Mod2002019Key.BN886 ,Mod2002019Key.BN590 ,Mod2002019Key.BN887 },"Total")
	 ,C36(new Mod2002019Key[]{Mod2002019Key.BN2287,null                ,null                },"2019: Deducci\u00F3n por investigaci\u00F3n y desarrollo en Canarias generada en el periodo impositivo")	 		
	 ,C37(new Mod2002019Key[]{Mod2002019Key.BN2288,null                ,null                },"2019: Deducci\u00F3n por innovaci\u00F3n tecnol\u00F3gica en Canarias generada en el periodo impositivo")
	;
	 
    private String description;
    
    private Mod2002019Key[] keys;
    
	private Mod2002019BN590Key(Mod2002019Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002019Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

