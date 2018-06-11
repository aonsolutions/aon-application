package com.esferalia.aon.occam.api.model.fiscal.mod200_2017;

import java.io.Serializable;

// Deducciones inversión en Canarias.
public enum Mod2002017BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002017Key[]{Mod2002017Key.BN854 ,Mod2002017Key.BN855 ,Mod2002017Key.BN1356},"Activos fijos (Ley 20/1991) 2010")
	 ,C02(new Mod2002017Key[]{Mod2002017Key.BN857 ,Mod2002017Key.BN858 ,Mod2002017Key.BN859 },"Activos fijos (Ley 20/1991) 2011")
	 ,C03(new Mod2002017Key[]{Mod2002017Key.BN860 ,Mod2002017Key.BN861 ,Mod2002017Key.BN862 },"Activos fijos (Ley 20/1991) 2012")
	 ,C04(new Mod2002017Key[]{Mod2002017Key.BN863 ,Mod2002017Key.BN864 ,Mod2002017Key.BN865 },"Activos fijos (Ley 20/1991) 2013")
	 ,C05(new Mod2002017Key[]{Mod2002017Key.BN883 ,Mod2002017Key.BN884 ,Mod2002017Key.BN885 },"Activos fijos (Ley 20/1991) 2014")
	 ,C06(new Mod2002017Key[]{Mod2002017Key.BN785 ,Mod2002017Key.BN789 ,Mod2002017Key.BN790 },"Activos fijos (Ley 20/1991) 2015")
	 ,C07(new Mod2002017Key[]{Mod2002017Key.BN1357,Mod2002017Key.BN1358,Mod2002017Key.BN1359},"Activos fijos (Ley 20/1991) 2016")
	 ,C08(new Mod2002017Key[]{Mod2002017Key.BN1778,Mod2002017Key.BN1779,Mod2002017Key.BN1780},"Activos fijos (Ley 20/1991) 2017(*)")
	 ,C09(new Mod2002017Key[]{Mod2002017Key.BN852 ,Mod2002017Key.BN853 ,Mod2002017Key.BN856 },"Activos fijos (Ley 20/1991) 2017")
	 ,C11(new Mod2002017Key[]{Mod2002017Key.BN194 ,Mod2002017Key.BN195 ,null				},"Inversiones en Canarias (Ley 20/1991) 1999")
	 ,C12(new Mod2002017Key[]{Mod2002017Key.BN868 ,Mod2002017Key.BN869 ,Mod2002017Key.BN834 },"Inversiones en Canarias (Ley 20/1991) 2000")
	 ,C13(new Mod2002017Key[]{Mod2002017Key.BN871 ,Mod2002017Key.BN872 ,Mod2002017Key.BN873 },"Inversiones en Canarias (Ley 20/1991) 2001")
	 ,C14(new Mod2002017Key[]{Mod2002017Key.BN874 ,Mod2002017Key.BN875 ,Mod2002017Key.BN876 },"Inversiones en Canarias (Ley 20/1991) 2002")
	 ,C15(new Mod2002017Key[]{Mod2002017Key.BN877 ,Mod2002017Key.BN878 ,Mod2002017Key.BN879 },"Inversiones en Canarias (Ley 20/1991) 2003")
	 ,C16(new Mod2002017Key[]{Mod2002017Key.BN880 ,Mod2002017Key.BN881 ,Mod2002017Key.BN882 },"Inversiones en Canarias (Ley 20/1991) 2004")
	 ,C17(new Mod2002017Key[]{Mod2002017Key.BN866 ,Mod2002017Key.BN867 ,Mod2002017Key.BN870 },"Inversiones en Canarias (Ley 20/1991) 2005")
	 ,C18(new Mod2002017Key[]{Mod2002017Key.BN939 ,Mod2002017Key.BN940 ,Mod2002017Key.BN941 },"Inversiones en Canarias (Ley 20/1991) 2006")
	 ,C19(new Mod2002017Key[]{Mod2002017Key.BN191 ,Mod2002017Key.BN192 ,Mod2002017Key.BN193 },"Inversiones en Canarias (Ley 20/1991) 2007")
	 ,C20(new Mod2002017Key[]{Mod2002017Key.BN613 ,Mod2002017Key.BN614 ,Mod2002017Key.BN701 },"Inversiones en Canarias (Ley 20/1991) 2008")
	 ,C21(new Mod2002017Key[]{Mod2002017Key.BN200 ,Mod2002017Key.BN257 ,Mod2002017Key.BN011 },"Inversiones en Canarias (Ley 20/1991) 2009")
	 ,C22(new Mod2002017Key[]{Mod2002017Key.BN037 ,Mod2002017Key.BN038 ,Mod2002017Key.BN039 },"Inversiones en Canarias (Ley 20/1991) 2010")
	 ,C23(new Mod2002017Key[]{Mod2002017Key.BN044 ,Mod2002017Key.BN045 ,Mod2002017Key.BN046 },"Inversiones en Canarias (Ley 20/1991) 2011")
	 ,C24(new Mod2002017Key[]{Mod2002017Key.BN528 ,Mod2002017Key.BN529 ,Mod2002017Key.BN530 },"Inversiones en Canarias (Ley 20/1991) 2012")
	 ,C25(new Mod2002017Key[]{Mod2002017Key.BN144 ,Mod2002017Key.BN145 ,Mod2002017Key.BN146 },"Inversiones en Canarias (Ley 20/1991) 2013")
	 ,C26(new Mod2002017Key[]{Mod2002017Key.BN147 ,Mod2002017Key.BN148 ,Mod2002017Key.BN149 },"Inversiones en Canarias (Ley 20/1991) 2014")
	 ,C27(new Mod2002017Key[]{Mod2002017Key.BN240 ,Mod2002017Key.BN241 ,Mod2002017Key.BN242 },"Inversiones en Canarias (Ley 20/1991 y art. 27bis Ley 19/1994) 2015")
	 ,C28(new Mod2002017Key[]{Mod2002017Key.BN1058,Mod2002017Key.BN1059,Mod2002017Key.BN1060},"Inversiones en Canarias (Ley 20/1991 y art. 27bis Ley 19/1994) 2016")
     ,C29(new Mod2002017Key[]{Mod2002017Key.BN791 ,Mod2002017Key.BN802 ,Mod2002017Key.BN806 },"Inversiones en Canarias (Ley 20/1991 y art. 27bis Ley 19/1994) 2017(*)")
     ,C30(new Mod2002017Key[]{Mod2002017Key.BN1781,Mod2002017Key.BN1782,Mod2002017Key.BN1783},"Inversiones en Canarias (Ley 20/1991 y art. 27bis Ley 19/1994) 2017")
     ,C31(new Mod2002017Key[]{Mod2002017Key.BN852 ,Mod2002017Key.BN853 ,Mod2002017Key.BN856 },"Activos fijos (Ley 20/1991) 2016")
	 ,C32(new Mod2002017Key[]{Mod2002017Key.BN886 ,null /* BN590 */    ,Mod2002017Key.BN887 },"Total")
	;
	 
    private String description;
    
    private Mod2002017Key[] keys;
    
	private Mod2002017BN590Key(Mod2002017Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002017Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

