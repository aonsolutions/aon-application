package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Deducciones doble imposición internacional RDL 4/2004
public enum Mod2002018BN572Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002018Key[]{Mod2002018Key.BN153,Mod2002018Key.BN728 ,Mod2002018Key.BN637,Mod2002018Key.BN638,Mod2002018Key.BN639},"D.I. internacional 2005")
	,C02(new Mod2002018Key[]{Mod2002018Key.BN154,Mod2002018Key.BN729 ,Mod2002018Key.BN849,Mod2002018Key.BN894,Mod2002018Key.BN197},"D.I. internacional 2006")
	,C03(new Mod2002018Key[]{Mod2002018Key.BN155,Mod2002018Key.BN730 ,Mod2002018Key.BN285,Mod2002018Key.BN286,Mod2002018Key.BN287},"D.I. internacional 2007")
	,C04(new Mod2002018Key[]{Mod2002018Key.BN156,Mod2002018Key.BN731 ,Mod2002018Key.BN825,Mod2002018Key.BN826,Mod2002018Key.BN827},"D.I. internacional 2008")
	,C05(new Mod2002018Key[]{Mod2002018Key.BN157,Mod2002018Key.BN732 ,Mod2002018Key.BN001,Mod2002018Key.BN002,Mod2002018Key.BN003},"D.I. internacional 2009")
	,C06(new Mod2002018Key[]{Mod2002018Key.BN158,Mod2002018Key.BN733 ,Mod2002018Key.BN028,Mod2002018Key.BN029,Mod2002018Key.BN030},"D.I. internacional 2010")
	,C07(new Mod2002018Key[]{Mod2002018Key.BN159,Mod2002018Key.BN734 ,Mod2002018Key.BN717,Mod2002018Key.BN718,Mod2002018Key.BN719},"D.I. internacional 2011")
	,C08(new Mod2002018Key[]{Mod2002018Key.BN720,Mod2002018Key.BN721 ,Mod2002018Key.BN722,Mod2002018Key.BN723,Mod2002018Key.BN724},"D.I. internacional 2012")
	,C09(new Mod2002018Key[]{Mod2002018Key.BN739,Mod2002018Key.BN921 ,Mod2002018Key.BN740,Mod2002018Key.BN741,Mod2002018Key.BN742},"D.I. internacional 2013")
	,C10(new Mod2002018Key[]{Mod2002018Key.BN134,Mod2002018Key.BN926 ,Mod2002018Key.BN135,Mod2002018Key.BN136,Mod2002018Key.BN137},"D.I. internacional 2014")
	,C11(new Mod2002018Key[]{Mod2002018Key.BN160,null  				 ,Mod2002018Key.BN161,Mod2002018Key.BN572,Mod2002018Key.BN162},"Total")
	,C12(new Mod2002018Key[]{null  				,Mod2002018Key.BN103C,null  				,null  				,null  				},"Tipo de gravamen 2018")
	;
	 
    private String description;
    private Mod2002018Key[] keys;

	private Mod2002018BN572Key(Mod2002018Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002018Key[] getKeys() {
		return keys;
	}

}

