package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Deducciones para incentivar det. actividades (cap. IV tit. VI Ley 43/95, RDL 4/2004 y LIS y art. 27bis Ley 19/1994) 
public enum Mod2002015BN588Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	
	  C01(new Mod2002015Key[]{Mod2002015Key.BN1061,Mod2002015Key.BN1062,null                },"1997: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C02(new Mod2002015Key[]{Mod2002015Key.BN768 ,Mod2002015Key.BN769 ,Mod2002015Key.BN770 },"1998: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C03(new Mod2002015Key[]{Mod2002015Key.BN774 ,Mod2002015Key.BN775 ,Mod2002015Key.BN776 },"1999: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C04(new Mod2002015Key[]{Mod2002015Key.BN780 ,Mod2002015Key.BN781 ,Mod2002015Key.BN782 },"2000: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C05(new Mod2002015Key[]{Mod2002015Key.BN786 ,Mod2002015Key.BN787 ,Mod2002015Key.BN788 },"2001: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C06(new Mod2002015Key[]{Mod2002015Key.BN766 ,Mod2002015Key.BN767 ,Mod2002015Key.BN833 },"2002: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C07(new Mod2002015Key[]{Mod2002015Key.BN198 ,Mod2002015Key.BN896 ,Mod2002015Key.BN897 },"2003: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C08(new Mod2002015Key[]{Mod2002015Key.BN288 ,Mod2002015Key.BN289 ,Mod2002015Key.BN290 },"2004: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C09(new Mod2002015Key[]{Mod2002015Key.BN466 ,Mod2002015Key.BN467 ,Mod2002015Key.BN468 },"2005: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C10(new Mod2002015Key[]{Mod2002015Key.BN061 ,Mod2002015Key.BN498 ,Mod2002015Key.BN586 },"2006: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C11(new Mod2002015Key[]{Mod2002015Key.BN472 ,Mod2002015Key.BN473 ,Mod2002015Key.BN478 },"2007: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C12(new Mod2002015Key[]{Mod2002015Key.BN180 ,Mod2002015Key.BN181 ,Mod2002015Key.BN182 },"2008: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C13(new Mod2002015Key[]{Mod2002015Key.BN531 ,Mod2002015Key.BN532 ,Mod2002015Key.BN533 },"2009: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C14(new Mod2002015Key[]{Mod2002015Key.BN945 ,Mod2002015Key.BN946 ,Mod2002015Key.BN947 },"2010: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004")
	 ,C15(new Mod2002015Key[]{Mod2002015Key.BN960 ,Mod2002015Key.BN961 ,Mod2002015Key.BN962 },"2011: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004")
	 ,C16(new Mod2002015Key[]{Mod2002015Key.BN183 ,Mod2002015Key.BN185 ,Mod2002015Key.BN186 },"2012: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004")
	 ,C17(new Mod2002015Key[]{Mod2002015Key.BN966 ,Mod2002015Key.BN967 ,Mod2002015Key.BN968 },"2013: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004 (excepto I+D+i)")
	 ,C18(new Mod2002015Key[]{Mod2002015Key.BN457 ,Mod2002015Key.BN458 ,Mod2002015Key.BN459 },"2013: Investigaci\u00F3n y desarrollo (CT)")
	 ,C19(new Mod2002015Key[]{Mod2002015Key.BN460 ,Mod2002015Key.BN461 ,Mod2002015Key.BN462 },"2013: Innovaci\u00F3n tecnol\u00F3gica (IT)")
	 ,C20(new Mod2002015Key[]{Mod2002015Key.BN1063,Mod2002015Key.BN1064,Mod2002015Key.BN1065},"2014: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004 (excepto I+D+i)(*)")
	 ,C21(new Mod2002015Key[]{Mod2002015Key.BN1066,Mod2002015Key.BN1067,Mod2002015Key.BN1068},"2014: Investigaci\u00F3n y desarrollo (CT)")
	 ,C22(new Mod2002015Key[]{Mod2002015Key.BN1069,Mod2002015Key.BN1070,Mod2002015Key.BN1071},"2014: Innovaci\u00F3n tecnol\u00F3gica (IT)")
	 ,C23(new Mod2002015Key[]{Mod2002015Key.BN813 ,Mod2002015Key.BN814 ,Mod2002015Key.BN815 },"2015(*): Suma deducciones Cap. IV Tit. VI Ley 43/95, RDL 4/2004 y LIS (excepto I+D+i)(*)") 
	 ,C24(new Mod2002015Key[]{Mod2002015Key.BN986 ,Mod2002015Key.BN810 ,Mod2002015Key.BN507 },"2015(*): Investigaci\u00F3n y desarrollo (CT)")
     ,C25(new Mod2002015Key[]{Mod2002015Key.BN557 ,Mod2002015Key.BN591 ,Mod2002015Key.BN594 },"2015(*): Innovaci\u00F3n tecnol\u00F3gica (IT)")
     ,C26(new Mod2002015Key[]{Mod2002015Key.BN795 ,Mod2002015Key.BN796 ,Mod2002015Key.BN797 },"2015: Deducci\u00F3n creaci\u00F3n empleo trabaj. con discapacidad (CE)")
     ,C27(new Mod2002015Key[]{Mod2002015Key.BN798 ,Mod2002015Key.BN799 ,Mod2002015Key.BN800 },"2015: Investigaci\u00F3n y desarrollo (CT)")
     ,C28(new Mod2002015Key[]{Mod2002015Key.BN096 ,Mod2002015Key.BN698 ,Mod2002015Key.BN713 },"2015: Innovaci\u00F3n tecnol\u00F3gica (IT)")
     ,C29(new Mod2002015Key[]{Mod2002015Key.BN549 ,Mod2002015Key.BN888 ,Mod2002015Key.BN889 },"2015: Deducci\u00F3n por inversi\u00F3n de beneficios (IB)")
     ,C30(new Mod2002015Key[]{Mod2002015Key.BN807 ,Mod2002015Key.BN808 ,Mod2002015Key.BN809 },"2015: Producciones cinematogr\u00E1ficas espa\u00F1olas (PC)")
     ,C31(new Mod2002015Key[]{Mod2002015Key.BN1350,Mod2002015Key.BN1351,Mod2002015Key.BN1352},"2015: Producciones cinematogr\u00E1ficas extranjeras (PE)")
     ,C32(new Mod2002015Key[]{Mod2002015Key.BN1075,Mod2002015Key.BN1076,Mod2002015Key.BN1077},"2015: Espect\u00E1culos en vivo de artes esc\u00E9nicas y musicales (EV)")
     ,C33(new Mod2002015Key[]{Mod2002015Key.BN963 ,Mod2002015Key.BN964 ,Mod2002015Key.BN965 },"2015: Creaci\u00F3n empleo contrataci\u00F3n menores de 30 (CEM-1) (art. 37 LIS)")
     ,C34(new Mod2002015Key[]{Mod2002015Key.BN931 ,Mod2002015Key.BN502 ,Mod2002015Key.BN751 },"2015: Creaci\u00F3n empleo contrataci\u00F3n desempleados con prest. desempleo (CEM-2) (art. 37 LIS)")
     ,C35(new Mod2002015Key[]{Mod2002015Key.BN1078,Mod2002015Key.BN1079,Mod2002015Key.BN1080},"2015: Inversiones en territ. \u00C1frica Occidental y gastos de propaganda y public. (TAP) (art. 27bis Ley 19/94)")
     ,C36(new Mod2002015Key[]{Mod2002015Key.BN070 ,Mod2002015Key.BN072 ,Mod2002015Key.BN073 },"2015: Programa El \u00C1rbol es Vida (AV)")
     ,C37(new Mod2002015Key[]{Mod2002015Key.BN078 ,Mod2002015Key.BN079 ,Mod2002015Key.BN080 },"2015: Plan Director para la recuperaci\u00F3n del Patrimonio Cultural de Lorca (PL)")
     ,C38(new Mod2002015Key[]{Mod2002015Key.BN085 ,Mod2002015Key.BN086 ,Mod2002015Key.BN087 },"2015: Universiada de Invierno de Granada 2015 (UG)")
     ,C39(new Mod2002015Key[]{Mod2002015Key.BN093 ,Mod2002015Key.BN057 ,Mod2002015Key.BN058 },"2015: Campeonato del Mundo de Ciclismo en Carretera Ponferrada 2014 (P)")     
     ,C40(new Mod2002015Key[]{Mod2002015Key.BN207 ,Mod2002015Key.BN208 ,Mod2002015Key.BN209 },"2015: Barcelona World Jumping Challenge (WJ)")
     ,C41(new Mod2002015Key[]{Mod2002015Key.BN216 ,Mod2002015Key.BN217 ,Mod2002015Key.BN218 },"2015: 3\u00AA Edici\u00F3n de la Barcelona World Race (3W)")
     ,C42(new Mod2002015Key[]{Mod2002015Key.BN204 ,Mod2002015Key.BN205 ,Mod2002015Key.BN206 },"2015: Programa de preparaci\u00F3n de los deportistas espa\u00F1oles para los juegos de R\u00EDo de Janeiro 2016 (R16)")
     ,C43(new Mod2002015Key[]{Mod2002015Key.BN219 ,Mod2002015Key.BN220 ,Mod2002015Key.BN221 },"2015: VIII Centenario de la Peregrinaci\u00F3n de San Francisco de As\u00EDs a Santiago de Compostela (1214-2014) (FA)")
     ,C44(new Mod2002015Key[]{Mod2002015Key.BN228 ,Mod2002015Key.BN229 ,Mod2002015Key.BN230 },"2015: V Centenario del Nacimiento de Santa Teresa de Jes\u00FAs en el a\u00F1o 2015 (ST)")
     ,C45(new Mod2002015Key[]{Mod2002015Key.BN237 ,Mod2002015Key.BN238 ,Mod2002015Key.BN239 },"2015: Alicante 2014 (A14)")
     ,C46(new Mod2002015Key[]{Mod2002015Key.BN007 ,Mod2002015Key.BN012 ,Mod2002015Key.BN016 },"2015: Donostia/San Sebasti\u00E1n, Capital Europea de la Cultura 2016 (D16)")
     ,C47(new Mod2002015Key[]{Mod2002015Key.BN199 ,Mod2002015Key.BN292 ,Mod2002015Key.BN293 },"2015: Expo Mil\u00E1n 2015 (EM)")
     ,C48(new Mod2002015Key[]{Mod2002015Key.BN419 ,Mod2002015Key.BN422 ,Mod2002015Key.BN423 },"2015: Madrid Horse Week (MH)")
     ,C49(new Mod2002015Key[]{Mod2002015Key.BN424 ,Mod2002015Key.BN425 ,Mod2002015Key.BN428 },"2015: III Centenario de la Real Academia Espa\u00F1ola (RA)")
     ,C50(new Mod2002015Key[]{Mod2002015Key.BN429 ,Mod2002015Key.BN430 ,Mod2002015Key.BN431 },"2015: A Coru\u00F1a 2015-120 a\u00F1os despu\u00E9s")
     ,C51(new Mod2002015Key[]{Mod2002015Key.BN432 ,Mod2002015Key.BN433 ,Mod2002015Key.BN434 },"2015: IV Centenario de la segunda parte de El Quijote (Q)")
     ,C52(new Mod2002015Key[]{Mod2002015Key.BN435 ,Mod2002015Key.BN436 ,Mod2002015Key.BN437 },"2015: World Challenge LFP/85\u00BA Aniversario de la Liga (WCH)")
     ,C53(new Mod2002015Key[]{Mod2002015Key.BN438 ,Mod2002015Key.BN439 ,Mod2002015Key.BN440 },"2015: Juegos del Mediterr\u00E1neo de 2017 (M17)")
     ,C54(new Mod2002015Key[]{Mod2002015Key.BN1081,Mod2002015Key.BN1082,Mod2002015Key.BN1083},"2015: 200 Aniversario del Teatro Real y el Vig\u00E9simo Aniversario de la reapertura del Teatro Real (TR)")
     ,C55(new Mod2002015Key[]{Mod2002015Key.BN1084,Mod2002015Key.BN1085,Mod2002015Key.BN1086},"2015: IV Centenario de la muerte de Miguel de Cervantes (MC)")
     ,C56(new Mod2002015Key[]{Mod2002015Key.BN1087,Mod2002015Key.BN1088,Mod2002015Key.BN1089},"2015: VIII Centenario de la Universidad de Salamanca (US)")
     ,C57(new Mod2002015Key[]{Mod2002015Key.BN1090,Mod2002015Key.BN1091,Mod2002015Key.BN1092},"2015: Programa Jerez, Capital mundial del Motociclismo (J)")
     ,C58(new Mod2002015Key[]{Mod2002015Key.BN1093,Mod2002015Key.BN1094,Mod2002015Key.BN1095},"2015: Cantabria 2017, Li\u00E9bana A\u00F1o Jubilar (C17)")
     ,C59(new Mod2002015Key[]{Mod2002015Key.BN1096,Mod2002015Key.BN1097,Mod2002015Key.BN1098},"2015: Programa Universo Mujer (UM)")
     ,C60(new Mod2002015Key[]{Mod2002015Key.BN1099,Mod2002015Key.BN1100,Mod2002015Key.BN1101},"2015: 60 Aniversario de la Fundaci\u00F3n de la Escuela de Organizaci\u00F3n Industrial (EOI)")
     ,C61(new Mod2002015Key[]{Mod2002015Key.BN1102,Mod2002015Key.BN1103,Mod2002015Key.BN1104},"2015: Encuentro Mundial en Las Estrellas 2017 (EME)")
     ,C62(new Mod2002015Key[]{Mod2002015Key.BN1105,Mod2002015Key.BN1106,Mod2002015Key.BN1107},"2015: Barcelona Mobile World Capital (MW)")
     ,C63(new Mod2002015Key[]{Mod2002015Key.BN1108,Mod2002015Key.BN1109,Mod2002015Key.BN1110},"2015: A\u00F1o internacional de la luz y de las tecnolog\u00EDas basadas en la luz (L)")
     ,C64(new Mod2002015Key[]{Mod2002015Key.BN1111,Mod2002015Key.BN1112,Mod2002015Key.BN1113},"2015: ORC Barcelona World Championship 2015 (WCH)")
     ,C65(new Mod2002015Key[]{Mod2002015Key.BN1114,Mod2002015Key.BN1115,Mod2002015Key.BN1116},"2015: Barcelona Equestrian Challenge (BE)")
     ,C66(new Mod2002015Key[]{Mod2002015Key.BN1117,Mod2002015Key.BN1118,Mod2002015Key.BN1119},"2015: Women's Hockey World League Round 3 Events 2015 (WH)")
     ,C67(new Mod2002015Key[]{Mod2002015Key.BN1120,Mod2002015Key.BN1121,Mod2002015Key.BN1122},"2015: Centenario de la Real Federaci\u00F3n Andaluza de F\u00FAtbol 2015 (FAF)")
	 ,C68(new Mod2002015Key[]{Mod2002015Key.BN828 ,Mod2002015Key.BN829 ,Mod2002015Key.BN830 },"2015: Diferimiento deducciones Cap. IV T\u00EDt. VI Ley 43/95, RDL 4/2004 y LIS") 
	 ,C69(new Mod2002015Key[]{Mod2002015Key.BN634 ,Mod2002015Key.BN635 ,Mod2002015Key.BN636 },"Total deducciones relativas a programas de apoyo acontecimientos de excepcional inter\u00E9s p\u00FAblico") 
	 ,C70(new Mod2002015Key[]{Mod2002015Key.BN831 ,null /* BN588 */    ,Mod2002015Key.BN832 },"Total deducciones Cap. IV T\u00EDt. VI Ley 43/95, RDL 4/2004 y LIS")
	; 

    private String description;
    
    private Mod2002015Key[] keys;
    
	private Mod2002015BN588Key(Mod2002015Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002015Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
	
	public static void main(String[] args) {
		
		for (Mod2002015BN588Key k : Mod2002015BN588Key.values()) {
			
			System.out.println(k.getDescription());		
			
		}
		
	}
	
	
}
