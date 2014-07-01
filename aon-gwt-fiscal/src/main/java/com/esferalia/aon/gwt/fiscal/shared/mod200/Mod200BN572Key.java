package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public enum Mod200BN572Key implements Serializable, IsSerializable,IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C0001(new Mod200Key[]{Mod200Key.BN151,Mod200Key.BN152,Mod200Key.BN711,Mod200Key.BN712,null           },"D.I.internacional 2003")
	,C0002(new Mod200Key[]{Mod200Key.BN153,Mod200Key.BN728,Mod200Key.BN637,Mod200Key.BN638,Mod200Key.BN639},"D.I.internacional 2004")
	,C0003(new Mod200Key[]{Mod200Key.BN154,Mod200Key.BN729,Mod200Key.BN849,Mod200Key.BN894,Mod200Key.BN197},"D.I.internacional 2005")
	,C0004(new Mod200Key[]{Mod200Key.BN155,Mod200Key.BN730,Mod200Key.BN285,Mod200Key.BN286,Mod200Key.BN287},"D.I.internacional 2006")
	,C0005(new Mod200Key[]{Mod200Key.BN156,Mod200Key.BN731,Mod200Key.BN825,Mod200Key.BN826,Mod200Key.BN827},"D.I.internacional 2007")
	,C0006(new Mod200Key[]{Mod200Key.BN157,Mod200Key.BN732,Mod200Key.BN001,Mod200Key.BN002,Mod200Key.BN003},"D.I.internacional 2008")
	,C0007(new Mod200Key[]{Mod200Key.BN158,Mod200Key.BN733,Mod200Key.BN028,Mod200Key.BN029,Mod200Key.BN030},"D.I.internacional 2009")
	,C0008(new Mod200Key[]{Mod200Key.BN159,Mod200Key.BN734,Mod200Key.BN717,Mod200Key.BN718,Mod200Key.BN719},"D.I.internacional 2010")
	,C0009(new Mod200Key[]{Mod200Key.BN720,Mod200Key.BN721,Mod200Key.BN722,Mod200Key.BN723,Mod200Key.BN724},"D.I.internacional 2011")
	,C0010(new Mod200Key[]{Mod200Key.BN739,Mod200Key.BN921,Mod200Key.BN740,Mod200Key.BN741,Mod200Key.BN742},"D.I.internacional 2012")
	,C0011(new Mod200Key[]{Mod200Key.BN134,Mod200Key.BN926,Mod200Key.BN135,Mod200Key.BN136,Mod200Key.BN137},"D.I.internacional 2013")
	,C0012(new Mod200Key[]{Mod200Key.BN160,Mod200Key.BN103,Mod200Key.BN161,null           ,Mod200Key.BN162},"Total 2003-2013")
	;
	 
    private String description;
    private Mod200Key[] keys;

	private Mod200BN572Key(Mod200Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod200Key[] getKeys() {
		return keys;
	}

}

