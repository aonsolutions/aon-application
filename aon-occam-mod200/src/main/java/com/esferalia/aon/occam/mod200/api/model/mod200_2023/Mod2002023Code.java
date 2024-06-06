package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.util.HashMap;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;

// Código que se mostrará en pantalla, antes de la casilla del importe, en un BoxLabel
// Si está vacio indica que ese BoxLabel no debe mostrarse en pantalla
// Solo es necesario indicarlo en el enumerado, cuando no coincide con el código 
// numérico de la casilla, si coincide, automáticamente lo calcula getCode de Mod2002023Key

public class Mod2002023Code {
	
	public static HashMap<IMod200Key,String> CODE_MAP = new HashMap<IMod200Key,String>();

	static {
		
		CODE_MAP.put(Mod2002023Key.C0012R, "");
		CODE_MAP.put(Mod2002023Key.POR51, "");
		CODE_MAP.put(Mod2002023Key.PORES, "");
		CODE_MAP.put(Mod2002023Key.VOLOPE, "");
		CODE_MAP.put(Mod2002023Key.LQ0N1, "N1");
		CODE_MAP.put(Mod2002023Key.LQ550TG, "");
		CODE_MAP.put(Mod2002023Key.LQ550T0, "");
		CODE_MAP.put(Mod2002023Key.BN580R, "");
		CODE_MAP.put(Mod2002023Key.BN978R, "");
		CODE_MAP.put(Mod2002023Key.BN231R, "");
		CODE_MAP.put(Mod2002023Key.BN851R, "");
		CODE_MAP.put(Mod2002023Key.BN1126R, "");
		CODE_MAP.put(Mod2002023Key.BN1130R, "");
		CODE_MAP.put(Mod2002023Key.BN1429R, "");
		CODE_MAP.put(Mod2002023Key.BN1433R, "");
		CODE_MAP.put(Mod2002023Key.BN1713R, "");
		CODE_MAP.put(Mod2002023Key.BN1717R, "");
		CODE_MAP.put(Mod2002023Key.BN1971R, "");
		CODE_MAP.put(Mod2002023Key.BN1975R, "");
		CODE_MAP.put(Mod2002023Key.BN2248R, "");
		CODE_MAP.put(Mod2002023Key.BN2252R, "");
		CODE_MAP.put(Mod2002023Key.BN2394R, "");
		CODE_MAP.put(Mod2002023Key.BN2398R, "");
		CODE_MAP.put(Mod2002023Key.BN1093R, "");
		CODE_MAP.put(Mod2002023Key.BN1097R, "");
		CODE_MAP.put(Mod2002023Key.BN1388R, "");
		CODE_MAP.put(Mod2002023Key.BN1392R, "");
		CODE_MAP.put(Mod2002023Key.BN2758R, "");
		CODE_MAP.put(Mod2002023Key.BN2763R, "");
		CODE_MAP.put(Mod2002023Key.CNEST, "");
//		CODE_MAP.put(Mod2002023Key.UTC01, "");
//		CODE_MAP.put(Mod2002023Key.UTC02, "");
//		CODE_MAP.put(Mod2002023Key.UTC03, "");
//		CODE_MAP.put(Mod2002023Key.UTC04, "");
//		CODE_MAP.put(Mod2002023Key.UTC05, "");
		CODE_MAP.put(Mod2002023Key.BN1039M, "");
		CODE_MAP.put(Mod2002023Key.BN2314M, "");
		CODE_MAP.put(Mod2002023Key.IPCRG01, ""); // Régimen general: Producciones cinematográficas (excepto series audiovisuales)
		CODE_MAP.put(Mod2002023Key.IPCRG02, ""); // Régimen general: Series audiovisuales
		CODE_MAP.put(Mod2002023Key.IPCRG03, ""); // Régimen general: Número de episodios
		CODE_MAP.put(Mod2002023Key.IPCRC01, ""); // Régimen fiscal Canarias: Producciones cinematográficas (excepto series audiovisuales)
		CODE_MAP.put(Mod2002023Key.IPCRC02, ""); // Régimen fiscal Canarias: Series audiovisuales
		CODE_MAP.put(Mod2002023Key.IPCRC03, ""); // Régimen fiscal Canarias: Número de episodios
		
	}
	
}
