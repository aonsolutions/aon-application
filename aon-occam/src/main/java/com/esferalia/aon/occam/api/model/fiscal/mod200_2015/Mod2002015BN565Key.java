package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
public enum Mod2002015BN565Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002015Key[]{Mod2002015Key.BN929,Mod2002015Key.BN930,null               },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2005") 
	,C02(new Mod2002015Key[]{Mod2002015Key.BN942,Mod2002015Key.BN943,Mod2002015Key.BN944},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2006")
	,C03(new Mod2002015Key[]{Mod2002015Key.BN294,Mod2002015Key.BN295,Mod2002015Key.BN296},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2007")
	,C04(new Mod2002015Key[]{Mod2002015Key.BN066,Mod2002015Key.BN074,Mod2002015Key.BN084},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2008")
	,C05(new Mod2002015Key[]{Mod2002015Key.BN008,Mod2002015Key.BN009,Mod2002015Key.BN010},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2009")
	,C06(new Mod2002015Key[]{Mod2002015Key.BN034,Mod2002015Key.BN035,Mod2002015Key.BN036},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2010")
	,C07(new Mod2002015Key[]{Mod2002015Key.BN201,Mod2002015Key.BN202,Mod2002015Key.BN203},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2011")
	,C08(new Mod2002015Key[]{Mod2002015Key.BN904,Mod2002015Key.BN905,Mod2002015Key.BN906},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2012")
	,C09(new Mod2002015Key[]{Mod2002015Key.BN990,Mod2002015Key.BN991,Mod2002015Key.BN992},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2013")
	,C10(new Mod2002015Key[]{Mod2002015Key.BN997,Mod2002015Key.BN998,Mod2002015Key.BN999},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2014")
	,C11(new Mod2002015Key[]{Mod2002015Key.BN246,Mod2002015Key.BN247,Mod2002015Key.BN248},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2015(*)")
	,C12(new Mod2002015Key[]{Mod2002015Key.BN993,Mod2002015Key.BN994,Mod2002015Key.BN995},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2015")
	,C13(new Mod2002015Key[]{Mod2002015Key.BN598,null /* BN565 */   ,Mod2002015Key.BN895},"Total donaciones a entidades sin fines de lucro (Ley 49/2002)")
	,C14(new Mod2002015Key[]{Mod2002015Key.BN974,null               ,null               },"Donaciones del per\u00EDodo impositivo efectuadas a entidades sin fines de lucro (Ley 49/2002)")
	;
	 
    private String description;
    
    private Mod2002015Key[] keys;
    
	private Mod2002015BN565Key(Mod2002015Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002015Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

