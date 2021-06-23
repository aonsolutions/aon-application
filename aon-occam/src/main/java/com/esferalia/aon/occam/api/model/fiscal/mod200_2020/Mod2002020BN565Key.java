package com.esferalia.aon.occam.api.model.fiscal.mod200_2020;

import java.io.Serializable;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
public enum Mod2002020BN565Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002020Key[]{Mod2002020Key.BN034 ,Mod2002020Key.BN035 ,null                },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2010")
	,C02(new Mod2002020Key[]{Mod2002020Key.BN201 ,Mod2002020Key.BN202 ,Mod2002020Key.BN203 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2011")
	,C03(new Mod2002020Key[]{Mod2002020Key.BN904 ,Mod2002020Key.BN905 ,Mod2002020Key.BN906 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2012")
	,C04(new Mod2002020Key[]{Mod2002020Key.BN990 ,Mod2002020Key.BN991 ,Mod2002020Key.BN992 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2013")
	,C05(new Mod2002020Key[]{Mod2002020Key.BN997 ,Mod2002020Key.BN998 ,Mod2002020Key.BN999 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2014")
	,C06(new Mod2002020Key[]{Mod2002020Key.BN246 ,Mod2002020Key.BN247 ,Mod2002020Key.BN248 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2015")
	,C07(new Mod2002020Key[]{Mod2002020Key.BN993 ,Mod2002020Key.BN994 ,Mod2002020Key.BN995 },"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2016")
	,C08(new Mod2002020Key[]{Mod2002020Key.BN1434,Mod2002020Key.BN1435,Mod2002020Key.BN1436},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2017")
	,C09(new Mod2002020Key[]{Mod2002020Key.BN1718,Mod2002020Key.BN1719,Mod2002020Key.BN1720},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2018")
	,C10(new Mod2002020Key[]{Mod2002020Key.BN1950,Mod2002020Key.BN1951,Mod2002020Key.BN1952},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2019")
	,C11(new Mod2002020Key[]{Mod2002020Key.BN2227,Mod2002020Key.BN2228,Mod2002020Key.BN2229},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2020(*)")
	,C12(new Mod2002020Key[]{Mod2002020Key.BN2380,Mod2002020Key.BN2381,Mod2002020Key.BN2382},"Donaciones a entidades sin fines de lucro (Ley 49/2002) 2020)")
	,C13(new Mod2002020Key[]{Mod2002020Key.BN598 ,Mod2002020Key.BN565 ,Mod2002020Key.BN895 },"Total")
	,C14(new Mod2002020Key[]{Mod2002020Key.BN974 ,null                ,null                },"Donaciones del per\u00EDodo impositivo efectuadas a entidades sin fines de lucro (Ley 49/2002)")
	;
	 
    private String description;
    private Mod2002020Key[] keys;
    
	private Mod2002020BN565Key(Mod2002020Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}

	public Mod2002020Key[] getKeys() {
		return keys;
	}
	
	public String getDescription() {
		return description;
	}

}

