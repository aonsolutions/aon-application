package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014BN572Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C0001(new Mod2002014Key[]{Mod2002014Key.BN151,Mod2002014Key.BN152,Mod2002014Key.BN711,Mod2002014Key.BN712,null               },"D.I.Intl. 2004")
	,C0002(new Mod2002014Key[]{Mod2002014Key.BN153,Mod2002014Key.BN728,Mod2002014Key.BN637,Mod2002014Key.BN638,Mod2002014Key.BN639},"D.I.Intl. 2005")
	,C0003(new Mod2002014Key[]{Mod2002014Key.BN154,Mod2002014Key.BN729,Mod2002014Key.BN849,Mod2002014Key.BN894,Mod2002014Key.BN197},"D.I.Intl. 2006")
	,C0004(new Mod2002014Key[]{Mod2002014Key.BN155,Mod2002014Key.BN730,Mod2002014Key.BN285,Mod2002014Key.BN286,Mod2002014Key.BN287},"D.I.Intl. 2007")
	,C0005(new Mod2002014Key[]{Mod2002014Key.BN156,Mod2002014Key.BN731,Mod2002014Key.BN825,Mod2002014Key.BN826,Mod2002014Key.BN827},"D.I.Intl. 2008")
	,C0006(new Mod2002014Key[]{Mod2002014Key.BN157,Mod2002014Key.BN732,Mod2002014Key.BN001,Mod2002014Key.BN002,Mod2002014Key.BN003},"D.I.Intl. 2009")
	,C0007(new Mod2002014Key[]{Mod2002014Key.BN158,Mod2002014Key.BN733,Mod2002014Key.BN028,Mod2002014Key.BN029,Mod2002014Key.BN030},"D.I.Intl. 2010")
	,C0008(new Mod2002014Key[]{Mod2002014Key.BN159,Mod2002014Key.BN734,Mod2002014Key.BN717,Mod2002014Key.BN718,Mod2002014Key.BN719},"D.I.Intl. 2011")
	,C0009(new Mod2002014Key[]{Mod2002014Key.BN720,Mod2002014Key.BN721,Mod2002014Key.BN722,Mod2002014Key.BN723,Mod2002014Key.BN724},"D.I.Intl. 2012")
	,C0010(new Mod2002014Key[]{Mod2002014Key.BN739,Mod2002014Key.BN921,Mod2002014Key.BN740,Mod2002014Key.BN741,Mod2002014Key.BN742},"D.I.Intl. 2013")
	,C0011(new Mod2002014Key[]{Mod2002014Key.BN134,Mod2002014Key.BN926,Mod2002014Key.BN135,Mod2002014Key.BN136,Mod2002014Key.BN137},"D.I.Intl. 2014")
	,C0012(new Mod2002014Key[]{Mod2002014Key.BN160,Mod2002014Key.BN103,Mod2002014Key.BN161,null           ,Mod2002014Key.BN162},"Total 2004-2014")
	;
	 
    private String description;
    private Mod2002014Key[] keys;

	private Mod2002014BN572Key(Mod2002014Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002014Key[] getKeys() {
		return keys;
	}

}

