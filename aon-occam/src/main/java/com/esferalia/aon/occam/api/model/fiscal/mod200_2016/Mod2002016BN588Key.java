package com.esferalia.aon.occam.api.model.fiscal.mod200_2016;

import java.io.Serializable;

// Desglose Casilla 588
// Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI y DT 24ª.3 LIS)  
public enum Mod2002016BN588Key implements Serializable, IMod200KeysProvider {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF	
	
	  C01(new Mod2002016Key[]{Mod2002016Key.BN768 ,Mod2002016Key.BN769 ,null },"1998: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C02(new Mod2002016Key[]{Mod2002016Key.BN774 ,Mod2002016Key.BN775 ,Mod2002016Key.BN776 },"1999: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C03(new Mod2002016Key[]{Mod2002016Key.BN780 ,Mod2002016Key.BN781 ,Mod2002016Key.BN782 },"2000: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C04(new Mod2002016Key[]{Mod2002016Key.BN786 ,Mod2002016Key.BN787 ,Mod2002016Key.BN788 },"2001: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C05(new Mod2002016Key[]{Mod2002016Key.BN766 ,Mod2002016Key.BN767 ,Mod2002016Key.BN833 },"2002: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C06(new Mod2002016Key[]{Mod2002016Key.BN198 ,Mod2002016Key.BN896 ,Mod2002016Key.BN897 },"2003: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C07(new Mod2002016Key[]{Mod2002016Key.BN288 ,Mod2002016Key.BN289 ,Mod2002016Key.BN290 },"2004: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C08(new Mod2002016Key[]{Mod2002016Key.BN466 ,Mod2002016Key.BN467 ,Mod2002016Key.BN468 },"2005: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C09(new Mod2002016Key[]{Mod2002016Key.BN061 ,Mod2002016Key.BN498 ,Mod2002016Key.BN586 },"2006: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C10(new Mod2002016Key[]{Mod2002016Key.BN472 ,Mod2002016Key.BN473 ,Mod2002016Key.BN478 },"2007: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C11(new Mod2002016Key[]{Mod2002016Key.BN180 ,Mod2002016Key.BN181 ,Mod2002016Key.BN182 },"2008: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C12(new Mod2002016Key[]{Mod2002016Key.BN531 ,Mod2002016Key.BN532 ,Mod2002016Key.BN533 },"2009: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C13(new Mod2002016Key[]{Mod2002016Key.BN945 ,Mod2002016Key.BN946 ,Mod2002016Key.BN947 },"2010: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004")
	 ,C14(new Mod2002016Key[]{Mod2002016Key.BN960 ,Mod2002016Key.BN961 ,Mod2002016Key.BN962 },"2011: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004")
	 ,C15(new Mod2002016Key[]{Mod2002016Key.BN183 ,Mod2002016Key.BN185 ,Mod2002016Key.BN186 },"2012: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004")
	 ,C16(new Mod2002016Key[]{Mod2002016Key.BN966 ,Mod2002016Key.BN967 ,Mod2002016Key.BN968 },"2013: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004 (excepto I+D+i)")
	 ,C17(new Mod2002016Key[]{Mod2002016Key.BN457 ,Mod2002016Key.BN458 ,Mod2002016Key.BN459 },"2013: Investigaci\u00F3n y desarrollo (CT)")
	 ,C18(new Mod2002016Key[]{Mod2002016Key.BN460 ,Mod2002016Key.BN461 ,Mod2002016Key.BN462 },"2013: Innovaci\u00F3n tecnol\u00F3gica (IT)")
	 ,C19(new Mod2002016Key[]{Mod2002016Key.BN1063,Mod2002016Key.BN1064,Mod2002016Key.BN1065},"2014: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004 (excepto I+D+i)")
	 ,C20(new Mod2002016Key[]{Mod2002016Key.BN1066,Mod2002016Key.BN1067,Mod2002016Key.BN1068},"2014: Investigaci\u00F3n y desarrollo (CT)")
	 ,C21(new Mod2002016Key[]{Mod2002016Key.BN1069,Mod2002016Key.BN1070,Mod2002016Key.BN1071},"2014: Innovaci\u00F3n tecnol\u00F3gica (IT)")
	 ,C22(new Mod2002016Key[]{Mod2002016Key.BN813 ,Mod2002016Key.BN814 ,Mod2002016Key.BN815 },"2015: Suma deducciones Cap. IV Tit. VI Ley 43/95, RDL 4/2004 y LIS (excepto I+D+i)") 
	 ,C23(new Mod2002016Key[]{Mod2002016Key.BN986 ,Mod2002016Key.BN810 ,Mod2002016Key.BN507 },"2015: Investigaci\u00F3n y desarrollo (CT)")
     ,C24(new Mod2002016Key[]{Mod2002016Key.BN557 ,Mod2002016Key.BN591 ,Mod2002016Key.BN594 },"2015: Innovaci\u00F3n tecnol\u00F3gica (IT)")
     ,C25(new Mod2002016Key[]{Mod2002016Key.BN1360 ,Mod2002016Key.BN1361 ,Mod2002016Key.BN1362 },"2016(*): Suma deducciones Cap. IV Tit. VI Ley 43/95, RDL 4/2004 y LIS (excepto I+D+i)")
     ,C26(new Mod2002016Key[]{Mod2002016Key.BN1363 ,Mod2002016Key.BN1364 ,Mod2002016Key.BN1365 },"2016(*): Investigaci\u00F3n y desarrollo (CT)")
     ,C27(new Mod2002016Key[]{Mod2002016Key.BN1366 ,Mod2002016Key.BN1367 ,Mod2002016Key.BN1368 },"2016(*): Innovaci\u00F3n tecnol\u00F3gica (IT)")
     ,C28(new Mod2002016Key[]{Mod2002016Key.BN798 ,Mod2002016Key.BN799 ,Mod2002016Key.BN800 },"2016: Investigaci\u00F3n y desarrollo (CT)")
     ,C29(new Mod2002016Key[]{Mod2002016Key.BN096 ,Mod2002016Key.BN698 ,Mod2002016Key.BN713 },"2016: Innovaci\u00F3n tecnol\u00F3gica (IT)")
     ,C30(new Mod2002016Key[]{Mod2002016Key.BN807 ,Mod2002016Key.BN808 ,Mod2002016Key.BN809 },"2016: Producciones cinematogr\u00E1ficas espa\u00F1olas (PC)")
     ,C31(new Mod2002016Key[]{Mod2002016Key.BN1075,Mod2002016Key.BN1076,Mod2002016Key.BN1077},"2016: Espect\u00E1culos en vivo de artes esc\u00E9nicas y musicales (EV)")
     ,C32(new Mod2002016Key[]{Mod2002016Key.BN963 ,Mod2002016Key.BN964 ,Mod2002016Key.BN965 },"2016: Creaci\u00F3n empleo contrataci\u00F3n menores de 30 (CEM-1) (art. 37 LIS)")
     ,C33(new Mod2002016Key[]{Mod2002016Key.BN931 ,Mod2002016Key.BN502 ,Mod2002016Key.BN751 },"2016: Creaci\u00F3n empleo contrataci\u00F3n desempleados con prest. desempleo (CEM-2) (art. 37 LIS)")
     ,C34(new Mod2002016Key[]{Mod2002016Key.BN795 ,Mod2002016Key.BN796 ,Mod2002016Key.BN797 },"2016: Deducci\u00F3n creaci\u00F3n empleo trabaj. con discapacidad (CE)")
     ,C35(new Mod2002016Key[]{Mod2002016Key.BN549 ,Mod2002016Key.BN888 ,Mod2002016Key.BN889 },"2016: Deducci\u00F3n por inversi\u00F3n de beneficios (IB)")
     ,C36(new Mod2002016Key[]{Mod2002016Key.BN1369 ,Mod2002016Key.BN1370 ,Mod2002016Key.BN1371 },"2016: Gastos e inversiones de sociedades forestales (SF)")     
     ,C37(new Mod2002016Key[]{Mod2002016Key.BN078 ,Mod2002016Key.BN079 ,Mod2002016Key.BN080 },"2016: Plan Director para la recuperaci\u00F3n del Patrimonio Cultural de Lorca (PL)")
     ,C38(new Mod2002016Key[]{Mod2002016Key.BN085 ,Mod2002016Key.BN086 ,Mod2002016Key.BN087 },"2016: Universiada de Invierno de Granada 2015 (UG)")
     ,C39(new Mod2002016Key[]{Mod2002016Key.BN204 ,Mod2002016Key.BN205 ,Mod2002016Key.BN206 },"2016: Programa de preparaci\u00F3n de los deportistas espa\u00F1oles para los juegos de R\u00EDo de Janeiro 2016 (R16)")
     ,C40(new Mod2002016Key[]{Mod2002016Key.BN007 ,Mod2002016Key.BN012 ,Mod2002016Key.BN016 },"2016: Donostia/San Sebasti\u00E1n, Capital Europea de la Cultura 2016 (D16)")
     ,C41(new Mod2002016Key[]{Mod2002016Key.BN199 ,Mod2002016Key.BN292 ,Mod2002016Key.BN293 },"2016: Expo Mil\u00E1n 2015 (EM)")
     ,C42(new Mod2002016Key[]{Mod2002016Key.BN419 ,Mod2002016Key.BN422 ,Mod2002016Key.BN423 },"2016: Madrid Horse Week (MH)")
     ,C43(new Mod2002016Key[]{Mod2002016Key.BN432 ,Mod2002016Key.BN433 ,Mod2002016Key.BN434 },"2016: IV Centenario de la segunda parte de El Quijote (Q)")
     ,C44(new Mod2002016Key[]{Mod2002016Key.BN435 ,Mod2002016Key.BN436 ,Mod2002016Key.BN437 },"2016: World Challenge LFP/85\u00BA Aniversario de la Liga (WCH)")
     ,C45(new Mod2002016Key[]{Mod2002016Key.BN438 ,Mod2002016Key.BN439 ,Mod2002016Key.BN440 },"2016: Juegos del Mediterr\u00E1neo de 2017 (M17)")
     ,C46(new Mod2002016Key[]{Mod2002016Key.BN1081,Mod2002016Key.BN1082,Mod2002016Key.BN1083},"2016: 200 Aniversario del Teatro Real y el Vig\u00E9simo Aniversario de la reapertura del Teatro Real (TR)")
     ,C47(new Mod2002016Key[]{Mod2002016Key.BN1084,Mod2002016Key.BN1085,Mod2002016Key.BN1086},"2016: IV Centenario de la muerte de Miguel de Cervantes (MC)")
     ,C48(new Mod2002016Key[]{Mod2002016Key.BN1087,Mod2002016Key.BN1088,Mod2002016Key.BN1089},"2016: VIII Centenario de la Universidad de Salamanca (US)")
     ,C49(new Mod2002016Key[]{Mod2002016Key.BN1090,Mod2002016Key.BN1091,Mod2002016Key.BN1092},"2016: Programa Jerez, Capital mundial del Motociclismo (J)")
     ,C50(new Mod2002016Key[]{Mod2002016Key.BN1093,Mod2002016Key.BN1094,Mod2002016Key.BN1095},"2016: Cantabria 2017, Li\u00E9bana A\u00F1o Jubilar (C17)")
     ,C51(new Mod2002016Key[]{Mod2002016Key.BN1096,Mod2002016Key.BN1097,Mod2002016Key.BN1098},"2016: Programa Universo Mujer (UM)")
     ,C52(new Mod2002016Key[]{Mod2002016Key.BN1099,Mod2002016Key.BN1100,Mod2002016Key.BN1101},"2016: 60 Aniversario de la Fundaci\u00F3n de la Escuela de Organizaci\u00F3n Industrial (EOI)")
     ,C53(new Mod2002016Key[]{Mod2002016Key.BN1102,Mod2002016Key.BN1103,Mod2002016Key.BN1104},"2016: Encuentro Mundial en Las Estrellas 2017 (EME)")
     ,C54(new Mod2002016Key[]{Mod2002016Key.BN1105,Mod2002016Key.BN1106,Mod2002016Key.BN1107},"2016: Barcelona Mobile World Capital (MW)")
     ,C55(new Mod2002016Key[]{Mod2002016Key.BN1114,Mod2002016Key.BN1115,Mod2002016Key.BN1116},"2016: Barcelona Equestrian Challenge (BE)")
     ,C56(new Mod2002016Key[]{Mod2002016Key.BN1117,Mod2002016Key.BN1118,Mod2002016Key.BN1119},"2016: Women's Hockey World League Round 3 Events 2015 (WH)")
     ,C57(new Mod2002016Key[]{Mod2002016Key.BN1372,Mod2002016Key.BN1373,Mod2002016Key.BN1374},"2016: II Centenario del Museo Nacional del Prado (MP)")
     ,C58(new Mod2002016Key[]{Mod2002016Key.BN1375,Mod2002016Key.BN1376,Mod2002016Key.BN1377},"2016: 20 Aniversario de la Reapertura del Gran Teatro del Liceo de Barcelona y el bicentenario de la creaci\u00F3n de la Societat d'Accionistes (LB)")
     ,C59(new Mod2002016Key[]{Mod2002016Key.BN1378,Mod2002016Key.BN1379,Mod2002016Key.BN1380},"2016: Foro Iberoamericano de Ciudades (FIC)")
     ,C60(new Mod2002016Key[]{Mod2002016Key.BN1381,Mod2002016Key.BN1382,Mod2002016Key.BN1383},"2016: Plan Decenio M\u00E1laga Cultura Innovadora 2025 (MCI)")
     ,C61(new Mod2002016Key[]{Mod2002016Key.BN1384,Mod2002016Key.BN1385,Mod2002016Key.BN1386},"2016: XX Aniversario de la Declaraci\u00F3n de Cuenca como Ciudad Patrimonio de la Humanidad (CPH)")
     ,C62(new Mod2002016Key[]{Mod2002016Key.BN1387,Mod2002016Key.BN1388,Mod2002016Key.BN1389},"2016: Campeonatos del Mundo FIS de Freestyle y Snowboard Sierra Nevada 2017 (SN17)")     
 	 ,C63(new Mod2002016Key[]{Mod2002016Key.BN1390,Mod2002016Key.BN1391,Mod2002016Key.BN1392},"2016: Vig\u00E9simo quinto aniversario del Museo Thyssen-Bornemisza (MT)")
     ,C64(new Mod2002016Key[]{Mod2002016Key.BN1393,Mod2002016Key.BN1394,Mod2002016Key.BN1395},"2016: Campeonato de Europa de Waterpolo Barcelona 2018 (WB18)")
	 ,C65(new Mod2002016Key[]{Mod2002016Key.BN1396,Mod2002016Key.BN1397,Mod2002016Key.BN1398},"2016: Centenario del nacimiento de Camilo Jos\u00E9 Cela (CJC)")
	 ,C66(new Mod2002016Key[]{Mod2002016Key.BN1399,Mod2002016Key.BN1400,Mod2002016Key.BN1401},"2016: 2017: A\u00F1o de la retina en Espa\u00F1a (R)")
	 ,C67(new Mod2002016Key[]{Mod2002016Key.BN1402,Mod2002016Key.BN1403,Mod2002016Key.BN1404},"2016: Caravaca de la Cruz 2017. A\u00F1o Jubilar (CC17)")
	 ,C68(new Mod2002016Key[]{Mod2002016Key.BN1405,Mod2002016Key.BN1406,Mod2002016Key.BN1407},"2016: Plan 2020 de apoyo al Deporte de Base (P20)")
	 ,C69(new Mod2002016Key[]{Mod2002016Key.BN1408,Mod2002016Key.BN1409,Mod2002016Key.BN1410},"2016: 2150 aniversario de Numancia (N)")
	 ,C70(new Mod2002016Key[]{Mod2002016Key.BN1411,Mod2002016Key.BN1412,Mod2002016Key.BN1413},"2016: V Centenario del fallecimiento de Fernando el Cat\u00F3lico (FC)")
	 ,C71(new Mod2002016Key[]{Mod2002016Key.BN1414,Mod2002016Key.BN1415,Mod2002016Key.BN1416},"2016: 525 Aniversario del Descubrimiento de Am\u00E9rica en Palos de la Frontera (Huelva) (DA)")
	 ,C72(new Mod2002016Key[]{Mod2002016Key.BN1417,Mod2002016Key.BN1418,Mod2002016Key.BN1419},"2016: Prevenci\u00F3n de la Obesidad. Aligera tu vida (PO)")
	 ,C73(new Mod2002016Key[]{Mod2002016Key.BN1420,Mod2002016Key.BN1421,Mod2002016Key.BN1422},"2016: 75 Aniversario de Willian Martin; El legado ingl\u00E9s (WM)")
	 ,C74(new Mod2002016Key[]{Mod2002016Key.BN1423,Mod2002016Key.BN1424,Mod2002016Key.BN1425},"2016: Salida de la vuelta al mundo a vela Alicante 2017 (A17)")
	 ,C75(new Mod2002016Key[]{Mod2002016Key.BN634 ,Mod2002016Key.BN635 ,Mod2002016Key.BN636 },"Total deducciones relativas a programas de apoyo acontecimientos de excepcional inter\u00E9s p\u00FAblico")
	 ,C76(new Mod2002016Key[]{Mod2002016Key.BN828 ,Mod2002016Key.BN829 ,Mod2002016Key.BN830 },"2016: Diferimiento deducciones Cap. IV T\u00EDt. VI Ley 43/95, RDL 4/2004 y LIS")	 
	 ,C77(new Mod2002016Key[]{Mod2002016Key.BN831 ,null /* BN588 */    ,Mod2002016Key.BN832 },"Total")
	; 

    private String description;
    
    private Mod2002016Key[] keys;
    
	private Mod2002016BN588Key(Mod2002016Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002016Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
	
	public static void main(String[] args) {
		
		for (Mod2002016BN588Key k : Mod2002016BN588Key.values()) {
			
			System.out.println(k.getKeys()[0]+" "+k.getKeys()[1]+" "+k.getKeys()[2]+" - "+k.getDescription());		
			
		}
		
	}
	
	
}
