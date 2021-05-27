package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Deducciones inversión en Canarias. Ley 20/1991
public enum Mod2002015BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002015Key[]{Mod2002015Key.BN854 ,Mod2002015Key.BN855 ,null                },"Activos fijos (Ley 20/1991) 2010")
	 ,C02(new Mod2002015Key[]{Mod2002015Key.BN857 ,Mod2002015Key.BN858 ,Mod2002015Key.BN859 },"Activos fijos (Ley 20/1991) 2011")
	 ,C03(new Mod2002015Key[]{Mod2002015Key.BN860 ,Mod2002015Key.BN861 ,Mod2002015Key.BN862 },"Activos fijos (Ley 20/1991) 2012")
	 ,C04(new Mod2002015Key[]{Mod2002015Key.BN863 ,Mod2002015Key.BN864 ,Mod2002015Key.BN865 },"Activos fijos (Ley 20/1991) 2013")
	 ,C05(new Mod2002015Key[]{Mod2002015Key.BN883 ,Mod2002015Key.BN884 ,Mod2002015Key.BN885 },"Activos fijos (Ley 20/1991) 2014")
	 ,C06(new Mod2002015Key[]{Mod2002015Key.BN785 ,Mod2002015Key.BN789 ,Mod2002015Key.BN790 },"Activos fijos (Ley 20/1991) 2015(*)")
	 ,C07(new Mod2002015Key[]{Mod2002015Key.BN852 ,Mod2002015Key.BN853 ,Mod2002015Key.BN856 },"Activos fijos (Ley 20/1991) 2015")
	 ,C08(new Mod2002015Key[]{Mod2002015Key.BN088 ,Mod2002015Key.BN564 ,null                },"Inversiones en Canarias (Ley 20/1991) 1997")
	 ,C09(new Mod2002015Key[]{Mod2002015Key.BN194 ,Mod2002015Key.BN195 ,Mod2002015Key.BN196 },"Inversiones en Canarias (Ley 20/1991) 1998")
	 ,C10(new Mod2002015Key[]{Mod2002015Key.BN868 ,Mod2002015Key.BN869 ,Mod2002015Key.BN834 },"Inversiones en Canarias (Ley 20/1991) 1999")
	 ,C11(new Mod2002015Key[]{Mod2002015Key.BN871 ,Mod2002015Key.BN872 ,Mod2002015Key.BN873 },"Inversiones en Canarias (Ley 20/1991) 2000")
	 ,C12(new Mod2002015Key[]{Mod2002015Key.BN874 ,Mod2002015Key.BN875 ,Mod2002015Key.BN876 },"Inversiones en Canarias (Ley 20/1991) 2001")
	 ,C13(new Mod2002015Key[]{Mod2002015Key.BN877 ,Mod2002015Key.BN878 ,Mod2002015Key.BN879 },"Inversiones en Canarias (Ley 20/1991) 2002")
	 ,C14(new Mod2002015Key[]{Mod2002015Key.BN880 ,Mod2002015Key.BN881 ,Mod2002015Key.BN882 },"Inversiones en Canarias (Ley 20/1991) 2003")
	 ,C15(new Mod2002015Key[]{Mod2002015Key.BN866 ,Mod2002015Key.BN867 ,Mod2002015Key.BN870 },"Inversiones en Canarias (Ley 20/1991) 2004")
	 ,C16(new Mod2002015Key[]{Mod2002015Key.BN939 ,Mod2002015Key.BN940 ,Mod2002015Key.BN941 },"Inversiones en Canarias (Ley 20/1991) 2005")
	 ,C17(new Mod2002015Key[]{Mod2002015Key.BN191 ,Mod2002015Key.BN192 ,Mod2002015Key.BN193 },"Inversiones en Canarias (Ley 20/1991) 2006")
	 ,C18(new Mod2002015Key[]{Mod2002015Key.BN613 ,Mod2002015Key.BN614 ,Mod2002015Key.BN701 },"Inversiones en Canarias (Ley 20/1991) 2007")
	 ,C19(new Mod2002015Key[]{Mod2002015Key.BN200 ,Mod2002015Key.BN257 ,Mod2002015Key.BN011 },"Inversiones en Canarias (Ley 20/1991) 2008")
	 ,C20(new Mod2002015Key[]{Mod2002015Key.BN037 ,Mod2002015Key.BN038 ,Mod2002015Key.BN039 },"Inversiones en Canarias (Ley 20/1991) 2009")
	 ,C21(new Mod2002015Key[]{Mod2002015Key.BN044 ,Mod2002015Key.BN045 ,Mod2002015Key.BN046 },"Inversiones en Canarias (Ley 20/1991) 2010")
	 ,C22(new Mod2002015Key[]{Mod2002015Key.BN528 ,Mod2002015Key.BN529 ,Mod2002015Key.BN530 },"Inversiones en Canarias (Ley 20/1991) 2011")
	 ,C23(new Mod2002015Key[]{Mod2002015Key.BN144 ,Mod2002015Key.BN145 ,Mod2002015Key.BN146 },"Inversiones en Canarias (Ley 20/1991) 2012")
	 ,C24(new Mod2002015Key[]{Mod2002015Key.BN147 ,Mod2002015Key.BN148 ,Mod2002015Key.BN149 },"Inversiones en Canarias (Ley 20/1991) 2013")
	 ,C25(new Mod2002015Key[]{Mod2002015Key.BN240 ,Mod2002015Key.BN241 ,Mod2002015Key.BN242 },"Inversiones en Canarias (Ley 20/1991) 2014")
	 ,C26(new Mod2002015Key[]{Mod2002015Key.BN1058,Mod2002015Key.BN1059,Mod2002015Key.BN1060},"Inversiones en Canarias (Ley 20/1991) 2015(*)")
     ,C27(new Mod2002015Key[]{Mod2002015Key.BN791 ,Mod2002015Key.BN802 ,Mod2002015Key.BN806 },"Inversiones en Canarias (Ley 20/1991) 2015")
	 ,C28(new Mod2002015Key[]{Mod2002015Key.BN886 ,null /* BN590 */    ,Mod2002015Key.BN887 },"Total deducciones inversiones en Canarias (Ley 20/1991)")
	;
	 
    private String description;
    
    private Mod2002015Key[] keys;
    
	private Mod2002015BN590Key(Mod2002015Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002015Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

