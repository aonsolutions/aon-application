package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import java.io.Serializable;

// Deducciones doble imposición internacional RDL 4/2004
public enum Mod2002016BN572Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002016Key[]{Mod2002016Key.BN153,Mod2002016Key.BN728,Mod2002016Key.BN637,Mod2002016Key.BN638,Mod2002016Key.BN639},"D.I. internacional 2005")
	,C02(new Mod2002016Key[]{Mod2002016Key.BN154,Mod2002016Key.BN729,Mod2002016Key.BN849,Mod2002016Key.BN894,Mod2002016Key.BN197},"D.I. internacional 2006")
	,C03(new Mod2002016Key[]{Mod2002016Key.BN155,Mod2002016Key.BN730,Mod2002016Key.BN285,Mod2002016Key.BN286,Mod2002016Key.BN287},"D.I. internacional 2007")
	,C04(new Mod2002016Key[]{Mod2002016Key.BN156,Mod2002016Key.BN731,Mod2002016Key.BN825,Mod2002016Key.BN826,Mod2002016Key.BN827},"D.I. internacional 2008")
	,C05(new Mod2002016Key[]{Mod2002016Key.BN157,Mod2002016Key.BN732,Mod2002016Key.BN001,Mod2002016Key.BN002,Mod2002016Key.BN003},"D.I. internacional 2009")
	,C06(new Mod2002016Key[]{Mod2002016Key.BN158,Mod2002016Key.BN733,Mod2002016Key.BN028,Mod2002016Key.BN029,Mod2002016Key.BN030},"D.I. internacional 2010")
	,C07(new Mod2002016Key[]{Mod2002016Key.BN159,Mod2002016Key.BN734,Mod2002016Key.BN717,Mod2002016Key.BN718,Mod2002016Key.BN719},"D.I. internacional 2011")
	,C08(new Mod2002016Key[]{Mod2002016Key.BN720,Mod2002016Key.BN721,Mod2002016Key.BN722,Mod2002016Key.BN723,Mod2002016Key.BN724},"D.I. internacional 2012")
	,C09(new Mod2002016Key[]{Mod2002016Key.BN739,Mod2002016Key.BN921,Mod2002016Key.BN740,Mod2002016Key.BN741,Mod2002016Key.BN742},"D.I. internacional 2013")
	,C10(new Mod2002016Key[]{Mod2002016Key.BN134,Mod2002016Key.BN926,Mod2002016Key.BN135,Mod2002016Key.BN136,Mod2002016Key.BN137},"D.I. internacional 2014")
	,C11(new Mod2002016Key[]{Mod2002016Key.BN160,null  				,Mod2002016Key.BN161,null /* BN572 */   ,Mod2002016Key.BN162},"Total")
	,C12(new Mod2002016Key[]{null  				,Mod2002016Key.BN103,null  				,null  				,null  				},"Tipo de gravamen 2016")
	;
	 
    private String description;
    private Mod2002016Key[] keys;

	private Mod2002016BN572Key(Mod2002016Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002016Key[] getKeys() {
		return keys;
	}

}

