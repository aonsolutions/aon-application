package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones disposición transitoria 24ª.7 LIS, art. 42 RDLeg. 4/2004
public enum Mod2002025BN585Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
		 
	 C01(new Mod2002025Key[]{Mod2002025Key.BN040 ,Mod2002025Key.BN041 ,null                },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2010")
	,C02(new Mod2002025Key[]{Mod2002025Key.BN138 ,Mod2002025Key.BN139 ,Mod2002025Key.BN140 },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2011")
	,C03(new Mod2002025Key[]{Mod2002025Key.BN141 ,Mod2002025Key.BN142 ,Mod2002025Key.BN143 },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2012")
	,C04(new Mod2002025Key[]{Mod2002025Key.BN188 ,Mod2002025Key.BN189 ,Mod2002025Key.BN190 },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2013")
	,C05(new Mod2002025Key[]{Mod2002025Key.BN803 ,Mod2002025Key.BN804 ,Mod2002025Key.BN805 },"Deducci\u00F3n art. 42 RDLeg. 4/2004 2014")
	,C06(new Mod2002025Key[]{Mod2002025Key.BN1055,Mod2002025Key.BN1056,Mod2002025Key.BN1057},"Deducci\u00F3n DT 24\u00AA.7 LIS 2015")
	,C07(new Mod2002025Key[]{Mod2002025Key.BN700 ,Mod2002025Key.BN708 ,Mod2002025Key.BN709 },"Deducci\u00F3n DT 24\u00AA.7 LIS 2016")
	,C08(new Mod2002025Key[]{Mod2002025Key.BN1353,Mod2002025Key.BN1354,Mod2002025Key.BN1355},"Deducci\u00F3n DT 24\u00AA.7 LIS 2017")
	,C19(new Mod2002025Key[]{Mod2002025Key.BN1775,Mod2002025Key.BN1776,Mod2002025Key.BN1777},"Deducci\u00F3n DT 24\u00AA.7 LIS 2018")
	,C10(new Mod2002025Key[]{Mod2002025Key.BN1838,Mod2002025Key.BN1839,Mod2002025Key.BN1840},"Deducci\u00F3n DT 24\u00AA.7 LIS 2019")
	,C11(new Mod2002025Key[]{Mod2002025Key.BN2206,Mod2002025Key.BN2207,Mod2002025Key.BN2208},"Deducci\u00F3n DT 24\u00AA.7 LIS 2020")
	,C12(new Mod2002025Key[]{Mod2002025Key.BN2329,Mod2002025Key.BN2330,Mod2002025Key.BN2331},"Deducci\u00F3n DT 24\u00AA.7 LIS 2021")
	,C13(new Mod2002025Key[]{Mod2002025Key.BN249 ,Mod2002025Key.BN252 ,Mod2002025Key.BN253 },"Deducci\u00F3n DT 24\u00AA.7 LIS 2022")
	,C14(new Mod2002025Key[]{Mod2002025Key.BN696 ,Mod2002025Key.BN697 ,Mod2002025Key.BN710 },"Deducci\u00F3n DT 24\u00AA.7 LIS 2023")
	,C15(new Mod2002025Key[]{Mod2002025Key.BN1515,Mod2002025Key.BN1522,Mod2002025Key.BN1571},"Deducci\u00F3n DT 24\u00AA.7 LIS 2024")
	,C16(new Mod2002025Key[]{Mod2002025Key.BN289 ,Mod2002025Key.BN291 ,Mod2002025Key.BN292 },"Deducci\u00F3n DT 24\u00AA.7 LIS 2025(*)")
	,C17(new Mod2002025Key[]{Mod2002025Key.BN3421,Mod2002025Key.BN3422,Mod2002025Key.BN3423},"Deducci\u00F3n DT 24\u00AA.7 LIS 2025")
	,C18(new Mod2002025Key[]{Mod2002025Key.BN841 ,Mod2002025Key.BN585 ,Mod2002025Key.BN843 },"Total")
	;
	 
    private String description;
    private Mod2002025Key[] keys;
    
	private Mod2002025BN585Key(Mod2002025Key[] keys, String description) {
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
