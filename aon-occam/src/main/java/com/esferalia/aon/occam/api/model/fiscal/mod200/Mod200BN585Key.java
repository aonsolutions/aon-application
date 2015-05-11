package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;


public enum Mod200BN585Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	 C0001(new Mod200Key[]{Mod200Key.BN835,Mod200Key.BN836,Mod200Key.BN837},"Deducci\u00F3n art. 36 ter Ley 43/95 2002")
	,C0002(new Mod200Key[]{Mod200Key.BN838,Mod200Key.BN839,Mod200Key.BN840},"Deducci\u00F3n art. 36 ter Ley 43/95 2003")
	,C0003(new Mod200Key[]{Mod200Key.BN932,Mod200Key.BN933,Mod200Key.BN934},"Deducci\u00F3n art. 42 L.I.S. 2004")
	,C0004(new Mod200Key[]{Mod200Key.BN297,Mod200Key.BN298,Mod200Key.BN299},"Deducci\u00F3n art. 42 L.I.S. 2005")
	,C0005(new Mod200Key[]{Mod200Key.BN090,Mod200Key.BN091,Mod200Key.BN092},"Deducci\u00F3n art. 42 L.I.S. 2006")
	,C0006(new Mod200Key[]{Mod200Key.BN004,Mod200Key.BN005,Mod200Key.BN006},"Deducci\u00F3n art. 42 L.I.S. 2007")
	,C0007(new Mod200Key[]{Mod200Key.BN031,Mod200Key.BN032,Mod200Key.BN033},"Deducci\u00F3n art. 42 L.I.S. 2008")
	,C0008(new Mod200Key[]{Mod200Key.BN022,Mod200Key.BN023,Mod200Key.BN024},"Deducci\u00F3n art. 42 L.I.S. 2009")
	,C0009(new Mod200Key[]{Mod200Key.BN040,Mod200Key.BN041,Mod200Key.BN042},"Deducci\u00F3n art. 42 L.I.S. 2010")
	,C0010(new Mod200Key[]{Mod200Key.BN138,Mod200Key.BN139,Mod200Key.BN140},"Deducci\u00F3n art. 42 L.I.S. 2011")
	,C0011(new Mod200Key[]{Mod200Key.BN141,Mod200Key.BN142,Mod200Key.BN143},"Deducci\u00F3n art. 42 L.I.S. 2012")
	,C0012(new Mod200Key[]{Mod200Key.BN188,Mod200Key.BN189,Mod200Key.BN190},"Deducci\u00F3n art. 42 L.I.S. 2013")
	,C0013(new Mod200Key[]{Mod200Key.BN841,null           ,Mod200Key.BN843},"Total deducciones art. 36 ter Ley 43/95 y art. 42 L.I.S.")
	;
	 
    private String description;
    
    private Mod200Key[] keys;
    
	private Mod200BN585Key(Mod200Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod200Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

