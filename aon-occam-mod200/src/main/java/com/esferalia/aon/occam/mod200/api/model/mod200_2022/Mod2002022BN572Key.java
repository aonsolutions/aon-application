package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición internacional RDL 4/2004
public enum Mod2002022BN572Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002022Key[]{Mod2002022Key.BN153,Mod2002022Key.BN728 ,Mod2002022Key.BN637,Mod2002022Key.BN638,Mod2002022Key.BN639},"D.I. internacional 2005")
	,C02(new Mod2002022Key[]{Mod2002022Key.BN154,Mod2002022Key.BN729 ,Mod2002022Key.BN849,Mod2002022Key.BN894,Mod2002022Key.BN197},"D.I. internacional 2006")
	,C03(new Mod2002022Key[]{Mod2002022Key.BN155,Mod2002022Key.BN730 ,Mod2002022Key.BN285,Mod2002022Key.BN286,Mod2002022Key.BN287},"D.I. internacional 2007")
	,C04(new Mod2002022Key[]{Mod2002022Key.BN156,Mod2002022Key.BN731 ,Mod2002022Key.BN825,Mod2002022Key.BN826,Mod2002022Key.BN827},"D.I. internacional 2008")
	,C05(new Mod2002022Key[]{Mod2002022Key.BN157,Mod2002022Key.BN732 ,Mod2002022Key.BN001,Mod2002022Key.BN002,Mod2002022Key.BN003},"D.I. internacional 2009")
	,C06(new Mod2002022Key[]{Mod2002022Key.BN158,Mod2002022Key.BN733 ,Mod2002022Key.BN028,Mod2002022Key.BN029,Mod2002022Key.BN030},"D.I. internacional 2010")
	,C07(new Mod2002022Key[]{Mod2002022Key.BN159,Mod2002022Key.BN734 ,Mod2002022Key.BN717,Mod2002022Key.BN718,Mod2002022Key.BN719},"D.I. internacional 2011")
	,C08(new Mod2002022Key[]{Mod2002022Key.BN720,Mod2002022Key.BN721 ,Mod2002022Key.BN722,Mod2002022Key.BN723,Mod2002022Key.BN724},"D.I. internacional 2012")
	,C09(new Mod2002022Key[]{Mod2002022Key.BN739,Mod2002022Key.BN921 ,Mod2002022Key.BN740,Mod2002022Key.BN741,Mod2002022Key.BN742},"D.I. internacional 2013")
	,C10(new Mod2002022Key[]{Mod2002022Key.BN134,Mod2002022Key.BN926 ,Mod2002022Key.BN135,Mod2002022Key.BN136,Mod2002022Key.BN137},"D.I. internacional 2014")
	,C11(new Mod2002022Key[]{Mod2002022Key.BN160,null  				 ,Mod2002022Key.BN161,Mod2002022Key.BN572,Mod2002022Key.BN162},"Total")
	,C12(new Mod2002022Key[]{null  				,Mod2002022Key.BN103C,null  			 ,null  			 ,null               },"Tipo de gravamen 2021")
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022BN572Key(Mod2002022Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002022Key[] getKeys() {
		return keys;
	}


}

