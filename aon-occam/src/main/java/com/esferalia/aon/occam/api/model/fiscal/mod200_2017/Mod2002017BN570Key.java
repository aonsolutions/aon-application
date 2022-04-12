package com.esferalia.aon.occam.api.model.fiscal.mod200_2017;

import java.io.Serializable;

// Deducciones doble imposición interna RDL 4/2004
public enum Mod2002017BN570Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002017Key[]{Mod2002017Key.BN104,Mod2002017Key.BN105  ,Mod2002017Key.BN846,Mod2002017Key.BN847,Mod2002017Key.BN848},"D.I. interna 2008")
	,C02(new Mod2002017Key[]{Mod2002017Key.BN106,Mod2002017Key.BN107  ,Mod2002017Key.BN282,Mod2002017Key.BN283,Mod2002017Key.BN284},"D.I. interna 2009")
	,C03(new Mod2002017Key[]{Mod2002017Key.BN108,Mod2002017Key.BN109  ,Mod2002017Key.BN702,Mod2002017Key.BN703,Mod2002017Key.BN707},"D.I. interna 2010")
	,C04(new Mod2002017Key[]{Mod2002017Key.BN110,Mod2002017Key.BN111  ,Mod2002017Key.BN071,Mod2002017Key.BN187,Mod2002017Key.BN300},"D.I. interna 2011")
	,C05(new Mod2002017Key[]{Mod2002017Key.BN112,Mod2002017Key.BN113  ,Mod2002017Key.BN025,Mod2002017Key.BN026,Mod2002017Key.BN027},"D.I. interna 2012")
	,C06(new Mod2002017Key[]{Mod2002017Key.BN114,Mod2002017Key.BN115  ,Mod2002017Key.BN714,Mod2002017Key.BN715,Mod2002017Key.BN716},"D.I. interna 2013")
	,C07(new Mod2002017Key[]{Mod2002017Key.BN735,Mod2002017Key.BN920  ,Mod2002017Key.BN736,Mod2002017Key.BN737,Mod2002017Key.BN738},"D.I. interna 2014")
	,C08(new Mod2002017Key[]{Mod2002017Key.BN116,null				  ,Mod2002017Key.BN117,null /* BN570 */   ,Mod2002017Key.BN118},"Total")
	,C09(new Mod2002017Key[]{null				,Mod2002017Key.BN103A ,null				,null				,null				},"Tipo de gravamen 2017")
	;
	 
    private String description;
    
    private Mod2002017Key[] keys;
    
	private Mod2002017BN570Key(Mod2002017Key[] keys, String description) {
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

