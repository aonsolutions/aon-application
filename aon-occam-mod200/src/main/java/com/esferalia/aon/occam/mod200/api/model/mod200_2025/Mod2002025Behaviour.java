package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Arrays;
import java.util.EnumMap;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

public class Mod2002025Behaviour {
	
	public static final EnumMap<Mod2002025Key,Boolean[]> BEHAVIOUR_KEYS_MAP = new EnumMap<Mod2002025Key,Boolean[]>(Mod2002025Key.class);
	// Elemento 0 ---> isTitle?    : Hace que aparezca en negrita y la casilla desplazada a la derecha
	// Elemento 1 ---> isDisabled? : Hace que la casilla esté deshabilitada

	static { 
		
		// PARTICIPACIONES
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P1501, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P1502, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P1503, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P1504, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P2376, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P1506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P1809, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P1810, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P1507, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.P1508, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// BALANCE: ACTIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA101, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA102, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA111, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA115, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA118, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA126, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA134, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA135, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA136, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA137, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA138, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA141, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA144, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA149, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA150, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA160, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA168, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA176, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA177, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BA180, new Boolean[]{TRUE,TRUE});
		
	}

	static { 
		
		// BALANCE: PATRIMONIO NETO Y PASIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP185, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP186, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP187, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP190, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP191, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP194, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP195, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP198, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP199, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP200, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP768, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP769, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP201, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP202, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP208, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP209, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP210, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP780, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP781, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP211, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP216, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP223, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP224, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP225, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP226, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP227, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP228, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP785, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP786, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP229, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP230, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP231, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP238, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP239, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP240, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP250, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP251, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BP252, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// CUENTA DE PERDIDAS Y GANANCIAS 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG255, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG705, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG258, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG259, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG260, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG261, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG262, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG770, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG265, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG266, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG270, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG279, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG280, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG284, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG285, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG286, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG287, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG288, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG291, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG791, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG294, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG295, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG296, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG297, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG298, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG301, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG305, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG309, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG312, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG313, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG314, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG319, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG329, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG324, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG325, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG326, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG327, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG328, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.PG500, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0500, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0336, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0339, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0340, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0341, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0342, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0343, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0344, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0345, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0346, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0349, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0350, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0351, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0352, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0353, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0354, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.T0355, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO TOTAL DE CAMBIOS EN EL PATRIMONIO NETO
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC422, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC423, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC424, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC425, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC426, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC428, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC429, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC427, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC430, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC431, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC432, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC433, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC434, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC435, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC464, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC465, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC466, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC467, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC468, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC469, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC470, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC471, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC472, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC475, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC476, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC477, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC507, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC508, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC509, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC510, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC511, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC512, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC513, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC514, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC515, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC516, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC517, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC518, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC519, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC619, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC620, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC621, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC622, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC623, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC624, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC625, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC626, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC627, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC628, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC629, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC630, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC631, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC632, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC633, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC634, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC635, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC636, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC637, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC638, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC639, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC640, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC641, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC643, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC644, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC645, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC393, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC407, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC421, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC449, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC463, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC491, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC505, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC533, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC547, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC561, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC575, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC589, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC603, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC617, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC728, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TC742, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// LIQUIDACION
		
		// Resultado de la cuenta de pérdidas y ganancias
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ500, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ301, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ302, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ501, new Boolean[]{TRUE,TRUE});
		
