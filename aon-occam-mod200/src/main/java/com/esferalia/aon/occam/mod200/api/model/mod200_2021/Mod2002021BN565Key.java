package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
public enum Mod2002021BN565Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	// FALTA - ESTE DESGLOSE NO TENGO MUY CLARO COMO VA A FUNCIONAR PORQUE EL QUE HABIA ANTES AHORA SON DOS BLOQUES Y VARIAS LINEAS SE DUPLICAN Y LLEVAN 3 SUBTOTALES
	
	// Deducciones de carácter general

	 C01(new Mod2002021Key[]{Mod2002021Key.BN201 ,Mod2002021Key.BN202 ,null                },"2011")
	,C02(new Mod2002021Key[]{Mod2002021Key.BN904 ,Mod2002021Key.BN905 ,Mod2002021Key.BN906 },"2012")
	,C03(new Mod2002021Key[]{Mod2002021Key.BN990 ,Mod2002021Key.BN991 ,Mod2002021Key.BN992 },"2013")
	,C04(new Mod2002021Key[]{Mod2002021Key.BN997 ,Mod2002021Key.BN998 ,Mod2002021Key.BN999 },"2014")
	,C05(new Mod2002021Key[]{Mod2002021Key.BN246 ,Mod2002021Key.BN247 ,Mod2002021Key.BN248 },"2015 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C06(new Mod2002021Key[]{Mod2002021Key.BN818 ,Mod2002021Key.BN819 ,Mod2002021Key.BN820 },"2015 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C07(new Mod2002021Key[]{Mod2002021Key.BN993 ,Mod2002021Key.BN994 ,Mod2002021Key.BN995 },"2016 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C08(new Mod2002021Key[]{Mod2002021Key.BN821 ,Mod2002021Key.BN833 ,Mod2002021Key.BN834 },"2016 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C09(new Mod2002021Key[]{Mod2002021Key.BN1434,Mod2002021Key.BN1435,Mod2002021Key.BN1436},"2017 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C10(new Mod2002021Key[]{Mod2002021Key.BN835 ,Mod2002021Key.BN836 ,Mod2002021Key.BN837 },"2017 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C11(new Mod2002021Key[]{Mod2002021Key.BN1718,Mod2002021Key.BN1719,Mod2002021Key.BN1720},"2018 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C12(new Mod2002021Key[]{Mod2002021Key.BN838 ,Mod2002021Key.BN839 ,Mod2002021Key.BN840 },"2018 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C13(new Mod2002021Key[]{Mod2002021Key.BN1950,Mod2002021Key.BN1951,Mod2002021Key.BN1952},"2019 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C14(new Mod2002021Key[]{Mod2002021Key.BN842 ,Mod2002021Key.BN844 ,Mod2002021Key.BN845 },"2019 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C15(new Mod2002021Key[]{Mod2002021Key.BN2227,Mod2002021Key.BN2228,Mod2002021Key.BN2229},"2020 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C16(new Mod2002021Key[]{Mod2002021Key.BN868 ,Mod2002021Key.BN869 ,Mod2002021Key.BN871 },"2020 Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C17(new Mod2002021Key[]{Mod2002021Key.BN2380,Mod2002021Key.BN2381,Mod2002021Key.BN2382},"2021(*) Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C18(new Mod2002021Key[]{Mod2002021Key.BN872 ,Mod2002021Key.BN873 ,Mod2002021Key.BN874 },"2021(*) Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C19(new Mod2002021Key[]{Mod2002021Key.BN875 ,Mod2002021Key.BN876 ,Mod2002021Key.BN890 },"2021 Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C20(new Mod2002021Key[]{Mod2002021Key.BN891 ,Mod2002021Key.BN892 ,Mod2002021Key.BN893 },"2021 Con reiteraci\u00F3n de donaciones a una misma entidad")
//	XXXXX XXXXX XXXXX Subtotal donaciones 2011 a 2014 (AUN NO TIENEN CLAVES ASIGNADAS)
//	XXXXX XXXXX XXXXX Subtotal donaciones 2015 a 2021 sin reiteración de donaciones a una misma entidad (AUN NO TIENEN CLAVES ASIGNADAS)
//	XXXXX XXXXX XXXXX Subtotal donaciones 2015 a 2021 con reiteración de donaciones a una misma entidad (AUN NO TIENEN CLAVES ASIGNADAS)
	,C21(new Mod2002021Key[]{Mod2002021Key.BN598 ,Mod2002021Key.BN565 ,Mod2002021Key.BN895 },"Total")
	,C22(new Mod2002021Key[]{Mod2002021Key.BN974 ,null                ,null                },"Donaciones del per\u00EDodo impositivo efectuadas a entidades sin fines de lucro (Ley 49/2002)")
	
	// Donaciones para actividades prioritarias de mecenazgo y otras con derecho a deducción incrementada
	
	,C51(new Mod2002021Key[]{Mod2002021Key.BN897 ,Mod2002021Key.BN898 ,null                },"2011")                                                          
	,C52(new Mod2002021Key[]{Mod2002021Key.BN899 ,Mod2002021Key.BN901 ,Mod2002021Key.BN902 },"2012")                                                          
	,C53(new Mod2002021Key[]{Mod2002021Key.BN903 ,Mod2002021Key.BN917 ,Mod2002021Key.BN929 },"2013")                                                          
	,C54(new Mod2002021Key[]{Mod2002021Key.BN930 ,Mod2002021Key.BN931 ,Mod2002021Key.BN932 },"2014")                                                          
	,C55(new Mod2002021Key[]{Mod2002021Key.BN933 ,Mod2002021Key.BN934 ,Mod2002021Key.BN942 },"2015 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C56(new Mod2002021Key[]{Mod2002021Key.BN943 ,Mod2002021Key.BN944 ,Mod2002021Key.BN948 },"2015 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C57(new Mod2002021Key[]{Mod2002021Key.BN949 ,Mod2002021Key.BN950 ,Mod2002021Key.BN951 },"2016 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C58(new Mod2002021Key[]{Mod2002021Key.BN952 ,Mod2002021Key.BN953 ,Mod2002021Key.BN954 },"2016 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C59(new Mod2002021Key[]{Mod2002021Key.BN955 ,Mod2002021Key.BN956 ,Mod2002021Key.BN957 },"2017 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C60(new Mod2002021Key[]{Mod2002021Key.BN958 ,Mod2002021Key.BN959 ,Mod2002021Key.BN963 },"2017 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C61(new Mod2002021Key[]{Mod2002021Key.BN964 ,Mod2002021Key.BN965 ,Mod2002021Key.BN969 },"2018 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C62(new Mod2002021Key[]{Mod2002021Key.BN970 ,Mod2002021Key.BN971 ,Mod2002021Key.BN972 },"2018 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C63(new Mod2002021Key[]{Mod2002021Key.BN973 ,Mod2002021Key.BN975 ,Mod2002021Key.BN979 },"2019 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C64(new Mod2002021Key[]{Mod2002021Key.BN980 ,Mod2002021Key.BN981 ,Mod2002021Key.BN982 },"2019 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C65(new Mod2002021Key[]{Mod2002021Key.BN983 ,Mod2002021Key.BN984 ,Mod2002021Key.BN985 },"2020 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C66(new Mod2002021Key[]{Mod2002021Key.BN1000,Mod2002021Key.BN1001,Mod2002021Key.BN1007},"2020 Con reiteraci\u00F3n de donaciones a una misma entidad")   
	,C67(new Mod2002021Key[]{Mod2002021Key.BN1008,Mod2002021Key.BN1017,Mod2002021Key.BN1024},"2021(*) Sin reiteraci\u00F3n de donaciones a una misma entidad")
	,C68(new Mod2002021Key[]{Mod2002021Key.BN1025,Mod2002021Key.BN1035,Mod2002021Key.BN1036},"2021(*) Con reiteraci\u00F3n de donaciones a una misma entidad")
	,C69(new Mod2002021Key[]{Mod2002021Key.BN1061,Mod2002021Key.BN1062,Mod2002021Key.BN1072},"2021 Sin reiteraci\u00F3n de donaciones a una misma entidad")   
	,C70(new Mod2002021Key[]{Mod2002021Key.BN1073,Mod2002021Key.BN1074,Mod2002021Key.BN1078},"2021 Con reiteraci\u00F3n de donaciones a una misma entidad")   
//	XXXXX XXXXX XXXXX Subtotal donaciones 2011 a 2014 (AUN NO TIENEN CLAVES ASIGNADAS)                                                  
//	XXXXX XXXXX XXXXX Subtotal donaciones 2015 a 2021 sin reiteración de donaciones a una misma entidad (AUN NO TIENEN CLAVES ASIGNADAS)
//	XXXXX XXXXX XXXXX Subtotal donaciones 2015 a 2021 con reiteración de donaciones a una misma entidad (AUN NO TIENEN CLAVES ASIGNADAS)
	,C71(new Mod2002021Key[]{Mod2002021Key.BN1079,Mod2002021Key.BN1080,Mod2002021Key.BN1081},"Total")
	;
	 
    private String description;
    private Mod2002021Key[] keys;
    
	private Mod2002021BN565Key(Mod2002021Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}

	public Mod2002021Key[] getKeys() {
		return keys;
	}
	
	public String getDescription() {
		return description;
	}

}

