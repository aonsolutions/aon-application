package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.util.HashMap;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;

// Código que se mostrará en pantalla, antes de la casilla del importe
// Si está en negativo indica que ese código no debe mostrarse en pantalla
// Solo es necesario indicarlo en el enumerado, cuando no coincide con el código 
// numérico de la casilla, si coincide, automáticamente lo calcula getCode de Mod2002021Key

public class Mod2002021Code {
	
	public static HashMap<IMod200Key,String> CODE_MAP = new HashMap<IMod200Key,String>();

	static {
		
		CODE_MAP.put(Mod2002021Key.C0012R, "-12");
		CODE_MAP.put(Mod2002021Key.POR51, "-1");
		CODE_MAP.put(Mod2002021Key.PORES, "-2");
		CODE_MAP.put(Mod2002021Key.VOLOPE, "VOLOPE");
		CODE_MAP.put(Mod2002021Key.LQ0N1, "N1");
		CODE_MAP.put(Mod2002021Key.LQ550TG, "-55001");
		CODE_MAP.put(Mod2002021Key.LQ550T0, "-55002");
		CODE_MAP.put(Mod2002021Key.CP0C1, "C1");
		CODE_MAP.put(Mod2002021Key.CP0E1, "E1");
		CODE_MAP.put(Mod2002021Key.CP0C2, "C2");
		CODE_MAP.put(Mod2002021Key.CP0E2, "E2");
		CODE_MAP.put(Mod2002021Key.CP0C3, "C3");
		CODE_MAP.put(Mod2002021Key.CP0E3, "E3");
		CODE_MAP.put(Mod2002021Key.CP0C4, "C4");
		CODE_MAP.put(Mod2002021Key.CP0E4, "E4");
		CODE_MAP.put(Mod2002021Key.CP0E5, "E5");
		CODE_MAP.put(Mod2002021Key.CP0C6, "C6");
		CODE_MAP.put(Mod2002021Key.CP0E6, "E6");
		CODE_MAP.put(Mod2002021Key.CP0C7, "C7");
		CODE_MAP.put(Mod2002021Key.CP0E7, "E7");
		CODE_MAP.put(Mod2002021Key.CP0C8, "C8");
		CODE_MAP.put(Mod2002021Key.CP0E8, "E8");
		CODE_MAP.put(Mod2002021Key.CP0C9, "C9");
		CODE_MAP.put(Mod2002021Key.CP0E9, "E9");
		CODE_MAP.put(Mod2002021Key.CPC10, "C10");
		CODE_MAP.put(Mod2002021Key.CPE10, "E10");
		CODE_MAP.put(Mod2002021Key.CPC11, "C11");
		CODE_MAP.put(Mod2002021Key.CPE11, "E11");
		CODE_MAP.put(Mod2002021Key.CPC12, "553");
		CODE_MAP.put(Mod2002021Key.CPE12, "554");
		CODE_MAP.put(Mod2002021Key.BN580R, "-580");
		CODE_MAP.put(Mod2002021Key.BN978R, "-978");
		CODE_MAP.put(Mod2002021Key.BN231R, "-231");
		CODE_MAP.put(Mod2002021Key.BN851R, "-851");
		CODE_MAP.put(Mod2002021Key.BN1126R, "-1126");
		CODE_MAP.put(Mod2002021Key.BN1130R, "-1130");
		CODE_MAP.put(Mod2002021Key.BN1429R, "-1429");
		CODE_MAP.put(Mod2002021Key.BN1433R, "-1433");
		CODE_MAP.put(Mod2002021Key.BN1713R, "-1713");
		CODE_MAP.put(Mod2002021Key.BN1717R, "-1717");
		CODE_MAP.put(Mod2002021Key.BN1971R, "-1971");
		CODE_MAP.put(Mod2002021Key.BN1975R, "-1975");
		CODE_MAP.put(Mod2002021Key.BN2248R, "-2248");
		CODE_MAP.put(Mod2002021Key.BN2252R, "-2252");
		CODE_MAP.put(Mod2002021Key.BN2394R, "-2394");
		CODE_MAP.put(Mod2002021Key.BN2398R, "-2398");
		CODE_MAP.put(Mod2002021Key.CNEST, "-99");
		CODE_MAP.put(Mod2002021Key.UTC01, "-108");
		CODE_MAP.put(Mod2002021Key.UTC02, "-109");
		CODE_MAP.put(Mod2002021Key.UTC03, "-110");
		CODE_MAP.put(Mod2002021Key.UTC04, "-111");
		CODE_MAP.put(Mod2002021Key.UTC05, "-112");
		CODE_MAP.put(Mod2002021Key.BN1039M, "-1039");
		CODE_MAP.put(Mod2002021Key.BN2314M, "-2314");
		
	}
	
}
