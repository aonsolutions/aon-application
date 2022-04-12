package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDL 4/2004 y art. 36 ter Ley 43/95
public enum Mod2002015BN585Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	 C01(new Mod2002015Key[]{Mod2002015Key.BN835 ,Mod2002015Key.BN836 ,Mod2002015Key.BN837 },"Deducci\u00F3n art. 36 ter Ley 43/95 2002")
	,C02(new Mod2002015Key[]{Mod2002015Key.BN838 ,Mod2002015Key.BN839 ,Mod2002015Key.BN840 },"Deducci\u00F3n art. 36 ter Ley 43/95 2003")
	,C03(new Mod2002015Key[]{Mod2002015Key.BN932 ,Mod2002015Key.BN933 ,Mod2002015Key.BN934 },"Deducci\u00F3n art. 42 RDL 4/2004 2004")
	,C04(new Mod2002015Key[]{Mod2002015Key.BN297 ,Mod2002015Key.BN298 ,Mod2002015Key.BN299 },"Deducci\u00F3n art. 42 RDL 4/2004 2005")
	,C05(new Mod2002015Key[]{Mod2002015Key.BN090 ,Mod2002015Key.BN091 ,Mod2002015Key.BN092 },"Deducci\u00F3n art. 42 RDL 4/2004 2006")
	,C06(new Mod2002015Key[]{Mod2002015Key.BN004 ,Mod2002015Key.BN005 ,Mod2002015Key.BN006 },"Deducci\u00F3n art. 42 RDL 4/2004 2007")
	,C07(new Mod2002015Key[]{Mod2002015Key.BN031 ,Mod2002015Key.BN032 ,Mod2002015Key.BN033 },"Deducci\u00F3n art. 42 RDL 4/2004 2008")
	,C08(new Mod2002015Key[]{Mod2002015Key.BN022 ,Mod2002015Key.BN023 ,Mod2002015Key.BN024 },"Deducci\u00F3n art. 42 RDL 4/2004 2009")
	,C09(new Mod2002015Key[]{Mod2002015Key.BN040 ,Mod2002015Key.BN041 ,Mod2002015Key.BN042 },"Deducci\u00F3n art. 42 RDL 4/2004 2010")
	,C10(new Mod2002015Key[]{Mod2002015Key.BN138 ,Mod2002015Key.BN139 ,Mod2002015Key.BN140 },"Deducci\u00F3n art. 42 RDL 4/2004 2011")
	,C11(new Mod2002015Key[]{Mod2002015Key.BN141 ,Mod2002015Key.BN142 ,Mod2002015Key.BN143 },"Deducci\u00F3n art. 42 RDL 4/2004 2012")
	,C12(new Mod2002015Key[]{Mod2002015Key.BN188 ,Mod2002015Key.BN189 ,Mod2002015Key.BN190 },"Deducci\u00F3n art. 42 RDL 4/2004 2013")
	,C13(new Mod2002015Key[]{Mod2002015Key.BN803 ,Mod2002015Key.BN804 ,Mod2002015Key.BN805 },"Deducci\u00F3n art. 42 RDL 4/2004 2014")
	,C14(new Mod2002015Key[]{Mod2002015Key.BN1055,Mod2002015Key.BN1056,Mod2002015Key.BN1057},"Deducci\u00F3n DT 24.7 LIS 2015(*)")
	,C15(new Mod2002015Key[]{Mod2002015Key.BN700 ,Mod2002015Key.BN708 ,Mod2002015Key.BN709 },"Deducci\u00F3n DT 24.7 LIS 2015")
	,C16(new Mod2002015Key[]{Mod2002015Key.BN841 ,null /* BN585 */    ,Mod2002015Key.BN843 },"Total deducciones art. 36 ter Ley 43/95, art.42 RDL 4/2004 y DT 24.7 LIS")
	;
	 
    private String description;
    
    private Mod2002015Key[] keys;
    
	private Mod2002015BN585Key(Mod2002015Key[] keys, String description) {
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

