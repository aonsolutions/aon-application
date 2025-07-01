package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDLeg. 4/2004
public enum Mod2002024BN585Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
		 
	 C01(new Mod2002024Key[]{Mod2002024Key.BN022 ,Mod2002024Key.BN023 ,null                },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2009")
	,C02(new Mod2002024Key[]{Mod2002024Key.BN040 ,Mod2002024Key.BN041 ,Mod2002024Key.BN042 },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2010")
	,C03(new Mod2002024Key[]{Mod2002024Key.BN138 ,Mod2002024Key.BN139 ,Mod2002024Key.BN140 },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2011")
	,C04(new Mod2002024Key[]{Mod2002024Key.BN141 ,Mod2002024Key.BN142 ,Mod2002024Key.BN143 },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2012")
	,C05(new Mod2002024Key[]{Mod2002024Key.BN188 ,Mod2002024Key.BN189 ,Mod2002024Key.BN190 },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2013")
	,C06(new Mod2002024Key[]{Mod2002024Key.BN803 ,Mod2002024Key.BN804 ,Mod2002024Key.BN805 },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2014")
	,C07(new Mod2002024Key[]{Mod2002024Key.BN1055,Mod2002024Key.BN1056,Mod2002024Key.BN1057},"Deducci\u00F3n DT 24\u00AA.7 LIS 2015")
	,C08(new Mod2002024Key[]{Mod2002024Key.BN700 ,Mod2002024Key.BN708 ,Mod2002024Key.BN709 },"Deducci\u00F3n DT 24\u00AA.7 LIS 2016")
	,C09(new Mod2002024Key[]{Mod2002024Key.BN1353,Mod2002024Key.BN1354,Mod2002024Key.BN1355},"Deducci\u00F3n DT 24\u00AA.7 LIS 2017")
	,C10(new Mod2002024Key[]{Mod2002024Key.BN1775,Mod2002024Key.BN1776,Mod2002024Key.BN1777},"Deducci\u00F3n DT 24\u00AA.7 LIS 2018")
	,C11(new Mod2002024Key[]{Mod2002024Key.BN1838,Mod2002024Key.BN1839,Mod2002024Key.BN1840},"Deducci\u00F3n DT 24\u00AA.7 LIS 2019")
	,C12(new Mod2002024Key[]{Mod2002024Key.BN2206,Mod2002024Key.BN2207,Mod2002024Key.BN2208},"Deducci\u00F3n DT 24\u00AA.7 LIS 2020")
	,C13(new Mod2002024Key[]{Mod2002024Key.BN2329,Mod2002024Key.BN2330,Mod2002024Key.BN2331},"Deducci\u00F3n DT 24\u00AA.7 LIS 2021")
	,C14(new Mod2002024Key[]{Mod2002024Key.BN249 ,Mod2002024Key.BN252 ,Mod2002024Key.BN253 },"Deducci\u00F3n DT 24\u00AA.7 LIS 2022")
	,C15(new Mod2002024Key[]{Mod2002024Key.BN696 ,Mod2002024Key.BN697 ,Mod2002024Key.BN710 },"Deducci\u00F3n DT 24\u00AA.7 LIS 2023")
	,C16(new Mod2002024Key[]{Mod2002024Key.BN1515,Mod2002024Key.BN1522,Mod2002024Key.BN1571},"Deducci\u00F3n DT 24\u00AA.7 LIS 2024(*)")
	,C17(new Mod2002024Key[]{Mod2002024Key.BN289 ,Mod2002024Key.BN291 ,Mod2002024Key.BN292 },"Deducci\u00F3n DT 24\u00AA.7 LIS 2024")
	,C18(new Mod2002024Key[]{Mod2002024Key.BN841 ,Mod2002024Key.BN585 ,Mod2002024Key.BN843 },"Total")
	;
	 
    private String description;
    private Mod2002024Key[] keys;
    
	private Mod2002024BN585Key(Mod2002024Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002024Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}

}

