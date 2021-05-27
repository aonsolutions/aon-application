package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014BN565Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C0001(new Mod2002014Key[]{Mod2002014Key.BN929,Mod2002014Key.BN930,null               },Mod2002014Key.BN929.getDescription()) 
	,C0002(new Mod2002014Key[]{Mod2002014Key.BN942,Mod2002014Key.BN943,Mod2002014Key.BN944},Mod2002014Key.BN942.getDescription())
	,C0003(new Mod2002014Key[]{Mod2002014Key.BN294,Mod2002014Key.BN295,Mod2002014Key.BN296},Mod2002014Key.BN294.getDescription())
	,C0004(new Mod2002014Key[]{Mod2002014Key.BN066,Mod2002014Key.BN074,Mod2002014Key.BN084},Mod2002014Key.BN066.getDescription())
	,C0005(new Mod2002014Key[]{Mod2002014Key.BN008,Mod2002014Key.BN009,Mod2002014Key.BN010},Mod2002014Key.BN008.getDescription())
	,C0006(new Mod2002014Key[]{Mod2002014Key.BN034,Mod2002014Key.BN035,Mod2002014Key.BN036},Mod2002014Key.BN034.getDescription())
	,C0007(new Mod2002014Key[]{Mod2002014Key.BN201,Mod2002014Key.BN202,Mod2002014Key.BN203},Mod2002014Key.BN201.getDescription())
	,C0008(new Mod2002014Key[]{Mod2002014Key.BN904,Mod2002014Key.BN905,Mod2002014Key.BN906},Mod2002014Key.BN904.getDescription())
	,C0009(new Mod2002014Key[]{Mod2002014Key.BN990,Mod2002014Key.BN991,Mod2002014Key.BN992},Mod2002014Key.BN990.getDescription())
	,C0010(new Mod2002014Key[]{Mod2002014Key.BN997,Mod2002014Key.BN998,Mod2002014Key.BN999},Mod2002014Key.BN997.getDescription())
	,C0011(new Mod2002014Key[]{Mod2002014Key.BN246,Mod2002014Key.BN247,Mod2002014Key.BN248},Mod2002014Key.BN246.getDescription())
	,C0012(new Mod2002014Key[]{Mod2002014Key.BN993,Mod2002014Key.BN994,Mod2002014Key.BN995},Mod2002014Key.BN993.getDescription())
	,C0013(new Mod2002014Key[]{Mod2002014Key.BN598,null               ,Mod2002014Key.BN895},Mod2002014Key.BN598.getDescription())
	,C0014(new Mod2002014Key[]{Mod2002014Key.BN974,null               ,null               },Mod2002014Key.BN974.getDescription())
	;
	 
    private String description;
    
    private Mod2002014Key[] keys;
    
	private Mod2002014BN565Key(Mod2002014Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002014Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

