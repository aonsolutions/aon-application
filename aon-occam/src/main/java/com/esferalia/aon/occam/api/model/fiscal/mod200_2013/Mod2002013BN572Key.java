package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;


public enum Mod2002013BN572Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C0001(new Mod2002013Key[]{Mod2002013Key.BN151,Mod2002013Key.BN152,Mod2002013Key.BN711,Mod2002013Key.BN712,null           },"D.I.internacional 2003")
	,C0002(new Mod2002013Key[]{Mod2002013Key.BN153,Mod2002013Key.BN728,Mod2002013Key.BN637,Mod2002013Key.BN638,Mod2002013Key.BN639},"D.I.internacional 2004")
	,C0003(new Mod2002013Key[]{Mod2002013Key.BN154,Mod2002013Key.BN729,Mod2002013Key.BN849,Mod2002013Key.BN894,Mod2002013Key.BN197},"D.I.internacional 2005")
	,C0004(new Mod2002013Key[]{Mod2002013Key.BN155,Mod2002013Key.BN730,Mod2002013Key.BN285,Mod2002013Key.BN286,Mod2002013Key.BN287},"D.I.internacional 2006")
	,C0005(new Mod2002013Key[]{Mod2002013Key.BN156,Mod2002013Key.BN731,Mod2002013Key.BN825,Mod2002013Key.BN826,Mod2002013Key.BN827},"D.I.internacional 2007")
	,C0006(new Mod2002013Key[]{Mod2002013Key.BN157,Mod2002013Key.BN732,Mod2002013Key.BN001,Mod2002013Key.BN002,Mod2002013Key.BN003},"D.I.internacional 2008")
	,C0007(new Mod2002013Key[]{Mod2002013Key.BN158,Mod2002013Key.BN733,Mod2002013Key.BN028,Mod2002013Key.BN029,Mod2002013Key.BN030},"D.I.internacional 2009")
	,C0008(new Mod2002013Key[]{Mod2002013Key.BN159,Mod2002013Key.BN734,Mod2002013Key.BN717,Mod2002013Key.BN718,Mod2002013Key.BN719},"D.I.internacional 2010")
	,C0009(new Mod2002013Key[]{Mod2002013Key.BN720,Mod2002013Key.BN721,Mod2002013Key.BN722,Mod2002013Key.BN723,Mod2002013Key.BN724},"D.I.internacional 2011")
	,C0010(new Mod2002013Key[]{Mod2002013Key.BN739,Mod2002013Key.BN921,Mod2002013Key.BN740,Mod2002013Key.BN741,Mod2002013Key.BN742},"D.I.internacional 2012")
	,C0011(new Mod2002013Key[]{Mod2002013Key.BN134,Mod2002013Key.BN926,Mod2002013Key.BN135,Mod2002013Key.BN136,Mod2002013Key.BN137},"D.I.internacional 2013")
	,C0012(new Mod2002013Key[]{Mod2002013Key.BN160,Mod2002013Key.BN103,Mod2002013Key.BN161,null           ,Mod2002013Key.BN162},"Total 2003-2013")
	;
	 
    private String description;
    private Mod2002013Key[] keys;

	private Mod2002013BN572Key(Mod2002013Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002013Key[] getKeys() {
		return keys;
	}

}

