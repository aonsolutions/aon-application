package com.esferalia.aon.occam.api.model.fiscal.mod200_2017;

import java.io.Serializable;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
public enum Mod2002017BN565Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002017Key[]{Mod2002017Key.BN294 ,Mod2002017Key.BN295 ,null				   },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2007")
	,C02(new Mod2002017Key[]{Mod2002017Key.BN066 ,Mod2002017Key.BN074 ,Mod2002017Key.BN084 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2008")
	,C03(new Mod2002017Key[]{Mod2002017Key.BN008 ,Mod2002017Key.BN009 ,Mod2002017Key.BN010 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2009")
	,C04(new Mod2002017Key[]{Mod2002017Key.BN034 ,Mod2002017Key.BN035 ,Mod2002017Key.BN036 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2010")
	,C05(new Mod2002017Key[]{Mod2002017Key.BN201 ,Mod2002017Key.BN202 ,Mod2002017Key.BN203 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2011")
	,C06(new Mod2002017Key[]{Mod2002017Key.BN904 ,Mod2002017Key.BN905 ,Mod2002017Key.BN906 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2012")
	,C07(new Mod2002017Key[]{Mod2002017Key.BN990 ,Mod2002017Key.BN991 ,Mod2002017Key.BN992 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2013")
	,C08(new Mod2002017Key[]{Mod2002017Key.BN997 ,Mod2002017Key.BN998 ,Mod2002017Key.BN999 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2014")
	,C09(new Mod2002017Key[]{Mod2002017Key.BN246 ,Mod2002017Key.BN247 ,Mod2002017Key.BN248 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2015")
	,C10(new Mod2002017Key[]{Mod2002017Key.BN993 ,Mod2002017Key.BN994 ,Mod2002017Key.BN995 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2016")
	,C11(new Mod2002017Key[]{Mod2002017Key.BN1434,Mod2002017Key.BN1435,Mod2002017Key.BN1436},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2017(*)")
	,C12(new Mod2002017Key[]{Mod2002017Key.BN1718,Mod2002017Key.BN1719,Mod2002017Key.BN1720},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2017")
	,C13(new Mod2002017Key[]{Mod2002017Key.BN598,null /* BN565 */   ,Mod2002017Key.BN895},"Total")
	,C14(new Mod2002017Key[]{Mod2002017Key.BN974,null               ,null               },"Donaciones del per\u00EDodo impositivo efectuadas a entidades sin fines de lucro (Ley 49/2002)")
	;
	 
    private String description;
    
    private Mod2002017Key[] keys;
    
	private Mod2002017BN565Key(Mod2002017Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002017Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

