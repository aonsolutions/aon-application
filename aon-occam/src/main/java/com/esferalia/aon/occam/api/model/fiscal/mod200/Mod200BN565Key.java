package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;


public enum Mod200BN565Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C0001(new Mod200Key[]{Mod200Key.BN929,Mod200Key.BN930,null           },"Donac. a ent. sin fines de lucro (Ley 49/2002) 2003") 
	,C0002(new Mod200Key[]{Mod200Key.BN942,Mod200Key.BN943,Mod200Key.BN944},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2004")
	,C0003(new Mod200Key[]{Mod200Key.BN294,Mod200Key.BN295,Mod200Key.BN296},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2005")
	,C0004(new Mod200Key[]{Mod200Key.BN066,Mod200Key.BN074,Mod200Key.BN084},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2006")
	,C0005(new Mod200Key[]{Mod200Key.BN008,Mod200Key.BN009,Mod200Key.BN010},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2007")
	,C0006(new Mod200Key[]{Mod200Key.BN034,Mod200Key.BN035,Mod200Key.BN036},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2008")
	,C0007(new Mod200Key[]{Mod200Key.BN201,Mod200Key.BN202,Mod200Key.BN203},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2009")
	,C0008(new Mod200Key[]{Mod200Key.BN904,Mod200Key.BN905,Mod200Key.BN906},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2010")
	,C0009(new Mod200Key[]{Mod200Key.BN990,Mod200Key.BN991,Mod200Key.BN992},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2011")
	,C0010(new Mod200Key[]{Mod200Key.BN997,Mod200Key.BN998,Mod200Key.BN999},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2012")
	,C0011(new Mod200Key[]{Mod200Key.BN993,Mod200Key.BN994,Mod200Key.BN995},"Donac. a ent. sin fines de lucro (Ley 49/2002) 2013")
	,C0012(new Mod200Key[]{Mod200Key.BN598,null           ,Mod200Key.BN895},"Total deducciones donac. a ent. sin fines de lucro (Ley 49/2002)")
	,C0013(new Mod200Key[]{Mod200Key.BN974,null           ,null           },"Donaciones del per\u00EDodo impositivo efectuadas a entidades sin fines de lucro (Ley 49/2002)")
	;
	 
    private String description;
    
    private Mod200Key[] keys;
    
	private Mod200BN565Key(Mod200Key[] keys, String description) {
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

