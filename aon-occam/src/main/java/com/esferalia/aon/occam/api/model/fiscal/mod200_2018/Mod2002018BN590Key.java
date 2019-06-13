package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Deducciones inversión en Canarias.
public enum Mod2002018BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002018Key[]{Mod2002018Key.BN854 ,Mod2002018Key.BN855 ,Mod2002018Key.BN1356},"Activos fijos (Ley 20/1991) 2010")
	 ,C02(new Mod2002018Key[]{Mod2002018Key.BN857 ,Mod2002018Key.BN858 ,Mod2002018Key.BN859 },"Activos fijos (Ley 20/1991) 2011")
	 ,C03(new Mod2002018Key[]{Mod2002018Key.BN860 ,Mod2002018Key.BN861 ,Mod2002018Key.BN862 },"Activos fijos (Ley 20/1991) 2012")
	 ,C04(new Mod2002018Key[]{Mod2002018Key.BN863 ,Mod2002018Key.BN864 ,Mod2002018Key.BN865 },"Activos fijos (Ley 20/1991) 2013")
	 ,C05(new Mod2002018Key[]{Mod2002018Key.BN883 ,Mod2002018Key.BN884 ,Mod2002018Key.BN885 },"Activos fijos (Ley 20/1991) 2014")
	 ,C06(new Mod2002018Key[]{Mod2002018Key.BN785 ,Mod2002018Key.BN789 ,Mod2002018Key.BN790 },"Activos fijos (Ley 20/1991) 2015")
	 ,C07(new Mod2002018Key[]{Mod2002018Key.BN1357,Mod2002018Key.BN1358,Mod2002018Key.BN1359},"Activos fijos (Ley 20/1991) 2016")
	 ,C08(new Mod2002018Key[]{Mod2002018Key.BN1778,Mod2002018Key.BN1779,Mod2002018Key.BN1780},"Activos fijos (Ley 20/1991) 2017")
	 ,C09(new Mod2002018Key[]{Mod2002018Key.BN852 ,Mod2002018Key.BN853 ,Mod2002018Key.BN856 },"Activos fijos (Ley 20/1991) 2018(*)")
	 ,C10(new Mod2002018Key[]{Mod2002018Key.BN2116,Mod2002018Key.BN2117,Mod2002018Key.BN2118},"Activos fijos (Ley 20/1991) 2018")
	 ,C11(new Mod2002018Key[]{Mod2002018Key.BN868 ,Mod2002018Key.BN869 ,null                },"Inversiones en Canarias (Ley 20/1991) 2000")
	 ,C12(new Mod2002018Key[]{Mod2002018Key.BN871 ,Mod2002018Key.BN872 ,Mod2002018Key.BN873 },"Inversiones en Canarias (Ley 20/1991) 2001")
	 ,C13(new Mod2002018Key[]{Mod2002018Key.BN874 ,Mod2002018Key.BN875 ,Mod2002018Key.BN876 },"Inversiones en Canarias (Ley 20/1991) 2002")
	 ,C14(new Mod2002018Key[]{Mod2002018Key.BN877 ,Mod2002018Key.BN878 ,Mod2002018Key.BN879 },"Inversiones en Canarias (Ley 20/1991) 2003")
	 ,C15(new Mod2002018Key[]{Mod2002018Key.BN880 ,Mod2002018Key.BN881 ,Mod2002018Key.BN882 },"Inversiones en Canarias (Ley 20/1991) 2004")
	 ,C16(new Mod2002018Key[]{Mod2002018Key.BN866 ,Mod2002018Key.BN867 ,Mod2002018Key.BN870 },"Inversiones en Canarias (Ley 20/1991) 2005")
	 ,C17(new Mod2002018Key[]{Mod2002018Key.BN939 ,Mod2002018Key.BN940 ,Mod2002018Key.BN941 },"Inversiones en Canarias (Ley 20/1991) 2006")
	 ,C18(new Mod2002018Key[]{Mod2002018Key.BN191 ,Mod2002018Key.BN192 ,Mod2002018Key.BN193 },"Inversiones en Canarias (Ley 20/1991) 2007")
	 ,C19(new Mod2002018Key[]{Mod2002018Key.BN613 ,Mod2002018Key.BN614 ,Mod2002018Key.BN701 },"Inversiones en Canarias (Ley 20/1991) 2008")
	 ,C20(new Mod2002018Key[]{Mod2002018Key.BN200 ,Mod2002018Key.BN257 ,Mod2002018Key.BN011 },"Inversiones en Canarias (Ley 20/1991) 2009")
	 ,C21(new Mod2002018Key[]{Mod2002018Key.BN037 ,Mod2002018Key.BN038 ,Mod2002018Key.BN039 },"Inversiones en Canarias (Ley 20/1991) 2010")
	 ,C22(new Mod2002018Key[]{Mod2002018Key.BN044 ,Mod2002018Key.BN045 ,Mod2002018Key.BN046 },"Inversiones en Canarias (Ley 20/1991) 2011")
	 ,C23(new Mod2002018Key[]{Mod2002018Key.BN528 ,Mod2002018Key.BN529 ,Mod2002018Key.BN530 },"Inversiones en Canarias (Ley 20/1991) 2012")
	 ,C24(new Mod2002018Key[]{Mod2002018Key.BN144 ,Mod2002018Key.BN145 ,Mod2002018Key.BN146 },"Inversiones en Canarias (Ley 20/1991) 2013")
	 ,C25(new Mod2002018Key[]{Mod2002018Key.BN147 ,Mod2002018Key.BN148 ,Mod2002018Key.BN149 },"Inversiones en Canarias (Ley 20/1991) 2014")
	 ,C26(new Mod2002018Key[]{Mod2002018Key.BN240 ,Mod2002018Key.BN241 ,Mod2002018Key.BN242 },"Inversiones en Canarias (Ley 20/1991) 2015")
	 ,C27(new Mod2002018Key[]{Mod2002018Key.BN1058,Mod2002018Key.BN1059,Mod2002018Key.BN1060},"Inversiones en Canarias (Ley 20/1991) 2016")
     ,C28(new Mod2002018Key[]{Mod2002018Key.BN791 ,Mod2002018Key.BN802 ,Mod2002018Key.BN806 },"Inversiones en Canarias (Ley 20/1991) 2017")
     ,C29(new Mod2002018Key[]{Mod2002018Key.BN1781,Mod2002018Key.BN1782,Mod2002018Key.BN1783},"Inversiones en Canarias (Ley 20/1991) 2018(*)")
     ,C30(new Mod2002018Key[]{Mod2002018Key.BN2119,Mod2002018Key.BN2120,Mod2002018Key.BN2121},"Inversiones en La Palma, La Gomera y El Hierro 2018(*)")
     ,C31(new Mod2002018Key[]{Mod2002018Key.BN2122,Mod2002018Key.BN2123,Mod2002018Key.BN2124},"Inversiones en Canarias (Ley 20/1991) 2018")
     ,C32(new Mod2002018Key[]{Mod2002018Key.BN2125,Mod2002018Key.BN2126,Mod2002018Key.BN2127},"Inversiones en La Palma, La Gomera y El Hierro 2018")
	 ,C33(new Mod2002018Key[]{Mod2002018Key.BN886 ,Mod2002018Key.BN590 ,Mod2002018Key.BN887 },"Total")
	;
	 
    private String description;
    
    private Mod2002018Key[] keys;
    
	private Mod2002018BN590Key(Mod2002018Key[] keys, String description) {
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

