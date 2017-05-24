package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import java.io.Serializable;

// Deducciones doble imposición interna RDL 4/2004
public enum Mod2002016BN570Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002016Key[]{Mod2002016Key.BN104,Mod2002016Key.BN105,Mod2002016Key.BN846,Mod2002016Key.BN847,Mod2002016Key.BN848},"D.I. interna 2008")
	,C02(new Mod2002016Key[]{Mod2002016Key.BN106,Mod2002016Key.BN107,Mod2002016Key.BN282,Mod2002016Key.BN283,Mod2002016Key.BN284},"D.I. interna 2009")
	,C03(new Mod2002016Key[]{Mod2002016Key.BN108,Mod2002016Key.BN109,Mod2002016Key.BN702,Mod2002016Key.BN703,Mod2002016Key.BN707},"D.I. interna 2010")
	,C04(new Mod2002016Key[]{Mod2002016Key.BN110,Mod2002016Key.BN111,Mod2002016Key.BN071,Mod2002016Key.BN187,Mod2002016Key.BN300},"D.I. interna 2011")
	,C05(new Mod2002016Key[]{Mod2002016Key.BN112,Mod2002016Key.BN113,Mod2002016Key.BN025,Mod2002016Key.BN026,Mod2002016Key.BN027},"D.I. interna 2012")
	,C06(new Mod2002016Key[]{Mod2002016Key.BN114,Mod2002016Key.BN115,Mod2002016Key.BN714,Mod2002016Key.BN715,Mod2002016Key.BN716},"D.I. interna 2013")
	,C07(new Mod2002016Key[]{Mod2002016Key.BN735,Mod2002016Key.BN920,Mod2002016Key.BN736,Mod2002016Key.BN737,Mod2002016Key.BN738},"D.I. interna 2014")
	,C08(new Mod2002016Key[]{Mod2002016Key.BN116,null				,Mod2002016Key.BN117,null /* BN570 */   ,Mod2002016Key.BN118},"Total")
	,C09(new Mod2002016Key[]{null				,Mod2002016Key.BN103,null				,null				,null				},"Tipo de gravamen 2016")
	;
	 
    private String description;
    
    private Mod2002016Key[] keys;
    
	private Mod2002016BN570Key(Mod2002016Key[] keys, String description) {
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

