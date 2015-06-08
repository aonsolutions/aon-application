package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;


public enum Mod2002013BN585Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	 C0001(new Mod2002013Key[]{Mod2002013Key.BN835,Mod2002013Key.BN836,Mod2002013Key.BN837},"Deducci\u00F3n art. 36 ter Ley 43/95 2002")
	,C0002(new Mod2002013Key[]{Mod2002013Key.BN838,Mod2002013Key.BN839,Mod2002013Key.BN840},"Deducci\u00F3n art. 36 ter Ley 43/95 2003")
	,C0003(new Mod2002013Key[]{Mod2002013Key.BN932,Mod2002013Key.BN933,Mod2002013Key.BN934},"Deducci\u00F3n art. 42 L.I.S. 2004")
	,C0004(new Mod2002013Key[]{Mod2002013Key.BN297,Mod2002013Key.BN298,Mod2002013Key.BN299},"Deducci\u00F3n art. 42 L.I.S. 2005")
	,C0005(new Mod2002013Key[]{Mod2002013Key.BN090,Mod2002013Key.BN091,Mod2002013Key.BN092},"Deducci\u00F3n art. 42 L.I.S. 2006")
	,C0006(new Mod2002013Key[]{Mod2002013Key.BN004,Mod2002013Key.BN005,Mod2002013Key.BN006},"Deducci\u00F3n art. 42 L.I.S. 2007")
	,C0007(new Mod2002013Key[]{Mod2002013Key.BN031,Mod2002013Key.BN032,Mod2002013Key.BN033},"Deducci\u00F3n art. 42 L.I.S. 2008")
	,C0008(new Mod2002013Key[]{Mod2002013Key.BN022,Mod2002013Key.BN023,Mod2002013Key.BN024},"Deducci\u00F3n art. 42 L.I.S. 2009")
	,C0009(new Mod2002013Key[]{Mod2002013Key.BN040,Mod2002013Key.BN041,Mod2002013Key.BN042},"Deducci\u00F3n art. 42 L.I.S. 2010")
	,C0010(new Mod2002013Key[]{Mod2002013Key.BN138,Mod2002013Key.BN139,Mod2002013Key.BN140},"Deducci\u00F3n art. 42 L.I.S. 2011")
	,C0011(new Mod2002013Key[]{Mod2002013Key.BN141,Mod2002013Key.BN142,Mod2002013Key.BN143},"Deducci\u00F3n art. 42 L.I.S. 2012")
	,C0012(new Mod2002013Key[]{Mod2002013Key.BN188,Mod2002013Key.BN189,Mod2002013Key.BN190},"Deducci\u00F3n art. 42 L.I.S. 2013")
	,C0013(new Mod2002013Key[]{Mod2002013Key.BN841,null           ,Mod2002013Key.BN843},"Total deducciones art. 36 ter Ley 43/95 y art. 42 L.I.S.")
	;
	 
    private String description;
    
    private Mod2002013Key[] keys;
    
	private Mod2002013BN585Key(Mod2002013Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002013Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

