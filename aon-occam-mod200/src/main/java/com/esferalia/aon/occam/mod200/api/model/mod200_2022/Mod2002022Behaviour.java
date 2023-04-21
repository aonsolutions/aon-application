package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Arrays;
import java.util.EnumMap;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

public class Mod2002022Behaviour {
	
	public static EnumMap<Mod2002022Key,Boolean[]> BEHAVIOUR_KEYS_MAP = new EnumMap<Mod2002022Key,Boolean[]>(Mod2002022Key.class);
	// Elemento 0 ---> isTitle?    : Hace que aparezca en negrita y la casilla desplazada a la derecha
	// Elemento 1 ---> isDisabled? : Hace que la casilla esté deshabilitada

	static { 
		
		// PARTICIPACIONES
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.P1501, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.P1502, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.P1503, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.P1504, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.P1506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.P1809, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.P1810, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.P1507, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.P1508, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// BALANCE: ACTIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA101, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA102, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA111, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA115, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA118, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA126, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA134, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA135, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA136, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA137, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA138, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA141, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA144, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA149, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA150, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA160, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA168, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA176, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA177, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BA180, new Boolean[]{TRUE,TRUE});
		
	}

	static { 
		
		// BALANCE: PATRIMONIO NETO Y PASIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP185, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP186, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP187, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP190, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP191, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP194, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP195, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP198, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP199, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP200, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP201, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP202, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP208, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP209, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP210, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP211, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP216, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP223, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP224, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP225, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP226, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP227, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP228, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP229, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP230, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP231, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP238, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP239, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP240, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP250, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP251, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BP252, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// BALANCE DE PERDIDAS Y GANANCIAS 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG255, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG705, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG258, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG259, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG260, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG261, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG262, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG265, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG266, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG270, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG279, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG280, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG284, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG285, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG286, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG287, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG288, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG291, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG294, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG295, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG296, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG297, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG298, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG301, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG305, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG309, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG312, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG313, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG314, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG319, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG329, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG324, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG325, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG326, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG327, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG328, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.PG500, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0500, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0336, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0339, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0340, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0341, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0342, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0343, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0344, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0345, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0346, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0349, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0350, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0351, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0352, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0353, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0354, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.T0355, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO TOTAL DE CAMBIOS EN EL PATRIMONIO NETO
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC422, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC423, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC424, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC425, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC426, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC428, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC429, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC427, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC430, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC431, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC432, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC433, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC434, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC435, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC464, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC465, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC466, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC467, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC468, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC469, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC470, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC471, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC472, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC475, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC476, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC477, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC507, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC508, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC509, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC510, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC511, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC512, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC513, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC514, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC515, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC516, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC517, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC518, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC519, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC619, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC620, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC621, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC622, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC623, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC624, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC625, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC626, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC627, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC628, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC629, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC630, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC631, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC632, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC633, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC634, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC635, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC636, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC637, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC638, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC639, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC640, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC641, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC643, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC644, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC645, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC393, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC407, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC421, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC449, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC463, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC491, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC505, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC533, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC547, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC561, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC575, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC589, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC603, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC617, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC728, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TC742, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// LIQUIDACION
		
		// Resultado de la cuenta de pérdidas y ganancias
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ500, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ301, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ302, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ501, new Boolean[]{TRUE,TRUE});
		
