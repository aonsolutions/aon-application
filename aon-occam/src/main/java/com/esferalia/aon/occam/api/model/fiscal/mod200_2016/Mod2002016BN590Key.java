package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import java.io.Serializable;

// Deducciones inversión en Canarias. Ley 20/1991
public enum Mod2002016BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002016Key[]{Mod2002016Key.BN854 ,Mod2002016Key.BN855 ,null                },"Activos fijos (Ley 20/1991) 2010")
	 ,C02(new Mod2002016Key[]{Mod2002016Key.BN857 ,Mod2002016Key.BN858 ,Mod2002016Key.BN859 },"Activos fijos (Ley 20/1991) 2011")
	 ,C03(new Mod2002016Key[]{Mod2002016Key.BN860 ,Mod2002016Key.BN861 ,Mod2002016Key.BN862 },"Activos fijos (Ley 20/1991) 2012")
	 ,C04(new Mod2002016Key[]{Mod2002016Key.BN863 ,Mod2002016Key.BN864 ,Mod2002016Key.BN865 },"Activos fijos (Ley 20/1991) 2013")
	 ,C05(new Mod2002016Key[]{Mod2002016Key.BN883 ,Mod2002016Key.BN884 ,Mod2002016Key.BN885 },"Activos fijos (Ley 20/1991) 2014")
	 ,C06(new Mod2002016Key[]{Mod2002016Key.BN785 ,Mod2002016Key.BN789 ,Mod2002016Key.BN790 },"Activos fijos (Ley 20/1991) 2015(*)")
	 ,C07(new Mod2002016Key[]{Mod2002016Key.BN852 ,Mod2002016Key.BN853 ,Mod2002016Key.BN856 },"Activos fijos (Ley 20/1991) 2015")
	 ,C08(new Mod2002016Key[]{Mod2002016Key.BN088 ,Mod2002016Key.BN564 ,null                },"Inversiones en Canarias (Ley 20/1991) 1997")
	 ,C09(new Mod2002016Key[]{Mod2002016Key.BN194 ,Mod2002016Key.BN195 ,Mod2002016Key.BN196 },"Inversiones en Canarias (Ley 20/1991) 1998")
	 ,C10(new Mod2002016Key[]{Mod2002016Key.BN868 ,Mod2002016Key.BN869 ,Mod2002016Key.BN834 },"Inversiones en Canarias (Ley 20/1991) 1999")
	 ,C11(new Mod2002016Key[]{Mod2002016Key.BN871 ,Mod2002016Key.BN872 ,Mod2002016Key.BN873 },"Inversiones en Canarias (Ley 20/1991) 2000")
	 ,C12(new Mod2002016Key[]{Mod2002016Key.BN874 ,Mod2002016Key.BN875 ,Mod2002016Key.BN876 },"Inversiones en Canarias (Ley 20/1991) 2001")
	 ,C13(new Mod2002016Key[]{Mod2002016Key.BN877 ,Mod2002016Key.BN878 ,Mod2002016Key.BN879 },"Inversiones en Canarias (Ley 20/1991) 2002")
	 ,C14(new Mod2002016Key[]{Mod2002016Key.BN880 ,Mod2002016Key.BN881 ,Mod2002016Key.BN882 },"Inversiones en Canarias (Ley 20/1991) 2003")
	 ,C15(new Mod2002016Key[]{Mod2002016Key.BN866 ,Mod2002016Key.BN867 ,Mod2002016Key.BN870 },"Inversiones en Canarias (Ley 20/1991) 2004")
	 ,C16(new Mod2002016Key[]{Mod2002016Key.BN939 ,Mod2002016Key.BN940 ,Mod2002016Key.BN941 },"Inversiones en Canarias (Ley 20/1991) 2005")
	 ,C17(new Mod2002016Key[]{Mod2002016Key.BN191 ,Mod2002016Key.BN192 ,Mod2002016Key.BN193 },"Inversiones en Canarias (Ley 20/1991) 2006")
	 ,C18(new Mod2002016Key[]{Mod2002016Key.BN613 ,Mod2002016Key.BN614 ,Mod2002016Key.BN701 },"Inversiones en Canarias (Ley 20/1991) 2007")
	 ,C19(new Mod2002016Key[]{Mod2002016Key.BN200 ,Mod2002016Key.BN257 ,Mod2002016Key.BN011 },"Inversiones en Canarias (Ley 20/1991) 2008")
	 ,C20(new Mod2002016Key[]{Mod2002016Key.BN037 ,Mod2002016Key.BN038 ,Mod2002016Key.BN039 },"Inversiones en Canarias (Ley 20/1991) 2009")
	 ,C21(new Mod2002016Key[]{Mod2002016Key.BN044 ,Mod2002016Key.BN045 ,Mod2002016Key.BN046 },"Inversiones en Canarias (Ley 20/1991) 2010")
	 ,C22(new Mod2002016Key[]{Mod2002016Key.BN528 ,Mod2002016Key.BN529 ,Mod2002016Key.BN530 },"Inversiones en Canarias (Ley 20/1991) 2011")
	 ,C23(new Mod2002016Key[]{Mod2002016Key.BN144 ,Mod2002016Key.BN145 ,Mod2002016Key.BN146 },"Inversiones en Canarias (Ley 20/1991) 2012")
	 ,C24(new Mod2002016Key[]{Mod2002016Key.BN147 ,Mod2002016Key.BN148 ,Mod2002016Key.BN149 },"Inversiones en Canarias (Ley 20/1991) 2013")
	 ,C25(new Mod2002016Key[]{Mod2002016Key.BN240 ,Mod2002016Key.BN241 ,Mod2002016Key.BN242 },"Inversiones en Canarias (Ley 20/1991) 2014")
	 ,C26(new Mod2002016Key[]{Mod2002016Key.BN1058,Mod2002016Key.BN1059,Mod2002016Key.BN1060},"Inversiones en Canarias (Ley 20/1991) 2015(*)")
     ,C27(new Mod2002016Key[]{Mod2002016Key.BN791 ,Mod2002016Key.BN802 ,Mod2002016Key.BN806 },"Inversiones en Canarias (Ley 20/1991) 2015")
	 ,C28(new Mod2002016Key[]{Mod2002016Key.BN886 ,null /* BN590 */    ,Mod2002016Key.BN887 },"Total deducciones inversiones en Canarias (Ley 20/1991)")
	;
	 
    private String description;
    
    private Mod2002016Key[] keys;
    
	private Mod2002016BN590Key(Mod2002016Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002016Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