		// Correcciones al resultado de la cuenta de perdidas y ganancias
		for (Mod2002025CorrectionKey key : Mod2002025CorrectionKey.values()) {
			if (key.getIncrease() != null)
				BEHAVIOUR_KEYS_MAP.put(key.getIncrease(), new Boolean[]{FALSE,TRUE});
			if (key.getDecrease() != null)
				BEHAVIOUR_KEYS_MAP.put(key.getDecrease(), new Boolean[]{FALSE,TRUE});
		}
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.I0417, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.D0418, new Boolean[]{TRUE,TRUE});
		
		// Entidades navieras en régimen de tributación en función del tonelaje
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ578, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ579, new Boolean[]{FALSE,TRUE});
		
        // Base Imponible	
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ550, new Boolean[]{TRUE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1032,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ547,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1887,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1890,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ552, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1033,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1034,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1330,new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ553, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ554, new Boolean[]{FALSE,TRUE});
		
		// Tipo de Gravamen
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ558,new Boolean[]{TRUE,FALSE});
		
		// Sociedades cooperativas
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ561,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1331,new Boolean[]{FALSE,TRUE});
		
		// Cuota integra
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ562, new Boolean[]{TRUE,TRUE});
		
		// Bonificaciones y deducciones. Cuota intega ajustada positiva
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN570,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1344,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1280,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN572,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN571,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN573,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN582,new Boolean[]{TRUE,TRUE});
		
		// Otras deducciones. Cuota líquida
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN585,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN584,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN588,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1039,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN2314,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN2315,new Boolean[]{FALSE,TRUE});  
 		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN565,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN590,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN082,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1040,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1041,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN619, new Boolean[]{TRUE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN592, new Boolean[]{TRUE,TRUE});
		
		// Total retenciones e ingresos a cuenta
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1766, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1784, new Boolean[]{FALSE,TRUE});
		
		// Cuota del ejercicio a ingresar o a devolver
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN599, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN600, new Boolean[]{FALSE,TRUE});
		
		// Pagos fraccionados. Cuota diferencial
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN602, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN604, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN606, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN611, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN612, new Boolean[]{FALSE,TRUE});
		
		// Resultado de la autoliquidación
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN616, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1234B,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1332,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1892,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1042,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1333,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1319,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ031,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1893,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1881,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ466 ,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1586,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1587,new Boolean[]{FALSE,TRUE});
		
		// Rectificativa y Resultado
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1583, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ1585, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN621, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN622, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ866, new Boolean[]{FALSE,TRUE});

		// Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS)
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2480, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2482, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2484, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2485, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2486, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2488, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2489, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ3242, new Boolean[]{FALSE,TRUE});
		
		// Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS)		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM150, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1043,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.BN1044,new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ3243, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ3245, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ3317, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ3319, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ3320, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2491, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2492, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LQ2494, new Boolean[]{FALSE,TRUE});

	}
	
	static {

		// Desglose Casilla 547
		addBreakdown(Mod2002025LQ547Key.values(), Mod2002025Key.LQ547);
		
		// Desglose Casilla 243
		addBreakdown(Mod2002025LQ243Key.values(), Mod2002025Key.LQ1886, Mod2002025Key.LQ1889, Mod2002025Key.LQ243);  // Desglose con dos filas de subtotales y una de totales
		
		// Desglose Casilla 570
		addBreakdown(Mod2002025BN570Key.values(), Mod2002025Key.BN570, new byte[] {2,4});  // Dos columnas calculadas 
		
		// Desglose Casilla 1344
		addBreakdown(Mod2002025BN1344Key.values(), Mod2002025Key.BN1344, new byte[] {2,4});  // Dos columnas calculadas

		// Desglose Casilla 1280
		addBreakdown(Mod2002025BN1280Key.values(), Mod2002025Key.BN1280);

		// Desglose Casilla 572
		addBreakdown(Mod2002025BN572Key.values(), Mod2002025Key.BN572, new byte[] {2,4});  // Dos columnas calculadas
		
		// Desglose Casilla 571
		addBreakdown(Mod2002025BN571Key.values(), Mod2002025Key.BN571, new byte[] {2,4});  // Dos columnas calculadas
		
		// Desglose Casilla 573
		addBreakdown(Mod2002025BN573Key.values(), Mod2002025Key.BN573);		
		
		// Desglose Casilla 585
		addBreakdown(Mod2002025BN585Key.values(), Mod2002025Key.BN585);
		
		// Desglose Casilla 584
		addBreakdown(Mod2002025BN584Key.values(), Mod2002025Key.BN584);

		// Desglose Casilla 590
		addBreakdown(Mod2002025BN590Key.values(), Mod2002025Key.BN590);		
		
		// Desglose Casilla 588
		addBreakdown(Mod2002025BN588Key.values(), Mod2002025Key.BN588, Mod2002025Key.BN634);
		
		// Desglose Casilla 2315
		addBreakdown(Mod2002025BN2315Key.values(), Mod2002025Key.BN2315);  
		
		// Desglose Casilla 1039
		addBreakdown(Mod2002025BN1039Key.values(), Mod2002025Key.BN1039);  
		
		// Desglose Casilla 2314
		addBreakdown(Mod2002025BN2314Key.values(), Mod2002025Key.BN2314);  
		
		// Desglose Casilla 565
		addBreakdown(Mod2002025BN565Key.values(), Mod2002025Key.BN565);
		addBreakdown(Mod2002025BN565_1Key.values(), Mod2002025Key.BN1692, Mod2002025Key.BN1695, Mod2002025Key.BN1698);
		addBreakdown(Mod2002025BN565_2Key.values(), Mod2002025Key.BN1704, Mod2002025Key.BN1729, Mod2002025Key.BN1079);
				
		// Desglose Casilla 1040
		addBreakdown(Mod2002025BN1040Key.values(), Mod2002025Key.BN1040, new byte[] {1,3});

		// Desglose Casilla 1041
		addBreakdown(Mod2002025BN1041Key.values(), Mod2002025Key.BN1041, new byte[] {1,3});

		// Desglose Casilla 082
		addBreakdown(Mod2002025BN082Key.values(), Mod2002025Key.BN082, new byte[] {1,3});  // Dos columnas calculadas, ademas 1 de ellas no es la última
	
	}
	
	static {
		
		//  Limitación en la deducibilidad de gastos financieros.
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1240,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1246,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1248,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1249,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1250,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1251,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1252,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1253,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1260,new Boolean[]{FALSE,TRUE});
		
		// Limitación en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1191,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1196,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1201,new Boolean[]{FALSE,TRUE});
		addBreakdown(Mod2002025LM1212Key.values(), Mod2002025Key.LM1212, new byte[]{});  // No lleva columnas de totales automatica (salvo las 3 primeras filas)
		
		// Pendiente de adición por límite beneficio operativo no aplicado
		addBreakdown(Mod2002025LM538Key.values(), Mod2002025Key.LM538);
		
		// Totales de los detalles de correcciones al resultado contable
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2305, new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2306, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2301, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2302, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2303, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2304, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2307, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2308, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.I0417B, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.D0418B, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2309, new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.DC2310, new Boolean[]{FALSE,TRUE});
		
	}
	
	static {
		
		// Desglose Casilla 1032 (Reserva de capitalización)
		addBreakdown(Mod2002025LQ1032Key.values(), Mod2002025Key.LQ1032);

		// Desglose Casillas 1033 y 1034 (Reserva de nivelación)
		addBreakdown(Mod2002025LQ1033_1Key.values(), Mod2002025Key.LQ1033, new byte[] {1,3});  // Dos columnas calculadas
		addBreakdown(Mod2002025LQ1033_2Key.values(), Mod2002025Key.LQ1158, new byte[]{});      // No lleva columnas de totales
		
	}
	
	static { 
		
		// CONVERSION DE ACTIVOS POR IMPUESTO DIFERIDO EN CREDITO EXIGIBLE FRENTE A LA ADMON. TRIBUTARIA (art. 130, DA 13ª Y DT 33ª LIS)
		
		// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM1527,new Boolean[]{FALSE,TRUE});
		addBreakdown(Mod2002025LM1535Key.values(), Mod2002025Key.LM1535);
	
		// Activos por impuesto diferido (AID). Art. 130 LIS
		addBreakdown(Mod2002025LM1561Key.values(), Mod2002025Key.LM1561, new byte[]{});  // No lleva columnas de totales
		
		// Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM393,new Boolean[]{FALSE,TRUE});
		
		// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
		addBreakdown(Mod2002025LM1579Key.values(), Mod2002025Key.LM1579);  
		
	}
	
	static {
		
		// APLICACIÓN DE RESULTADOS
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.ID650, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.ID653, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.ID654, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.ID666, new Boolean[]{TRUE,TRUE});

		// Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
	    // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.LM3622, new Boolean[]{FALSE,TRUE}); 
		addBreakdown(Mod2002025LM1494Key.values(), Mod2002025Key.LM1494, new byte[]{});  // No lleva columnas de totales
		
		// Reversión de las pérdidas por deterioro de valores representativos de la participación en el capital o en los fondos
		// propios de entidades pendientes de reversión (DT 16ª LIS)
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.RV991, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994)		
		addBreakdown(Mod2002025RIC_1Key.values(), Mod2002025Key.RC3627); // No lleva fila de totales, pero se deshabilita la ultima fila porque es calculada
		
		// REGIMEN DE COOPERATIVAS - Determinacion de la base imponible
		addBreakdown(Mod2002025LQ554Key.values(), new Mod2002025Key[] {Mod2002025Key.CP2837, Mod2002025Key.CP553}, new byte[] {}); // Lleva 2 filas de totales y no lleva columnas de totales
		
		// REGIMEN DE COOPERATIVAS - Desglose Casilla 561 - Detalle de compensación de cuotas
		addBreakdown(Mod2002025LQ561Key.values(), Mod2002025Key.LQ561);
		
		// Régimen especial de la reserva para inversiones en las Illes Balears (DA 70 Ley 31/2022)		
		addBreakdown(Mod2002025RIIB_1Key.values(),Mod2002025Key.RB3640); // No lleva fila de totales, pero la casilla de la última fila se deshabilita porque es calculada
		
	}
	
	static {
		
		// Agrupaciones de interés económico y UTES
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT500, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT552, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1330,new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// Partícipes de agrupaciones de interés económico y UTES
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1279, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1455, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1456, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1458, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1459, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1460, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1461, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1467, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1468, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1523, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1601, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1638, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1639, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1640, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1743, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1909, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1910, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1911, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1912, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.UT1934, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// Tributación conjunta al Estado y a las Administraciones Forales del País Vasco y Navarra
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR050, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR626, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR627, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR628, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR629, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR625, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR420, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR421, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR426, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR427, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR600, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR602, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR604, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR606, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR474, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR475, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR476, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR477, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR612, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR616, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1332,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1333,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1881,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR466, new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1624,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1625,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1629,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1630,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1587,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1583,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1585,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR494, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR495, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR496, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR497, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR622, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR2480,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR2482,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR2484,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1646,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1647,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1648,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1649,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR2486,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR2488,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1654,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1655,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1656,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1657,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR3242,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1043,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR1044,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR3245,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR3319,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR2491,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002025Key.TR2494,new Boolean[]{FALSE,TRUE});
		
	}
	
	// Añade los totales de los desgloses a BEHAVIOUR_KEYS_MAP, como casillas que no se pueden modificar, pues son calculadas
	// Normalmente los totales van en la última columna y en la última fila, aunque algunos desgloses no cumplen esa regla
	// por eso se indica alguna de las casillas de la fila(s) de los totales para indicar cual es la fila de totales y
	// la columna de total se asume que es la última salvo que venga indicado el parametro totalColumnPosition
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002025Key[] totalRowKey, byte[] totalColumnPosition) {
		
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
					BEHAVIOUR_KEYS_MAP.put((Mod2002025Key)keys[keys.length-1], new Boolean[]{FALSE,TRUE});
			}
			else {
				for (byte position : totalColumnPosition) {
					if (keys[position] != null)
						BEHAVIOUR_KEYS_MAP.put((Mod2002025Key)keys[position], new Boolean[]{FALSE,TRUE});
				}
			}
				
			// Fila(s) de totales
			for (Mod2002025Key totalKey : totalRowKey) {
				if (Arrays.asList(keys).contains(totalKey)) 
					for (IMod200Key key : keys)
						if (key != null)
							BEHAVIOUR_KEYS_MAP.put((Mod2002025Key) key, new Boolean[]{FALSE,TRUE});
			}
		}
				
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002025Key... totalRowKey) {
		addBreakdown(keysProvider, totalRowKey, null);
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002025Key totalRowKey, byte[] totalColumnPosition) {
		addBreakdown(keysProvider, new Mod2002025Key[] {totalRowKey}, totalColumnPosition);
	}	

}



