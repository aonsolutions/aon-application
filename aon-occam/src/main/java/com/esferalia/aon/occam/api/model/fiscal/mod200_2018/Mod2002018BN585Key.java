package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDL 4/2004 y art. 36 ter Ley 43/95
public enum Mod2002018BN585Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002018Key[]{Mod2002018Key.BN838 ,Mod2002018Key.BN839 ,null                },"Deducci\u00F3n art. 36 ter Ley 43/95 2003")
	,C02(new Mod2002018Key[]{Mod2002018Key.BN932 ,Mod2002018Key.BN933 ,Mod2002018Key.BN934 },"Deducci\u00F3n art. 42 RDL 4/2004 2004")
	,C03(new Mod2002018Key[]{Mod2002018Key.BN297 ,Mod2002018Key.BN298 ,Mod2002018Key.BN299 },"Deducci\u00F3n art. 42 RDL 4/2004 2005")
	,C04(new Mod2002018Key[]{Mod2002018Key.BN090 ,Mod2002018Key.BN091 ,Mod2002018Key.BN092 },"Deducci\u00F3n art. 42 RDL 4/2004 2006")
	,C05(new Mod2002018Key[]{Mod2002018Key.BN004 ,Mod2002018Key.BN005 ,Mod2002018Key.BN006 },"Deducci\u00F3n art. 42 RDL 4/2004 2007")
	,C06(new Mod2002018Key[]{Mod2002018Key.BN031 ,Mod2002018Key.BN032 ,Mod2002018Key.BN033 },"Deducci\u00F3n art. 42 RDL 4/2004 2008")
	,C07(new Mod2002018Key[]{Mod2002018Key.BN022 ,Mod2002018Key.BN023 ,Mod2002018Key.BN024 },"Deducci\u00F3n art. 42 RDL 4/2004 2009")
	,C08(new Mod2002018Key[]{Mod2002018Key.BN040 ,Mod2002018Key.BN041 ,Mod2002018Key.BN042 },"Deducci\u00F3n art. 42 RDL 4/2004 2010")
	,C09(new Mod2002018Key[]{Mod2002018Key.BN138 ,Mod2002018Key.BN139 ,Mod2002018Key.BN140 },"Deducci\u00F3n art. 42 RDL 4/2004 2011")
	,C10(new Mod2002018Key[]{Mod2002018Key.BN141 ,Mod2002018Key.BN142 ,Mod2002018Key.BN143 },"Deducci\u00F3n art. 42 RDL 4/2004 2012")
	,C11(new Mod2002018Key[]{Mod2002018Key.BN188 ,Mod2002018Key.BN189 ,Mod2002018Key.BN190 },"Deducci\u00F3n art. 42 RDL 4/2004 2013")
	,C12(new Mod2002018Key[]{Mod2002018Key.BN803 ,Mod2002018Key.BN804 ,Mod2002018Key.BN805 },"Deducci\u00F3n art. 42 RDL 4/2004 2014")
	,C13(new Mod2002018Key[]{Mod2002018Key.BN1055,Mod2002018Key.BN1056,Mod2002018Key.BN1057},"Deducci\u00F3n DT 24.7 LIS 2015")
	,C14(new Mod2002018Key[]{Mod2002018Key.BN700 ,Mod2002018Key.BN708 ,Mod2002018Key.BN709 },"Deducci\u00F3n DT 24.7 LIS 2016")
	,C15(new Mod2002018Key[]{Mod2002018Key.BN1353,Mod2002018Key.BN1354,Mod2002018Key.BN1355},"Deducci\u00F3n DT 24.7 LIS 2017")
	,C16(new Mod2002018Key[]{Mod2002018Key.BN1775,Mod2002018Key.BN1776,Mod2002018Key.BN1777},"Deducci\u00F3n DT 24.7 LIS 2018(*)")
	,C17(new Mod2002018Key[]{Mod2002018Key.BN1838,Mod2002018Key.BN1839,Mod2002018Key.BN1840},"Deducci\u00F3n DT 24.7 LIS 2018")
	,C18(new Mod2002018Key[]{Mod2002018Key.BN841 ,Mod2002018Key.BN585 ,Mod2002018Key.BN843 },"Total")
	;
	 
    private String description;
    
    private Mod2002018Key[] keys;
    
	private Mod2002018BN585Key(Mod2002018Key[] keys, String description) {
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

