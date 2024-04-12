package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
// Donaciones para actividades prioritarias de mecenazgo y otras con derecho a deducción incrementada 
// (Este es el segundo de los dos bloques del desglose de la casilla 565)
public enum Mod2002023BN565_2Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002023Key[]{Mod2002023Key.BN903 ,Mod2002023Key.BN917 ,null                },"2013")                                                          
	,C02(new Mod2002023Key[]{Mod2002023Key.BN930 ,Mod2002023Key.BN931 ,Mod2002023Key.BN932 },"2014")                                                          
	,C03(new Mod2002023Key[]{Mod2002023Key.BN933 ,Mod2002023Key.BN934 ,Mod2002023Key.BN942 },"2015 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C04(new Mod2002023Key[]{Mod2002023Key.BN943 ,Mod2002023Key.BN944 ,Mod2002023Key.BN948 },"2015 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C05(new Mod2002023Key[]{Mod2002023Key.BN949 ,Mod2002023Key.BN950 ,Mod2002023Key.BN951 },"2016 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C06(new Mod2002023Key[]{Mod2002023Key.BN952 ,Mod2002023Key.BN953 ,Mod2002023Key.BN954 },"2016 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C07(new Mod2002023Key[]{Mod2002023Key.BN2472,Mod2002023Key.BN2473,Mod2002023Key.BN2474},"2017 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C08(new Mod2002023Key[]{Mod2002023Key.BN958 ,Mod2002023Key.BN959 ,Mod2002023Key.BN963 },"2017 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C09(new Mod2002023Key[]{Mod2002023Key.BN964 ,Mod2002023Key.BN965 ,Mod2002023Key.BN969 },"2018 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C10(new Mod2002023Key[]{Mod2002023Key.BN970 ,Mod2002023Key.BN971 ,Mod2002023Key.BN972 },"2018 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C11(new Mod2002023Key[]{Mod2002023Key.BN973 ,Mod2002023Key.BN975 ,Mod2002023Key.BN979 },"2019 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C12(new Mod2002023Key[]{Mod2002023Key.BN980 ,Mod2002023Key.BN981 ,Mod2002023Key.BN982 },"2019 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C13(new Mod2002023Key[]{Mod2002023Key.BN983 ,Mod2002023Key.BN984 ,Mod2002023Key.BN985 },"2020 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C14(new Mod2002023Key[]{Mod2002023Key.BN1000,Mod2002023Key.BN1001,Mod2002023Key.BN1007},"2020 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C15(new Mod2002023Key[]{Mod2002023Key.BN1008,Mod2002023Key.BN1017,Mod2002023Key.BN1024},"2021 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C16(new Mod2002023Key[]{Mod2002023Key.BN1025,Mod2002023Key.BN1035,Mod2002023Key.BN1036},"2021 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C17(new Mod2002023Key[]{Mod2002023Key.BN1061,Mod2002023Key.BN1062,Mod2002023Key.BN1072},"2022 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C18(new Mod2002023Key[]{Mod2002023Key.BN1073,Mod2002023Key.BN1074,Mod2002023Key.BN1078},"2022 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C19(new Mod2002023Key[]{Mod2002023Key.BN1329,Mod2002023Key.BN1372,Mod2002023Key.BN1373},"2023(*) Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C20(new Mod2002023Key[]{Mod2002023Key.BN1374,Mod2002023Key.BN1375,Mod2002023Key.BN1376},"2023(*) Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C21(new Mod2002023Key[]{Mod2002023Key.BN2694,Mod2002023Key.BN2695,Mod2002023Key.BN2696},"2023 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C22(new Mod2002023Key[]{Mod2002023Key.BN2697,Mod2002023Key.BN2698,Mod2002023Key.BN2700},"2023 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C23(new Mod2002023Key[]{Mod2002023Key.BN1701,Mod2002023Key.BN1702,Mod2002023Key.BN1703},"Subtotal donaciones 2013 a 2014")                 
	,C24(new Mod2002023Key[]{Mod2002023Key.BN1704,Mod2002023Key.BN1705,Mod2002023Key.BN1706},"Subtotal donaciones 2015 a 2023 sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C25(new Mod2002023Key[]{Mod2002023Key.BN1729,Mod2002023Key.BN2475,Mod2002023Key.BN2476},"Subtotal donaciones 2015 a 2023 con reiteraci\u00F3n de donaciones a una misma entidad")
	,C26(new Mod2002023Key[]{Mod2002023Key.BN1079,Mod2002023Key.BN1080,Mod2002023Key.BN1081},"Total")
	;
	 
    private String description;
    private Mod2002023Key[] keys;
    
	private Mod2002023BN565_2Key(Mod2002023Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}

	public Mod2002023Key[] getKeys() {
		return keys;
	}
	
	public String getDescription() {
		return description;
	}

}

