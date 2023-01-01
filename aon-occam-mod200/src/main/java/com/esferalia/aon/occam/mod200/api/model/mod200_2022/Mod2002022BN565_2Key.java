package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
// Donaciones para actividades prioritarias de mecenazgo y otras con derecho a deducción incrementada 
// (Este es el segundo de los dos bloques del desglose de la casilla 565)
public enum Mod2002022BN565_2Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 C01(new Mod2002022Key[]{Mod2002022Key.BN2471,Mod2002022Key.BN898 ,null                },"2011")                                                          
	,C02(new Mod2002022Key[]{Mod2002022Key.BN899 ,Mod2002022Key.BN901 ,Mod2002022Key.BN902 },"2012")                                                          
	,C03(new Mod2002022Key[]{Mod2002022Key.BN903 ,Mod2002022Key.BN917 ,Mod2002022Key.BN929 },"2013")                                                          
	,C04(new Mod2002022Key[]{Mod2002022Key.BN930 ,Mod2002022Key.BN931 ,Mod2002022Key.BN932 },"2014")                                                          
	,C05(new Mod2002022Key[]{Mod2002022Key.BN933 ,Mod2002022Key.BN934 ,Mod2002022Key.BN942 },"2015 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C06(new Mod2002022Key[]{Mod2002022Key.BN943 ,Mod2002022Key.BN944 ,Mod2002022Key.BN948 },"2015 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C07(new Mod2002022Key[]{Mod2002022Key.BN949 ,Mod2002022Key.BN950 ,Mod2002022Key.BN951 },"2016 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C08(new Mod2002022Key[]{Mod2002022Key.BN952 ,Mod2002022Key.BN953 ,Mod2002022Key.BN954 },"2016 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C09(new Mod2002022Key[]{Mod2002022Key.BN2472,Mod2002022Key.BN2473,Mod2002022Key.BN2474},"2017 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C10(new Mod2002022Key[]{Mod2002022Key.BN958 ,Mod2002022Key.BN959 ,Mod2002022Key.BN963 },"2017 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C11(new Mod2002022Key[]{Mod2002022Key.BN964 ,Mod2002022Key.BN965 ,Mod2002022Key.BN969 },"2018 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C12(new Mod2002022Key[]{Mod2002022Key.BN970 ,Mod2002022Key.BN971 ,Mod2002022Key.BN972 },"2018 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C13(new Mod2002022Key[]{Mod2002022Key.BN973 ,Mod2002022Key.BN975 ,Mod2002022Key.BN979 },"2019 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C14(new Mod2002022Key[]{Mod2002022Key.BN980 ,Mod2002022Key.BN981 ,Mod2002022Key.BN982 },"2019 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C15(new Mod2002022Key[]{Mod2002022Key.BN983 ,Mod2002022Key.BN984 ,Mod2002022Key.BN985 },"2020 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C16(new Mod2002022Key[]{Mod2002022Key.BN1000,Mod2002022Key.BN1001,Mod2002022Key.BN1007},"2020 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C17(new Mod2002022Key[]{Mod2002022Key.BN1008,Mod2002022Key.BN1017,Mod2002022Key.BN1024},"2021(*) Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C18(new Mod2002022Key[]{Mod2002022Key.BN1025,Mod2002022Key.BN1035,Mod2002022Key.BN1036},"2021(*) Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C19(new Mod2002022Key[]{Mod2002022Key.BN1061,Mod2002022Key.BN1062,Mod2002022Key.BN1072},"2021 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C20(new Mod2002022Key[]{Mod2002022Key.BN1073,Mod2002022Key.BN1074,Mod2002022Key.BN1078},"2021 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C21(new Mod2002022Key[]{Mod2002022Key.BN1701,Mod2002022Key.BN1702,Mod2002022Key.BN1703},"Subtotal donaciones 2011 a 2014")                 
	,C22(new Mod2002022Key[]{Mod2002022Key.BN1704,Mod2002022Key.BN1705,Mod2002022Key.BN1706},"Subtotal donaciones 2015 a 2021 sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C23(new Mod2002022Key[]{Mod2002022Key.BN1729,Mod2002022Key.BN2475,Mod2002022Key.BN2476},"Subtotal donaciones 2015 a 2021 con reiteraci\u00F3n de donaciones a una misma entidad")
	,C24(new Mod2002022Key[]{Mod2002022Key.BN1079,Mod2002022Key.BN1080,Mod2002022Key.BN1081},"Total")
	;
	 
    private String description;
    private Mod2002022Key[] keys;
    
	private Mod2002022BN565_2Key(Mod2002022Key[] keys, String description) {
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

