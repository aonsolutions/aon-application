package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
// Donaciones de carácter general 
// (Este es el primero de los dos bloques del desglose de la casilla 565)
public enum Mod2002022BN565_1Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	 
     C02(new Mod2002022Key[]{Mod2002022Key.BN904 ,Mod2002022Key.BN905 ,null                },"2012")
	,C03(new Mod2002022Key[]{Mod2002022Key.BN990 ,Mod2002022Key.BN991 ,Mod2002022Key.BN992 },"2013")
	,C04(new Mod2002022Key[]{Mod2002022Key.BN997 ,Mod2002022Key.BN998 ,Mod2002022Key.BN999 },"2014")
	,C05(new Mod2002022Key[]{Mod2002022Key.BN246 ,Mod2002022Key.BN247 ,Mod2002022Key.BN248 },"2015 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C06(new Mod2002022Key[]{Mod2002022Key.BN818 ,Mod2002022Key.BN819 ,Mod2002022Key.BN820 },"2015 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C07(new Mod2002022Key[]{Mod2002022Key.BN993 ,Mod2002022Key.BN994 ,Mod2002022Key.BN995 },"2016 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C08(new Mod2002022Key[]{Mod2002022Key.BN821 ,Mod2002022Key.BN833 ,Mod2002022Key.BN834 },"2016 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C09(new Mod2002022Key[]{Mod2002022Key.BN1434,Mod2002022Key.BN1435,Mod2002022Key.BN1436},"2017 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C10(new Mod2002022Key[]{Mod2002022Key.BN835 ,Mod2002022Key.BN836 ,Mod2002022Key.BN837 },"2017 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C11(new Mod2002022Key[]{Mod2002022Key.BN1718,Mod2002022Key.BN1719,Mod2002022Key.BN1720},"2018 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C12(new Mod2002022Key[]{Mod2002022Key.BN838 ,Mod2002022Key.BN839 ,Mod2002022Key.BN840 },"2018 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C13(new Mod2002022Key[]{Mod2002022Key.BN1950,Mod2002022Key.BN1951,Mod2002022Key.BN1952},"2019 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C14(new Mod2002022Key[]{Mod2002022Key.BN842 ,Mod2002022Key.BN844 ,Mod2002022Key.BN845 },"2019 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C15(new Mod2002022Key[]{Mod2002022Key.BN2227,Mod2002022Key.BN2228,Mod2002022Key.BN2229},"2020 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C16(new Mod2002022Key[]{Mod2002022Key.BN868 ,Mod2002022Key.BN869 ,Mod2002022Key.BN871 },"2020 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C17(new Mod2002022Key[]{Mod2002022Key.BN2380,Mod2002022Key.BN2381,Mod2002022Key.BN2382},"2021 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C18(new Mod2002022Key[]{Mod2002022Key.BN872 ,Mod2002022Key.BN873 ,Mod2002022Key.BN2498},"2021 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C19(new Mod2002022Key[]{Mod2002022Key.BN2499,Mod2002022Key.BN876 ,Mod2002022Key.BN890 },"2022(*) Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C20(new Mod2002022Key[]{Mod2002022Key.BN891 ,Mod2002022Key.BN892 ,Mod2002022Key.BN893 },"2022(*) Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C21(new Mod2002022Key[]{Mod2002022Key.BN1323,Mod2002022Key.BN1324,Mod2002022Key.BN1325},"2022 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C22(new Mod2002022Key[]{Mod2002022Key.BN1326,Mod2002022Key.BN1327,Mod2002022Key.BN1328},"2022 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C23(new Mod2002022Key[]{Mod2002022Key.BN1689,Mod2002022Key.BN1690,Mod2002022Key.BN1691},"Subtotal donaciones 2012 a 2014")                                                  
	,C24(new Mod2002022Key[]{Mod2002022Key.BN1692,Mod2002022Key.BN1693,Mod2002022Key.BN1694},"Subtotal donaciones 2015 a 2022 sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C25(new Mod2002022Key[]{Mod2002022Key.BN1695,Mod2002022Key.BN1696,Mod2002022Key.BN1697},"Subtotal donaciones 2015 a 2022 con reiteraci\u00F3n de donaciones a una misma entidad")
	,C26(new Mod2002022Key[]{Mod2002022Key.BN1698,Mod2002022Key.BN1699,Mod2002022Key.BN1700},"Total")
	;
	 
    private String description;
    private Mod2002022Key[] keys;
    
	private Mod2002022BN565_1Key(Mod2002022Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}

	public Mod2002022Key[] getKeys() {
		return keys;
	}
	
	public String getDescription() {
		return description;
	}

}

