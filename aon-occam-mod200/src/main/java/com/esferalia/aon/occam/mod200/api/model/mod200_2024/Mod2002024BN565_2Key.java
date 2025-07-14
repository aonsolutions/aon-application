package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
// Donaciones para actividades prioritarias de mecenazgo y otras con derecho a deducción incrementada 
// (Este es el segundo de los dos bloques del desglose de la casilla 565)
public enum Mod2002024BN565_2Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002024Key[]{Mod2002024Key.BN930 ,Mod2002024Key.BN931 ,null                },"2014")                                                          
	,C02(new Mod2002024Key[]{Mod2002024Key.BN933 ,Mod2002024Key.BN934 ,Mod2002024Key.BN942 },"2015 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C03(new Mod2002024Key[]{Mod2002024Key.BN943 ,Mod2002024Key.BN944 ,Mod2002024Key.BN948 },"2015 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C04(new Mod2002024Key[]{Mod2002024Key.BN949 ,Mod2002024Key.BN950 ,Mod2002024Key.BN951 },"2016 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C05(new Mod2002024Key[]{Mod2002024Key.BN952 ,Mod2002024Key.BN953 ,Mod2002024Key.BN954 },"2016 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C06(new Mod2002024Key[]{Mod2002024Key.BN2472,Mod2002024Key.BN2473,Mod2002024Key.BN2474},"2017 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C07(new Mod2002024Key[]{Mod2002024Key.BN958 ,Mod2002024Key.BN959 ,Mod2002024Key.BN963 },"2017 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C08(new Mod2002024Key[]{Mod2002024Key.BN964 ,Mod2002024Key.BN965 ,Mod2002024Key.BN969 },"2018 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C09(new Mod2002024Key[]{Mod2002024Key.BN970 ,Mod2002024Key.BN971 ,Mod2002024Key.BN972 },"2018 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C10(new Mod2002024Key[]{Mod2002024Key.BN973 ,Mod2002024Key.BN975 ,Mod2002024Key.BN979 },"2019 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C11(new Mod2002024Key[]{Mod2002024Key.BN980 ,Mod2002024Key.BN981 ,Mod2002024Key.BN982 },"2019 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C12(new Mod2002024Key[]{Mod2002024Key.BN983 ,Mod2002024Key.BN984 ,Mod2002024Key.BN985 },"2020 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C13(new Mod2002024Key[]{Mod2002024Key.BN1000,Mod2002024Key.BN1001,Mod2002024Key.BN1007},"2020 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C14(new Mod2002024Key[]{Mod2002024Key.BN1008,Mod2002024Key.BN1017,Mod2002024Key.BN1024},"2021 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C15(new Mod2002024Key[]{Mod2002024Key.BN1025,Mod2002024Key.BN1035,Mod2002024Key.BN1036},"2021 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C16(new Mod2002024Key[]{Mod2002024Key.BN1061,Mod2002024Key.BN1062,Mod2002024Key.BN1072},"2022 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C17(new Mod2002024Key[]{Mod2002024Key.BN1073,Mod2002024Key.BN1074,Mod2002024Key.BN1078},"2022 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C18(new Mod2002024Key[]{Mod2002024Key.BN1329,Mod2002024Key.BN1372,Mod2002024Key.BN1373},"2023 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C19(new Mod2002024Key[]{Mod2002024Key.BN1374,Mod2002024Key.BN1375,Mod2002024Key.BN1376},"2023 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C20(new Mod2002024Key[]{Mod2002024Key.BN2694,Mod2002024Key.BN2695,Mod2002024Key.BN2696},"2024(*) Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C21(new Mod2002024Key[]{Mod2002024Key.BN2697,Mod2002024Key.BN2698,Mod2002024Key.BN2700},"2024(*) Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C22(new Mod2002024Key[]{Mod2002024Key.BN424 ,Mod2002024Key.BN430 ,Mod2002024Key.BN431 },"2024 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C23(new Mod2002024Key[]{Mod2002024Key.BN432 ,Mod2002024Key.BN439 ,Mod2002024Key.BN440 },"2024 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C24(new Mod2002024Key[]{Mod2002024Key.BN1701,Mod2002024Key.BN1702,null                },"Subtotal donaciones 2014")                 
	,C25(new Mod2002024Key[]{Mod2002024Key.BN1704,Mod2002024Key.BN1705,Mod2002024Key.BN1706},"Subtotal donaciones 2015 a 2024 sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C26(new Mod2002024Key[]{Mod2002024Key.BN1729,Mod2002024Key.BN2475,Mod2002024Key.BN2476},"Subtotal donaciones 2015 a 2024 con reiteraci\u00F3n de donaciones a una misma entidad")
	,C27(new Mod2002024Key[]{Mod2002024Key.BN1079,Mod2002024Key.BN1080,Mod2002024Key.BN1081},"Total")
	;  
	   
    private String description;
    private Mod2002024Key[] keys;
    
	private Mod2002024BN565_2Key(Mod2002024Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}

	public Mod2002024Key[] getKeys() {
		return keys;
	}
	
	public String getDescription() {
		return description;
	}

}

