package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.util.HashMap;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;

// Código que se mostrará en pantalla, antes de la casilla del importe, en un BoxLabel
// Si está vacio indica que ese BoxLabel no debe mostrarse en pantalla
// Solo es necesario indicarlo en el enumerado, cuando no coincide con el código 
// numérico de la casilla, si coincide, automáticamente lo calcula getCode de Mod2002022Key

public class Mod2002022Code {
	
	public static HashMap<IMod200Key,String> CODE_MAP = new HashMap<IMod200Key,String>();

	static {
		
		CODE_MAP.put(Mod2002022Key.C0012R, "");
		CODE_MAP.put(Mod2002022Key.POR51, "");
		CODE_MAP.put(Mod2002022Key.PORES, "");
		CODE_MAP.put(Mod2002022Key.VOLOPE, "");
		CODE_MAP.put(Mod2002022Key.LQ0N1, "N1");
		CODE_MAP.put(Mod2002022Key.LQ550TG, "");
		CODE_MAP.put(Mod2002022Key.LQ550T0, "");
		CODE_MAP.put(Mod2002022Key.CP0C1, "C1");
		CODE_MAP.put(Mod2002022Key.CP0E1, "E1");
		CODE_MAP.put(Mod2002022Key.CP0C2, "C2");
		CODE_MAP.put(Mod2002022Key.CP0E2, "E2");
		CODE_MAP.put(Mod2002022Key.CP0C3, "C3");
		CODE_MAP.put(Mod2002022Key.CP0E3, "E3");
		CODE_MAP.put(Mod2002022Key.CP0C4, "C4");
		CODE_MAP.put(Mod2002022Key.CP0E4, "E4");
		CODE_MAP.put(Mod2002022Key.CP0E5, "E5");
		CODE_MAP.put(Mod2002022Key.CP0C6, "C6");
		CODE_MAP.put(Mod2002022Key.CP0E6, "E6");
		CODE_MAP.put(Mod2002022Key.CP0C7, "C7");
		CODE_MAP.put(Mod2002022Key.CP0E7, "E7");
		CODE_MAP.put(Mod2002022Key.CP0C8, "C8");
		CODE_MAP.put(Mod2002022Key.CP0E8, "E8");
		CODE_MAP.put(Mod2002022Key.CP0C9, "C9");
		CODE_MAP.put(Mod2002022Key.CP0E9, "E9");
		CODE_MAP.put(Mod2002022Key.CPC10, "C10");
		CODE_MAP.put(Mod2002022Key.CPE10, "E10");
		CODE_MAP.put(Mod2002022Key.CPC11, "C11");
		CODE_MAP.put(Mod2002022Key.CPE11, "E11");
		CODE_MAP.put(Mod2002022Key.CPC12, "553");
		CODE_MAP.put(Mod2002022Key.CPE12, "554");
		CODE_MAP.put(Mod2002022Key.BN580R, "");
		CODE_MAP.put(Mod2002022Key.BN978R, "");
		CODE_MAP.put(Mod2002022Key.BN231R, "");
		CODE_MAP.put(Mod2002022Key.BN851R, "");
		CODE_MAP.put(Mod2002022Key.BN1126R, "");
		CODE_MAP.put(Mod2002022Key.BN1130R, "");
		CODE_MAP.put(Mod2002022Key.BN1429R, "");
		CODE_MAP.put(Mod2002022Key.BN1433R, "");
		CODE_MAP.put(Mod2002022Key.BN1713R, "");
		CODE_MAP.put(Mod2002022Key.BN1717R, "");
		CODE_MAP.put(Mod2002022Key.BN1971R, "");
		CODE_MAP.put(Mod2002022Key.BN1975R, "");
		CODE_MAP.put(Mod2002022Key.BN2248R, "");
		CODE_MAP.put(Mod2002022Key.BN2252R, "");
		CODE_MAP.put(Mod2002022Key.BN2394R, "");
		CODE_MAP.put(Mod2002022Key.BN2398R, "");
		CODE_MAP.put(Mod2002022Key.BN1093R, "");
		CODE_MAP.put(Mod2002022Key.BN1097R, "");
		CODE_MAP.put(Mod2002022Key.CNEST, "");
		CODE_MAP.put(Mod2002022Key.UTC01, "");
		CODE_MAP.put(Mod2002022Key.UTC02, "");
		CODE_MAP.put(Mod2002022Key.UTC03, "");
		CODE_MAP.put(Mod2002022Key.UTC04, "");
		CODE_MAP.put(Mod2002022Key.UTC05, "");
		CODE_MAP.put(Mod2002022Key.BN1039M, "");
		CODE_MAP.put(Mod2002022Key.BN2314M, "");
		
	}
	
}
