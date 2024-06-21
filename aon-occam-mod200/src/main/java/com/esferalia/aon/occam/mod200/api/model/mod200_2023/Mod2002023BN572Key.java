package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducciones doble imposición internacional RDL 4/2004
public enum Mod2002023BN572Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002023Key[]{Mod2002023Key.BN153,Mod2002023Key.BN728 ,Mod2002023Key.BN637,Mod2002023Key.BN638,Mod2002023Key.BN639},"D.I. internacional 2005")
	,C02(new Mod2002023Key[]{Mod2002023Key.BN154,Mod2002023Key.BN729 ,Mod2002023Key.BN849,Mod2002023Key.BN894,Mod2002023Key.BN197},"D.I. internacional 2006")
	,C03(new Mod2002023Key[]{Mod2002023Key.BN155,Mod2002023Key.BN730 ,Mod2002023Key.BN285,Mod2002023Key.BN286,Mod2002023Key.BN287},"D.I. internacional 2007")
	,C04(new Mod2002023Key[]{Mod2002023Key.BN156,Mod2002023Key.BN731 ,Mod2002023Key.BN825,Mod2002023Key.BN826,Mod2002023Key.BN827},"D.I. internacional 2008")
	,C05(new Mod2002023Key[]{Mod2002023Key.BN157,Mod2002023Key.BN732 ,Mod2002023Key.BN001,Mod2002023Key.BN002,Mod2002023Key.BN003},"D.I. internacional 2009")
	,C06(new Mod2002023Key[]{Mod2002023Key.BN158,Mod2002023Key.BN733 ,Mod2002023Key.BN028,Mod2002023Key.BN029,Mod2002023Key.BN030},"D.I. internacional 2010")
	,C07(new Mod2002023Key[]{Mod2002023Key.BN159,Mod2002023Key.BN734 ,Mod2002023Key.BN717,Mod2002023Key.BN718,Mod2002023Key.BN719},"D.I. internacional 2011")
	,C08(new Mod2002023Key[]{Mod2002023Key.BN720,Mod2002023Key.BN721 ,Mod2002023Key.BN722,Mod2002023Key.BN723,Mod2002023Key.BN724},"D.I. internacional 2012")
	,C09(new Mod2002023Key[]{Mod2002023Key.BN739,Mod2002023Key.BN921 ,Mod2002023Key.BN740,Mod2002023Key.BN741,Mod2002023Key.BN742},"D.I. internacional 2013")
	,C10(new Mod2002023Key[]{Mod2002023Key.BN134,Mod2002023Key.BN926 ,Mod2002023Key.BN135,Mod2002023Key.BN136,Mod2002023Key.BN137},"D.I. internacional 2014")
	,C11(new Mod2002023Key[]{Mod2002023Key.BN160,null  				 ,Mod2002023Key.BN161,Mod2002023Key.BN572,Mod2002023Key.BN162},"Total")
	,C12(new Mod2002023Key[]{null  				,Mod2002023Key.BN103C,null  			 ,null  			 ,null               },"Tipo de gravamen 2023")
	;
	 
    private String description;
    private Mod2002023Key[] keys;

	private Mod2002023BN572Key(Mod2002023Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002023Key[] getKeys() {
		return keys;
	}

}