		// Correcciones al resultado de la cuenta de perdidas y ganancias
		for (Mod2002022CorrectionKey key : Mod2002022CorrectionKey.values()) {
			if (key.getIncrease() != null)
				BEHAVIOUR_KEYS_MAP.put(key.getIncrease(), new Boolean[]{FALSE,TRUE});
			if (key.getDecrease() != null)
				BEHAVIOUR_KEYS_MAP.put(key.getDecrease(), new Boolean[]{FALSE,TRUE});
		}
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.I0417, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.D0418, new Boolean[]{TRUE,TRUE});
		
		// Entidades navieras en régimen de tributación en función del tonelaje
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ578, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ579, new Boolean[]{FALSE,TRUE});
		
        // Base Imponible	
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ550, new Boolean[]{TRUE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1032,new Boolean[]{FALSE,TRUE});
		
		// FALTA - NUEVAS CASILLAS 541 Y 564 VER SI SE PUEDEN CUMPLIMENTAR
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ547,new Boolean[]{FALSE,TRUE});
		
		// FALTA - NUEVAS CASILLAS 1887 Y 1890 VER SI SE PUEDEN CUMPLIMENTAR - SE SUPONE QUE SON DESGLOSES ASI QUE IRAN DESHABILITADAS
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1887,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1890,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ552, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1033,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1034,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1330,new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ553, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ554, new Boolean[]{FALSE,TRUE});
		
		// FALTA - NUEVAS CASILLAS 1576 Y 1577 VER SI SE PUEDEN CUMPLIMENTAR
		
		// Tipo de Gravamen
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ558,new Boolean[]{TRUE,FALSE});
		
		// Sociedades cooperativas
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ561,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1331,new Boolean[]{FALSE,TRUE});
		
		// Cuota integra
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ562, new Boolean[]{TRUE,TRUE});
		
		// Bonificaciones y deducciones. Cuota intega ajustada positiva
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN570,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1344,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1280,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN572,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN571,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN573,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN582,new Boolean[]{TRUE,TRUE});
		
		// Otras deducciones. Cuota líquida positiva
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN585,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN584,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN588,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1039,new Boolean[]{FALSE,TRUE});  // FALTA - ESTA CASILLA ESTE AÑO LLEVA DESGLOSE
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN2314,new Boolean[]{FALSE,TRUE});  // FALTA - ESTA CASILLA ESTE AÑO LLEVA DESGLOSE
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN2315,new Boolean[]{FALSE,TRUE});  // FALTA - ESTA CASILLA ESTE AÑO LLEVA DESGLOSE
 		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN565,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN590,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN082,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1040,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1041,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN619, new Boolean[]{TRUE,TRUE});  // FALTA - SUPONGO QUE ESTA NUEVA CASILLA SERA CALCULADA
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN592, new Boolean[]{TRUE,TRUE});
		
		// Total retenciones e ingresos a cuenta
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1766, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1784, new Boolean[]{FALSE,TRUE});
		
		// Cuota del ejercicio a ingresar o a devolver
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN599, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN600, new Boolean[]{FALSE,TRUE});
		
		// Pagos fraccionados. Cuota diferencial
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN602, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN604, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN606, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN611, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN612, new Boolean[]{FALSE,TRUE});
		
		// Resultado de la autoliquidación
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN616, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1234B,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1332,new Boolean[]{FALSE,TRUE});		
		//BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1200,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1892,new Boolean[]{FALSE,TRUE});  // FALTA - LA CASILLA 1200 LA RENUMERAN COMO 1892
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1042,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1333,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1319,new Boolean[]{FALSE,TRUE});  // FALTA - ESTA NUEVA CASILLA SE SUPONE QUE SERA CALCULADA (1893+1881)
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1893,new Boolean[]{FALSE,TRUE});  // FALTA - ESTA NUEVA CASILLA SE SUPONE QUE SERA CALCULADA
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1881,new Boolean[]{FALSE,TRUE});  // FALTA - ESTA NUEVA CASILLA SE SUPONE QUE SERA CALCULADA
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1586, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1587, new Boolean[]{FALSE,TRUE});
		
		// Liquido a ingresar o a devolver
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1583, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ1585, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN621, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN622, new Boolean[]{FALSE,TRUE});		

		// Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS)
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2480, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2482, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2484, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2485, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2486, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2488, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2489, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ3242, new Boolean[]{FALSE,TRUE});
		
		// Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS)		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM150, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1043,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.BN1044,new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ3243, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ3245, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ3317, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ3319, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ3320, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2491, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2492, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LQ2494, new Boolean[]{FALSE,TRUE});

	}
	
	static {

		// Desglose Casilla 547
		addBreakdown(Mod2002022LQ547Key.values(), Mod2002022Key.LQ547);
		
		// Desglose Casilla 243
		addBreakdown(Mod2002022LQ243Key.values(), Mod2002022Key.LQ1886, Mod2002022Key.LQ1889, Mod2002022Key.LQ243);  // FALTA - NUEVO DESGLOSE CON 2 FILAS DE SUBTOTALES Y UNA DE TOTALES
		
		// Desglose Casilla 570
		addBreakdown(Mod2002022BN570Key.values(), Mod2002022Key.BN570, new byte[] {2,4});  // Dos columnas calculadas 
		
		// Desglose Casilla 1344
		addBreakdown(Mod2002022BN1344Key.values(), Mod2002022Key.BN1344, new byte[] {2,4});  // Dos columnas calculadas

		// Desglose Casilla 1280
		addBreakdown(Mod2002022BN1280Key.values(), Mod2002022Key.BN1280);

		// Desglose Casilla 572
		addBreakdown(Mod2002022BN572Key.values(), Mod2002022Key.BN572, new byte[] {2,4});  // Dos columnas calculadas
		
		// Desglose Casilla 571
		addBreakdown(Mod2002022BN571Key.values(), Mod2002022Key.BN571, new byte[] {2,4});  // Dos columnas calculadas
		
		// Desglose Casilla 573
		addBreakdown(Mod2002022BN573Key.values(), Mod2002022Key.BN573);		
		
		// Desglose Casilla 585
		addBreakdown(Mod2002022BN585Key.values(), Mod2002022Key.BN585);
		
		// Desglose Casilla 584
		addBreakdown(Mod2002022BN584Key.values(), Mod2002022Key.BN584);

		// Desglose Casilla 590
		addBreakdown(Mod2002022BN590Key.values(), Mod2002022Key.BN590);		
		
		// Desglose Casilla 588
		addBreakdown(Mod2002022BN588Key.values(), Mod2002022Key.BN588, Mod2002022Key.BN634);
		
		// Desglose Casilla 2315
		addBreakdown(Mod2002022BN2315Key.values(), Mod2002022Key.BN2315);  // FALTA - NUEVO DESGLOSE
		
		// Desglose Casilla 1039
		addBreakdown(Mod2002022BN1039Key.values(), Mod2002022Key.BN1039);  // FALTA - NUEVO DESGLOSE - ESTE DESGLOSE TIENE 4 COLUMNAS, NO SÉ SI ALGUNA COLUMNA MAS TAMBIEN ES CALCULADA
		
		// Desglose Casilla 2314
		addBreakdown(Mod2002022BN2314Key.values(), Mod2002022Key.BN2314);  // FALTA - NUEVO DESGLOSE - ESTE DESGLOSE TIENE 4 COLUMNAS, NO SÉ SI ALGUNA COLUMNA MAS TAMBIEN ES CALCULADA
		
		// Desglose Casilla 565
		addBreakdown(Mod2002022BN565Key.values(), Mod2002022Key.BN565);
		addBreakdown(Mod2002022BN565_1Key.values(), Mod2002022Key.BN1689, Mod2002022Key.BN1692, Mod2002022Key.BN1695, Mod2002022Key.BN1698);
		addBreakdown(Mod2002022BN565_2Key.values(), Mod2002022Key.BN1701, Mod2002022Key.BN1704, Mod2002022Key.BN1729, Mod2002022Key.BN1079);
				
		// Desglose Casilla 1040
		addBreakdown(Mod2002022BN1040Key.values(), Mod2002022Key.BN1040, new byte[] {1,3});

		// Desglose Casilla 1041
		addBreakdown(Mod2002022BN1041Key.values(), Mod2002022Key.BN1041, new byte[] {1,3});

		// Desglose Casilla 082
		addBreakdown(Mod2002022BN082Key.values(), Mod2002022Key.BN082, new byte[] {1,3});  // Dos columnas calculadas, ademas 1 de ellas no es la última

		// Totales de los detalles de correcciones al resultado contable
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2305, new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2306, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2301, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2302, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2303, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2304, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2307, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2308, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.I0417B, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.D0418B, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2309, new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.DC2310, new Boolean[]{FALSE,TRUE});
		
	}
	
	static {
		
		//  Limitación en la deducibilidad de gastos financieros.
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1240,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1246,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1248,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1249,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1250,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1251,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1252,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1253,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1260,new Boolean[]{FALSE,TRUE});
		
		// Limitación en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1191,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1196,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1201,new Boolean[]{FALSE,TRUE});
		addBreakdown(Mod2002022LM1212Key.values(), Mod2002022Key.LM1212, new byte[]{});  // No lleva columnas de totales automatica (salvo las 3 primeras filas)
		
		// Pendiente de adición por límite beneficio operativo no aplicado
		addBreakdown(Mod2002022LM538Key.values(), Mod2002022Key.LM538);
		
	}
	
	static {
		
		// Desglose Casilla 1032 (Reserva de capitalización)
		addBreakdown(Mod2002022LQ1032Key.values(), Mod2002022Key.LQ1032);

		// Desglose Casillas 1033 y 1034 (Reserva de nivelación)
		addBreakdown(Mod2002022LQ1033_1Key.values(), Mod2002022Key.LQ1033, new byte[] {1,3});  // Dos columnas calculadas
		addBreakdown(Mod2002022LQ1033_2Key.values(), Mod2002022Key.LQ1158, new byte[]{});      // No lleva columnas de totales
		
	}
	
	static { 
		
		// CONVERSION DE ACTIVOS POR IMPUESTO DIFERIDO EN CREDITO EXIGIBLE FRENTE A LA ADMON. TRIBUTARIA (art. 130, DA 13ª Y DT 33ª LIS)
		
		// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1527,new Boolean[]{FALSE,TRUE});
		addBreakdown(Mod2002022LM1535Key.values(), Mod2002022Key.LM1535);
	
		// Activos por impuesto diferido (AID). Art. 130 LIS
		addBreakdown(Mod2002022LM1561Key.values(), Mod2002022Key.LM1561, new byte[]{});  // No lleva columnas de totales
		
		// Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM393,new Boolean[]{FALSE,TRUE});
		
		// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
		addBreakdown(Mod2002022LM1579Key.values(), Mod2002022Key.LM1579);  
		
	}
	
	static {
		
		// APLICACIÓN DE RESULTADOS
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.ID650, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.ID653, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.ID654, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.ID666, new Boolean[]{TRUE,TRUE});

		// Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
	    // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.LM1500, new Boolean[]{FALSE,TRUE}); // FALTA - COMPROBAR SI REALMENTE VA DESHABILITADO ESTE AÑO
		addBreakdown(Mod2002022LM1494Key.values(), Mod2002022Key.LM1494, new byte[]{});  // No lleva columnas de totales
		
	}
	
	static { 
		
		// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994)
		addBreakdown(Mod2002022RIC_1Key.values(), new Mod2002022Key[] {}); // No lleva fila de totales
		
		// REGIMEN DE COOPERATIVAS - Determinacion de la base imponible
		addBreakdown(Mod2002022LQ554Key.values(), new Mod2002022Key[] {Mod2002022Key.CP0C6, Mod2002022Key.CPC12}, new byte[] {}); // Lleva 2 filas de totales y no lleva columnas de totales
		
		// REGIMEN DE COOPERATIVAS - Desglose Casilla 561 - Detalle de compensación de cuotas
		addBreakdown(Mod2002022LQ561Key.values(), Mod2002022Key.LQ561);
		
	}
	
	static {
		
		// Agrupaciones de interés económico y UTES
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.UT500, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.UT552, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.UT1330,new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// Tributación conjunta al Estado y a las Administraciones Forales del País Vasco y Navarra
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR050, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR626, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR627, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR628, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR629, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR625, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR420, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR421, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR426, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR427, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR600, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR602, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR604, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR606, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR474, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR475, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR476, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR477, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR612, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR616, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1332,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1333,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1881,new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1624,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1625,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1629,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1630,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1587,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1583,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1585,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR494, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR495, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR496, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR497, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR622, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR2480,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR2482,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR2484,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1646,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1647,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1648,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1649,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR2486,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR2488,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1654,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1655,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1656,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1657,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR3242,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1043,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR1044,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR3245,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR3319,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR2491,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002022Key.TR2494,new Boolean[]{FALSE,TRUE});
		
	}
	
	// Añade los totales de los desgloses a BEHAVIOUR_KEYS_MAP, como casillas que no se pueden modificar, pues son calculadas
	// Normalmente los totales van en la última columna y en la última fila, aunque algunos desgloses no cumplen esa regla
	// por eso se indica alguna de las casillas de la fila(s) de los totales para indicar cual es la fila de totales y
	// la columna de total se asume que es la última salvo que venga indicado el parametro totalColumnPosition
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002022Key[] totalRowKey, byte[] totalColumnPosition) {
		
		for (IMod200KeysProvider kp : keysProvider) {			
		
			// Claves que contiene la fila
			IMod200Key[] keys = kp.getKeys();
			
			// Columna(s) calculada(s)
			// Normalmente la última columna es el total (totalColumnPosition=null), salvo que venga cumplimentado totalColumnPosition, 
			// en tal caso se le pasará un array con las posiciones (base 0), que ocupan las columnas calculadas
			// ya que algunos desgloses llevan mas de una columna calculada, o la ultima columna no es calculada, 
			// o no llevan columnas calculadas (en este ultimo caso, pasar un array vacio)
			if (totalColumnPosition == null) {
				// Se asume que la columna del total es la ultima columna
				if (keys[keys.length-1] != null)
					BEHAVIOUR_KEYS_MAP.put((Mod2002022Key)keys[keys.length-1], new Boolean[]{FALSE,TRUE});
			}
			else {
				for (byte position : totalColumnPosition) {
					if (keys[position] != null)
						BEHAVIOUR_KEYS_MAP.put((Mod2002022Key)keys[position], new Boolean[]{FALSE,TRUE});
				}
			}
				
			// Fila(s) de totales
			for (Mod2002022Key totalKey : totalRowKey) {
				if (Arrays.asList(keys).contains(totalKey)) 
					for (IMod200Key key : keys)
						if (key != null)
							BEHAVIOUR_KEYS_MAP.put((Mod2002022Key) key, new Boolean[]{FALSE,TRUE});
			}
		}
				
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002022Key... totalRowKey) {
		addBreakdown(keysProvider, totalRowKey, null);
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002022Key totalRowKey, byte[] totalColumnPosition) {
		addBreakdown(keysProvider, new Mod2002022Key[] {totalRowKey}, totalColumnPosition);
	}	

}



