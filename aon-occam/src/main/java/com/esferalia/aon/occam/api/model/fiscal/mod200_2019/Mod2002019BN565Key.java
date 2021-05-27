package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import java.io.Serializable;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
public enum Mod2002019BN565Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002019Key[]{Mod2002019Key.BN008 ,Mod2002019Key.BN009 ,null                },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2009")
	,C02(new Mod2002019Key[]{Mod2002019Key.BN034 ,Mod2002019Key.BN035 ,Mod2002019Key.BN036 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2010")
	,C03(new Mod2002019Key[]{Mod2002019Key.BN201 ,Mod2002019Key.BN202 ,Mod2002019Key.BN203 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2011")
	,C04(new Mod2002019Key[]{Mod2002019Key.BN904 ,Mod2002019Key.BN905 ,Mod2002019Key.BN906 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2012")
	,C05(new Mod2002019Key[]{Mod2002019Key.BN990 ,Mod2002019Key.BN991 ,Mod2002019Key.BN992 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2013")
	,C06(new Mod2002019Key[]{Mod2002019Key.BN997 ,Mod2002019Key.BN998 ,Mod2002019Key.BN999 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2014")
	,C07(new Mod2002019Key[]{Mod2002019Key.BN246 ,Mod2002019Key.BN247 ,Mod2002019Key.BN248 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2015")
	,C08(new Mod2002019Key[]{Mod2002019Key.BN993 ,Mod2002019Key.BN994 ,Mod2002019Key.BN995 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2016")
	,C09(new Mod2002019Key[]{Mod2002019Key.BN1434,Mod2002019Key.BN1435,Mod2002019Key.BN1436},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2017")
	,C10(new Mod2002019Key[]{Mod2002019Key.BN1718,Mod2002019Key.BN1719,Mod2002019Key.BN1720},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2018")
	,C11(new Mod2002019Key[]{Mod2002019Key.BN1950,Mod2002019Key.BN1951,Mod2002019Key.BN1952},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2019(*)")
	,C12(new Mod2002019Key[]{Mod2002019Key.BN2227,Mod2002019Key.BN2228,Mod2002019Key.BN2229},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2019")
	,C13(new Mod2002019Key[]{Mod2002019Key.BN598 ,Mod2002019Key.BN565 ,Mod2002019Key.BN895 },"Total")
	,C14(new Mod2002019Key[]{Mod2002019Key.BN974 ,null                ,null                },"Donaciones del per\u00EDodo impositivo efectuadas a entidades sin fines de lucro (Ley 49/2002)")
	;
	 
    private String description;
    
    private Mod2002019Key[] keys;
    
	private Mod2002019BN565Key(Mod2002019Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002019Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

