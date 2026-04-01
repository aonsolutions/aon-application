package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.util.HashMap;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;

// Código que se mostrará en pantalla, antes de la casilla del importe, en un BoxLabel
// Si está vacio indica que ese BoxLabel no debe mostrarse en pantalla
// Solo es necesario indicarlo en el enumerado, cuando no coincide con el código 
// numérico de la casilla, si coincide, automáticamente lo calcula getCode de Mod2002025Key

public class Mod2002025Code {
	
	protected static final HashMap<IMod200Key,String> CODE_MAP = new HashMap<IMod200Key,String>();

	static {
		
		CODE_MAP.put(Mod2002025Key.C0012R, "");
		CODE_MAP.put(Mod2002025Key.POR51, "");
		CODE_MAP.put(Mod2002025Key.PORES, "");
		CODE_MAP.put(Mod2002025Key.VOLOPE, "");
		CODE_MAP.put(Mod2002025Key.LQ0N1, "N1");
		CODE_MAP.put(Mod2002025Key.LQ550TG, "");
		CODE_MAP.put(Mod2002025Key.LQ550T0, "");
		CODE_MAP.put(Mod2002025Key.BN580R, "");
		CODE_MAP.put(Mod2002025Key.BN978R, "");
		CODE_MAP.put(Mod2002025Key.BN231R, "");
		CODE_MAP.put(Mod2002025Key.BN851R, "");
		CODE_MAP.put(Mod2002025Key.BN1126R, "");
		CODE_MAP.put(Mod2002025Key.BN1130R, "");
		CODE_MAP.put(Mod2002025Key.BN1429R, "");
		CODE_MAP.put(Mod2002025Key.BN1433R, "");
		CODE_MAP.put(Mod2002025Key.BN1713R, "");
		CODE_MAP.put(Mod2002025Key.BN1717R, "");
		CODE_MAP.put(Mod2002025Key.BN1971R, "");
		CODE_MAP.put(Mod2002025Key.BN1975R, "");
		CODE_MAP.put(Mod2002025Key.BN2248R, "");
		CODE_MAP.put(Mod2002025Key.BN2252R, "");
		CODE_MAP.put(Mod2002025Key.BN2394R, "");
		CODE_MAP.put(Mod2002025Key.BN2398R, "");
		CODE_MAP.put(Mod2002025Key.BN1093R, "");
		CODE_MAP.put(Mod2002025Key.BN1097R, "");
		CODE_MAP.put(Mod2002025Key.BN1388R, "");
		CODE_MAP.put(Mod2002025Key.BN1392R, "");
		CODE_MAP.put(Mod2002025Key.BN2758R, "");
		CODE_MAP.put(Mod2002025Key.BN2763R, "");
		CODE_MAP.put(Mod2002025Key.CNEST, "");
		CODE_MAP.put(Mod2002025Key.BN1039M, "");
		CODE_MAP.put(Mod2002025Key.BN2314M, "");
		CODE_MAP.put(Mod2002025Key.IPCRG01, ""); // Régimen general: Producciones cinematográficas (excepto series audiovisuales)
		CODE_MAP.put(Mod2002025Key.IPCRG02, ""); // Régimen general: Series audiovisuales
		CODE_MAP.put(Mod2002025Key.IPCRG03, ""); // Régimen general: Número de episodios
		CODE_MAP.put(Mod2002025Key.IPCRC01, ""); // Régimen fiscal Canarias: Producciones cinematográficas (excepto series audiovisuales)
		CODE_MAP.put(Mod2002025Key.IPCRC02, ""); // Régimen fiscal Canarias: Series audiovisuales
		CODE_MAP.put(Mod2002025Key.IPCRC03, ""); // Régimen fiscal Canarias: Número de episodios
		CODE_MAP.put(Mod2002025Key.RV000, "");   // Reversión de las pérdidas por deterioro de valores representativos... Número de período impositivo
		CODE_MAP.put(Mod2002025Key.ING01, "");   // Resultado a ingresar correspondiente a la anterior autoliquidación o liquidación administrativa correspondiente al período impositivo 2025, previos a la rectificación  
		CODE_MAP.put(Mod2002025Key.ING02, "");   // Resultado a ingresar correspondiente a la anterior autoliquidación o liquidación administrativa correspondiente al periodo impositivo 2025, que se anula con la presentación de esta autoliquidación rectificativa  
		CODE_MAP.put(Mod2002025Key.MILLON, "");  // Entidad cuyo importe neto de la cifra de negocios durante los 12 meses anteriores a la fecha en que se inicie el período impositivo al que corresponda esta reducción es inferior a 1 millón de euros
		
	}
	
}