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
	
	 C0001(new Mod2002014Key[]{Mod2002014Key.BN929,Mod2002014Key.BN930,null           },"Donac. a ent. sin fines de lucro (Ley 49/2002) 2003") 
	,C0002(new Mod2002014Key[]{Mod2002014Key.BN942,Mod2002014Key.BN943,Mod2002014Key.BN944},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2004")
	,C0003(new Mod2002014Key[]{Mod2002014Key.BN294,Mod2002014Key.BN295,Mod2002014Key.BN296},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2005")
	,C0004(new Mod2002014Key[]{Mod2002014Key.BN066,Mod2002014Key.BN074,Mod2002014Key.BN084},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2006")
	,C0005(new Mod2002014Key[]{Mod2002014Key.BN008,Mod2002014Key.BN009,Mod2002014Key.BN010},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2007")
	,C0006(new Mod2002014Key[]{Mod2002014Key.BN034,Mod2002014Key.BN035,Mod2002014Key.BN036},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2008")
	,C0007(new Mod2002014Key[]{Mod2002014Key.BN201,Mod2002014Key.BN202,Mod2002014Key.BN203},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2009")
	,C0008(new Mod2002014Key[]{Mod2002014Key.BN904,Mod2002014Key.BN905,Mod2002014Key.BN906},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2010")
	,C0009(new Mod2002014Key[]{Mod2002014Key.BN990,Mod2002014Key.BN991,Mod2002014Key.BN992},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2011")
	,C0010(new Mod2002014Key[]{Mod2002014Key.BN997,Mod2002014Key.BN998,Mod2002014Key.BN999},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2012")
	,C0011(new Mod2002014Key[]{Mod2002014Key.BN993,Mod2002014Key.BN994,Mod2002014Key.BN995},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2013")
	,C0012(new Mod2002014Key[]{Mod2002014Key.BN598,null           ,Mod2002014Key.BN895},"Total deducciones donac. a ent. sin fines de lucro (Ley 49/2002)")
	,C0013(new Mod2002014Key[]{Mod2002014Key.BN974,null           ,null           },"Donaciones del per\u00EDodo impositivo efectuadas a entidades sin fines de lucro (Ley 49/2002)")
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

