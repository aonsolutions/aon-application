package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición internacional RDLeg. 4/2004
public enum Mod2002025BN572Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002025Key[]{Mod2002025Key.BN153,Mod2002025Key.BN728 ,Mod2002025Key.BN637,Mod2002025Key.BN638,Mod2002025Key.BN639},"D.I. internacional 2005")
	,C02(new Mod2002025Key[]{Mod2002025Key.BN154,Mod2002025Key.BN729 ,Mod2002025Key.BN849,Mod2002025Key.BN894,Mod2002025Key.BN197},"D.I. internacional 2006")
	,C03(new Mod2002025Key[]{Mod2002025Key.BN155,Mod2002025Key.BN730 ,Mod2002025Key.BN285,Mod2002025Key.BN286,Mod2002025Key.BN287},"D.I. internacional 2007")
	,C04(new Mod2002025Key[]{Mod2002025Key.BN156,Mod2002025Key.BN731 ,Mod2002025Key.BN825,Mod2002025Key.BN826,Mod2002025Key.BN827},"D.I. internacional 2008")
	,C05(new Mod2002025Key[]{Mod2002025Key.BN157,Mod2002025Key.BN732 ,Mod2002025Key.BN001,Mod2002025Key.BN002,Mod2002025Key.BN003},"D.I. internacional 2009")
	,C06(new Mod2002025Key[]{Mod2002025Key.BN158,Mod2002025Key.BN733 ,Mod2002025Key.BN028,Mod2002025Key.BN029,Mod2002025Key.BN030},"D.I. internacional 2010")
	,C07(new Mod2002025Key[]{Mod2002025Key.BN159,Mod2002025Key.BN734 ,Mod2002025Key.BN717,Mod2002025Key.BN718,Mod2002025Key.BN719},"D.I. internacional 2011")
	,C08(new Mod2002025Key[]{Mod2002025Key.BN720,Mod2002025Key.BN721 ,Mod2002025Key.BN722,Mod2002025Key.BN723,Mod2002025Key.BN724},"D.I. internacional 2012")
	,C09(new Mod2002025Key[]{Mod2002025Key.BN739,Mod2002025Key.BN921 ,Mod2002025Key.BN740,Mod2002025Key.BN741,Mod2002025Key.BN742},"D.I. internacional 2013")
	,C10(new Mod2002025Key[]{Mod2002025Key.BN134,Mod2002025Key.BN926 ,Mod2002025Key.BN135,Mod2002025Key.BN136,Mod2002025Key.BN137},"D.I. internacional 2014")
	,C11(new Mod2002025Key[]{Mod2002025Key.BN160,null  				 ,Mod2002025Key.BN161,Mod2002025Key.BN572,Mod2002025Key.BN162},"Total")
	,C12(new Mod2002025Key[]{null  				,Mod2002025Key.BN103C,null  			 ,null  			 ,null               },"Tipo de gravamen 2025")
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025BN572Key(Mod2002025Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002025Key[] getKeys() {
		return keys;
	}

}