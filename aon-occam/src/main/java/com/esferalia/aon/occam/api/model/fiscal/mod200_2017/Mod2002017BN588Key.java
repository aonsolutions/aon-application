package com.esferalia.aon.occam.api.model.fiscal.mod200_2017;

import java.io.Serializable;

// Desglose Casilla 588
// Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI y DT 24ª.3 LIS)  
public enum Mod2002017BN588Key implements Serializable, IMod200KeysProvider {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF	
	
	  C01(new Mod2002017Key[]{Mod2002017Key.BN774 ,Mod2002017Key.BN775 ,null },"1999: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C02(new Mod2002017Key[]{Mod2002017Key.BN780 ,Mod2002017Key.BN781 ,Mod2002017Key.BN782 },"2000: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C03(new Mod2002017Key[]{Mod2002017Key.BN786 ,Mod2002017Key.BN787 ,Mod2002017Key.BN788 },"2001: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C04(new Mod2002017Key[]{Mod2002017Key.BN766 ,Mod2002017Key.BN767 ,Mod2002017Key.BN833 },"2002: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C05(new Mod2002017Key[]{Mod2002017Key.BN198 ,Mod2002017Key.BN896 ,Mod2002017Key.BN897 },"2003: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C06(new Mod2002017Key[]{Mod2002017Key.BN288 ,Mod2002017Key.BN289 ,Mod2002017Key.BN290 },"2004: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95")
	 ,C07(new Mod2002017Key[]{Mod2002017Key.BN466 ,Mod2002017Key.BN467 ,Mod2002017Key.BN468 },"2005: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C08(new Mod2002017Key[]{Mod2002017Key.BN061 ,Mod2002017Key.BN498 ,Mod2002017Key.BN586 },"2006: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C09(new Mod2002017Key[]{Mod2002017Key.BN472 ,Mod2002017Key.BN473 ,Mod2002017Key.BN478 },"2007: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C10(new Mod2002017Key[]{Mod2002017Key.BN180 ,Mod2002017Key.BN181 ,Mod2002017Key.BN182 },"2008: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C11(new Mod2002017Key[]{Mod2002017Key.BN531 ,Mod2002017Key.BN532 ,Mod2002017Key.BN533 },"2009: Suma deducciones Cap. IV T\u00EDt. VI Ley 43/95 y RDL 4/2004")
	 ,C12(new Mod2002017Key[]{Mod2002017Key.BN945 ,Mod2002017Key.BN946 ,Mod2002017Key.BN947 },"2010: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004")
	 ,C13(new Mod2002017Key[]{Mod2002017Key.BN960 ,Mod2002017Key.BN961 ,Mod2002017Key.BN962 },"2011: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004")
	 ,C14(new Mod2002017Key[]{Mod2002017Key.BN183 ,Mod2002017Key.BN185 ,Mod2002017Key.BN186 },"2012: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004")
	 ,C15(new Mod2002017Key[]{Mod2002017Key.BN966 ,Mod2002017Key.BN967 ,Mod2002017Key.BN968 },"2013: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004 (excepto I+D+i)")
	 ,C16(new Mod2002017Key[]{Mod2002017Key.BN457 ,Mod2002017Key.BN458 ,Mod2002017Key.BN459 },"2013: Investigaci\u00F3n y desarrollo (CT)")
	 ,C17(new Mod2002017Key[]{Mod2002017Key.BN460 ,Mod2002017Key.BN461 ,Mod2002017Key.BN462 },"2013: Innovaci\u00F3n tecnol\u00F3gica (IT)")
	 ,C18(new Mod2002017Key[]{Mod2002017Key.BN1063,Mod2002017Key.BN1064,Mod2002017Key.BN1065},"2014: Suma deducciones Cap. IV Tit. VI Ley 43/95 y RDL 4/2004 (excepto I+D+i)")
	 ,C19(new Mod2002017Key[]{Mod2002017Key.BN1066,Mod2002017Key.BN1067,Mod2002017Key.BN1068},"2014: Investigaci\u00F3n y desarrollo (CT)")
	 ,C20(new Mod2002017Key[]{Mod2002017Key.BN1069,Mod2002017Key.BN1070,Mod2002017Key.BN1071},"2014: Innovaci\u00F3n tecnol\u00F3gica (IT)")
	 ,C21(new Mod2002017Key[]{Mod2002017Key.BN813 ,Mod2002017Key.BN814 ,Mod2002017Key.BN815 },"2015: Suma deducciones Cap. IV Tit. VI Ley 43/95, RDL 4/2004 y LIS (excepto I+D+i)") 
	 ,C22(new Mod2002017Key[]{Mod2002017Key.BN986 ,Mod2002017Key.BN810 ,Mod2002017Key.BN507 },"2015: Investigaci\u00F3n y desarrollo (CT)")
	 ,C23(new Mod2002017Key[]{Mod2002017Key.BN557 ,Mod2002017Key.BN591 ,Mod2002017Key.BN594 },"2015: Innovaci\u00F3n tecnol\u00F3gica (IT)")
	 ,C24(new Mod2002017Key[]{Mod2002017Key.BN1614,Mod2002017Key.BN1615,Mod2002017Key.BN1616},"2016: Suma deducciones Cap. IV Tit. VI Ley 43/95, RDL 4/2004 y LIS (excepto I+D+i)")
	 ,C25(new Mod2002017Key[]{Mod2002017Key.BN1617,Mod2002017Key.BN1618,Mod2002017Key.BN1619},"2016: Investigaci\u00F3n y desarrollo (CT)")
	 ,C26(new Mod2002017Key[]{Mod2002017Key.BN1620,Mod2002017Key.BN1621,Mod2002017Key.BN1622},"2016: Innovaci\u00F3n tecnol\u00F3gica (IT)")
     ,C27(new Mod2002017Key[]{Mod2002017Key.BN1360,Mod2002017Key.BN1361,Mod2002017Key.BN1362},"2017(*): Suma deducciones Cap. IV Tit. VI Ley 43/95, RDL 4/2004 y LIS (excepto I+D+i)")
     ,C28(new Mod2002017Key[]{Mod2002017Key.BN1363,Mod2002017Key.BN1364,Mod2002017Key.BN1365},"2017(*): Investigaci\u00F3n y desarrollo (CT)")
     ,C29(new Mod2002017Key[]{Mod2002017Key.BN1366,Mod2002017Key.BN1367,Mod2002017Key.BN1368},"2017(*): Innovaci\u00F3n tecnol\u00F3gica (IT)")
     ,C30(new Mod2002017Key[]{Mod2002017Key.BN798 ,Mod2002017Key.BN799 ,Mod2002017Key.BN800 },"2017: Investigaci\u00F3n y desarrollo (CT)")
     ,C31(new Mod2002017Key[]{Mod2002017Key.BN096 ,Mod2002017Key.BN698 ,Mod2002017Key.BN713 },"2017: Innovaci\u00F3n tecnol\u00F3gica (IT)")
     ,C32(new Mod2002017Key[]{Mod2002017Key.BN807 ,Mod2002017Key.BN808 ,Mod2002017Key.BN809 },"2016: Producciones cinematogr\u00E1ficas espa\u00F1olas (PC)")
     ,C33(new Mod2002017Key[]{Mod2002017Key.BN1075,Mod2002017Key.BN1076,Mod2002017Key.BN1077},"2017: Espect\u00E1culos en vivo de artes esc\u00E9nicas y musicales (EV)")
     ,C34(new Mod2002017Key[]{Mod2002017Key.BN963 ,Mod2002017Key.BN964 ,Mod2002017Key.BN965 },"2017: Creaci\u00F3n empleo contrataci\u00F3n menores de 30 (CEM-1) (art. 37 LIS)")
     ,C35(new Mod2002017Key[]{Mod2002017Key.BN931 ,Mod2002017Key.BN502 ,Mod2002017Key.BN751 },"2017: Creaci\u00F3n empleo contrataci\u00F3n desempleados con prest. desempleo (CEM-2) (art. 37 LIS)")
     ,C36(new Mod2002017Key[]{Mod2002017Key.BN795 ,Mod2002017Key.BN796 ,Mod2002017Key.BN797 },"2017: Deducci\u00F3n creaci\u00F3n empleo trabaj. con discapacidad (CE)")
     ,C37(new Mod2002017Key[]{Mod2002017Key.BN549 ,Mod2002017Key.BN888 ,Mod2002017Key.BN889 },"2017: Deducci\u00F3n por inversi\u00F3n de beneficios (IB)")
     ,C38(new Mod2002017Key[]{Mod2002017Key.BN1369,Mod2002017Key.BN1370,Mod2002017Key.BN1371},"2017: Gastos e inversiones de sociedades forestales (SF)")     
     ,C46(new Mod2002017Key[]{Mod2002017Key.BN438 ,Mod2002017Key.BN439 ,Mod2002017Key.BN440 },"2017: Juegos del Mediterr\u00E1neo de 2018 (M18)")
     ,C47(new Mod2002017Key[]{Mod2002017Key.BN1081,Mod2002017Key.BN1082,Mod2002017Key.BN1083},"2017: 200 Aniversario del Teatro Real y el Vig\u00E9simo Aniversario de la reapertura del Teatro Real (TR)")
     ,C48(new Mod2002017Key[]{Mod2002017Key.BN1084,Mod2002017Key.BN1085,Mod2002017Key.BN1086},"2017: IV Centenario de la muerte de Miguel de Cervantes (MC)")
     ,C49(new Mod2002017Key[]{Mod2002017Key.BN1087,Mod2002017Key.BN1088,Mod2002017Key.BN1089},"2017: VIII Centenario de la Universidad de Salamanca (US)")
     ,C50(new Mod2002017Key[]{Mod2002017Key.BN1090,Mod2002017Key.BN1091,Mod2002017Key.BN1092},"2017: Programa Jerez, Capital mundial del Motociclismo (J)")
     ,C51(new Mod2002017Key[]{Mod2002017Key.BN1093,Mod2002017Key.BN1094,Mod2002017Key.BN1095},"2017: Cantabria 2017, Li\u00E9bana A\u00F1o Jubilar (C17)")
     ,C52(new Mod2002017Key[]{Mod2002017Key.BN1096,Mod2002017Key.BN1097,Mod2002017Key.BN1098},"2017: Programa Universo Mujer (UM)")
     ,C53(new Mod2002017Key[]{Mod2002017Key.BN1099,Mod2002017Key.BN1100,Mod2002017Key.BN1101},"2017: 60 Aniversario de la Fundaci\u00F3n de la Escuela de Organizaci\u00F3n Industrial (EOI)")
     ,C54(new Mod2002017Key[]{Mod2002017Key.BN1102,Mod2002017Key.BN1103,Mod2002017Key.BN1104},"2017: Encuentro Mundial en Las Estrellas 2017 (EME)")
     ,C55(new Mod2002017Key[]{Mod2002017Key.BN1105,Mod2002017Key.BN1106,Mod2002017Key.BN1107},"2017: Barcelona Mobile World Capital (MW)")
     ,C56(new Mod2002017Key[]{Mod2002017Key.BN1114,Mod2002017Key.BN1115,Mod2002017Key.BN1116},"2017: Barcelona Equestrian Challenge (BE)")
     ,C57(new Mod2002017Key[]{Mod2002017Key.BN1117,Mod2002017Key.BN1118,Mod2002017Key.BN1119},"2017: Women's Hockey World League Round 3 Events 2015 (WH)")
     ,C58(new Mod2002017Key[]{Mod2002017Key.BN1372,Mod2002017Key.BN1373,Mod2002017Key.BN1374},"2017: II Centenario del Museo Nacional del Prado (MP)")
     ,C59(new Mod2002017Key[]{Mod2002017Key.BN1375,Mod2002017Key.BN1376,Mod2002017Key.BN1377},"2017: 20 Aniversario de la Reapertura del Gran Teatro del Liceo de Barcelona y el bicentenario de la creaci\u00F3n de la Societat d'Accionistes (LB)")
     ,C60(new Mod2002017Key[]{Mod2002017Key.BN1378,Mod2002017Key.BN1379,Mod2002017Key.BN1380},"2017: Foro Iberoamericano de Ciudades (FIC)")
     ,C61(new Mod2002017Key[]{Mod2002017Key.BN1381,Mod2002017Key.BN1382,Mod2002017Key.BN1383},"2017: Plan Decenio M\u00E1laga Cultura Innovadora 2025 (MCI)")
     ,C62(new Mod2002017Key[]{Mod2002017Key.BN1384,Mod2002017Key.BN1385,Mod2002017Key.BN1386},"2017: XX Aniversario de la Declaraci\u00F3n de Cuenca como Ciudad Patrimonio de la Humanidad (CPH)")
     ,C63(new Mod2002017Key[]{Mod2002017Key.BN1387,Mod2002017Key.BN1388,Mod2002017Key.BN1389},"2017: Campeonatos del Mundo FIS de Freestyle y Snowboard Sierra Nevada 2017 (SN17)")     
 	 ,C64(new Mod2002017Key[]{Mod2002017Key.BN1390,Mod2002017Key.BN1391,Mod2002017Key.BN1392},"2017: Vig\u00E9simo quinto aniversario del Museo Thyssen-Bornemisza (MT)")
     ,C65(new Mod2002017Key[]{Mod2002017Key.BN1393,Mod2002017Key.BN1394,Mod2002017Key.BN1395},"2017: Campeonato de Europa de Waterpolo Barcelona 2018 (WB18)")
	 ,C66(new Mod2002017Key[]{Mod2002017Key.BN1396,Mod2002017Key.BN1397,Mod2002017Key.BN1398},"2017: Centenario del nacimiento de Camilo Jos\u00E9 Cela (CJC)")
	 ,C67(new Mod2002017Key[]{Mod2002017Key.BN1399,Mod2002017Key.BN1400,Mod2002017Key.BN1401},"2017: 2017: A\u00F1o de la retina en Espa\u00F1a (R)")
	 ,C68(new Mod2002017Key[]{Mod2002017Key.BN1402,Mod2002017Key.BN1403,Mod2002017Key.BN1404},"2017: Caravaca de la Cruz 2017. A\u00F1o Jubilar (CC17)")
	 ,C69(new Mod2002017Key[]{Mod2002017Key.BN1405,Mod2002017Key.BN1406,Mod2002017Key.BN1407},"2017: Plan 2020 de apoyo al Deporte de Base (P20)")
	 ,C70(new Mod2002017Key[]{Mod2002017Key.BN1414,Mod2002017Key.BN1415,Mod2002017Key.BN1416},"2017: 525 Aniversario del Descubrimiento de Am\u00E9rica en Palos de la Frontera (Huelva) (DA)")
	 ,C71(new Mod2002017Key[]{Mod2002017Key.BN1417,Mod2002017Key.BN1418,Mod2002017Key.BN1419},"2017: Prevenci\u00F3n de la Obesidad. Aligera tu vida (PO)")
	 ,C72(new Mod2002017Key[]{Mod2002017Key.BN1420,Mod2002017Key.BN1421,Mod2002017Key.BN1422},"2017: 75 Aniversario de Willian Martin; El legado ingl\u00E9s (WM)")
	 ,C73(new Mod2002017Key[]{Mod2002017Key.BN1423,Mod2002017Key.BN1424,Mod2002017Key.BN1425},"2017: Salida de la vuelta al mundo a vela Alicante 2017 (A17)")
	 ,C74(new Mod2002017Key[]{Mod2002017Key.BN1623,Mod2002017Key.BN1624,Mod2002017Key.BN1625},"2017: 25 Aniversario de la Casa Am\u00E9rica (CA)")
	 ,C75(new Mod2002017Key[]{Mod2002017Key.BN1626,Mod2002017Key.BN1627,Mod2002017Key.BN1628},"2017: 4\u00AA Edici\u00F3n de la Barcelona World Race (4BWR)")
	 ,C76(new Mod2002017Key[]{Mod2002017Key.BN1629,Mod2002017Key.BN1630,Mod2002017Key.BN1631},"2017: World Roller Games Barcelona 2019 (RG19)")
	 ,C77(new Mod2002017Key[]{Mod2002017Key.BN1632,Mod2002017Key.BN1633,Mod2002017Key.BN1634},"2017: Madrid Horse Week 17/19 (HW19)")
	 ,C78(new Mod2002017Key[]{Mod2002017Key.BN1635,Mod2002017Key.BN1636,Mod2002017Key.BN1637},"2017: La Liga World Challenge (LWCH)")
	 ,C79(new Mod2002017Key[]{Mod2002017Key.BN1638,Mod2002017Key.BN1639,Mod2002017Key.BN1640},"2017: V Centenario de la expedici\u00F3n de la primera vuelta al mundo de Fernando de Magallanes y Juan Sebasti\u00E1n Elcano (EPVM)")
	 ,C80(new Mod2002017Key[]{Mod2002017Key.BN1641,Mod2002017Key.BN1642,Mod2002017Key.BN1643},"2017: 25 Aniversario de la declaraci\u00F3n por la Unesco de M\u00E9rida como Patrimonio de la Humanidad (25M)")
	 ,C81(new Mod2002017Key[]{Mod2002017Key.BN1644,Mod2002017Key.BN1645,Mod2002017Key.BN1646},"2017: Campeonatos del Mundo de Canoa 2019 (C19)")
	 ,C82(new Mod2002017Key[]{Mod2002017Key.BN1647,Mod2002017Key.BN1648,Mod2002017Key.BN1649},"2017: 250 Aniversario del Fuero de Poblaci\u00F3n de 1767 y Fundaci\u00F3n de las Nuevas Poblaciones de Sierra Morena y Andaluc\u00EDa (250F)")
	 ,C83(new Mod2002017Key[]{Mod2002017Key.BN1650,Mod2002017Key.BN1651,Mod2002017Key.BN1652},"2017: IV Centenario del nacimiento de Bartolom\u00E9 Esteban Murillo (BEM)")
	 ,C84(new Mod2002017Key[]{Mod2002017Key.BN1653,Mod2002017Key.BN1654,Mod2002017Key.BN1655},"2017: Numancia 2017 (N17)")
	 ,C85(new Mod2002017Key[]{Mod2002017Key.BN1656,Mod2002017Key.BN1657,Mod2002017Key.BN1658},"2017: PHotoEspa\u00F1a. 20 aniversario (PH20)")
	 ,C86(new Mod2002017Key[]{Mod2002017Key.BN1659,Mod2002017Key.BN1660,Mod2002017Key.BN1661},"2017: IV Centenario de la Plaza Mayor de Madrid (PMM)")
	 ,C87(new Mod2002017Key[]{Mod2002017Key.BN1662,Mod2002017Key.BN1663,Mod2002017Key.BN1664},"2017: XXX Aniversario de la Declaraci\u00F3n de Toledo como Ciudad Patrimonio de la Humanidad (TCPH)")
	 ,C88(new Mod2002017Key[]{Mod2002017Key.BN1665,Mod2002017Key.BN1666,Mod2002017Key.BN1667},"2017: VII Centenario del Archivo de la Corona de Arag\u00F3n (ACA)")
	 ,C89(new Mod2002017Key[]{Mod2002017Key.BN1668,Mod2002017Key.BN1669,Mod2002017Key.BN1670},"2017: Lorca, Aula de la Historia (LAH)")
	 ,C90(new Mod2002017Key[]{Mod2002017Key.BN1671,Mod2002017Key.BN1672,Mod2002017Key.BN1673},"2017: Plan de Fomento de la Lectura (2017-2020) (PFL)")
	 ,C91(new Mod2002017Key[]{Mod2002017Key.BN1674,Mod2002017Key.BN1675,Mod2002017Key.BN1676},"2017: Plan 2020 de Apoyo a los Nuevos Creadores Cinematogr\u00E1ficos y a la conservaci\u00F3n y difusi\u00F3n de la historia del cine espa\u00F1ol (NCC)")
	 ,C92(new Mod2002017Key[]{Mod2002017Key.BN1677,Mod2002017Key.BN1678,Mod2002017Key.BN1679},"2017: 40 Aniversario del Festival Internacional de Teatro Cl\u00E1sico de Almagro (TCA)")
	 ,C93(new Mod2002017Key[]{Mod2002017Key.BN1680,Mod2002017Key.BN1681,Mod2002017Key.BN1682},"2017: I Centenario de la Ley de Parques Nacionales de 1916 (LPN)")
	 ,C94(new Mod2002017Key[]{Mod2002017Key.BN1689,Mod2002017Key.BN1690,Mod2002017Key.BN1691},"2017: 75\u00BA Aniversario de la Escuela Diplom\u00E1tica (ED)")
	 ,C95(new Mod2002017Key[]{Mod2002017Key.BN1692,Mod2002017Key.BN1693,Mod2002017Key.BN1694},"2017: Teruel 2017. 800 A\u00F1os de los Amantes (T17)")
	 ,C96(new Mod2002017Key[]{Mod2002017Key.BN1695,Mod2002017Key.BN1696,Mod2002017Key.BN1697},"2017: 40 Aniversario de la Constituci\u00F3n Espa\u00F1ola (40CE)")
	 ,C97(new Mod2002017Key[]{Mod2002017Key.BN1698,Mod2002017Key.BN1699,Mod2002017Key.BN1700},"2017: 50\u00BA Aniversario de Sitges-Festival Internacional de Cine Fant\u00E1stico de Catalunya (SFIC)")
	 ,C98(new Mod2002017Key[]{Mod2002017Key.BN1701,Mod2002017Key.BN1702,Mod2002017Key.BN1703},"2017: 50 Aniversario de la Universidad Aut\u00F3noma de Madrid (50UA)")
	 ,C99(new Mod2002017Key[]{Mod2002017Key.BN1704,Mod2002017Key.BN1705,Mod2002017Key.BN1706},"2017: A\u00F1o Hernandiano 2017 (AH17)")
	,C100(new Mod2002017Key[]{Mod2002017Key.BN1707,Mod2002017Key.BN1708,Mod2002017Key.BN1709},"2017: Plan Decenio Millarium Montserrat 1025 (PDMM)")
	,C101(new Mod2002017Key[]{Mod2002017Key.BN1800,Mod2002017Key.BN1801,Mod2002017Key.BN1802},"2017: Programa de preparaci\u00F3n de los deportistas espa\u00F1oles de los Juegos de Tokio 2020 (T20)")
	,C102(new Mod2002017Key[]{Mod2002017Key.BN1683,Mod2002017Key.BN1684,Mod2002017Key.BN1685},"2018(****): Resto deducciones relativas a programas de apoyo a acontecimientos de excepcional inter\u00E9s p\u00FAblico")
	
	,C103(new Mod2002017Key[]{Mod2002017Key.BN634 ,Mod2002017Key.BN635 ,Mod2002017Key.BN636 },"Total deducciones relativas a programas de apoyo acontecimientos de excepcional inter\u00E9s p\u00FAblico")
	,C104(new Mod2002017Key[]{Mod2002017Key.BN828 ,Mod2002017Key.BN829 ,Mod2002017Key.BN830 },"2016: Diferimiento deducciones Cap. IV T\u00EDt. VI Ley 43/95, RDL 4/2004 y LIS")	 
	,C105(new Mod2002017Key[]{Mod2002017Key.BN831 ,null /* BN588 */    ,Mod2002017Key.BN832 },"Total")
	; 

    private String description;
    
    private Mod2002017Key[] keys;
    
	private Mod2002017BN588Key(Mod2002017Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002017Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
	
	public static void main(String[] args) {
		
		for (Mod2002017BN588Key k : Mod2002017BN588Key.values()) {
			
			System.out.println(k.getKeys()[0]+" "+k.getKeys()[1]+" "+k.getKeys()[2]+" - "+k.getDescription());		
			
		}
		
	}
	
	
}
