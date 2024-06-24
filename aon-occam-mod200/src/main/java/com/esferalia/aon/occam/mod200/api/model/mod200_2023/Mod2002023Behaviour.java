package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Arrays;
import java.util.EnumMap;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

public class Mod2002023Behaviour {
	
	public static final EnumMap<Mod2002023Key,Boolean[]> BEHAVIOUR_KEYS_MAP = new EnumMap<Mod2002023Key,Boolean[]>(Mod2002023Key.class);
	// Elemento 0 ---> isTitle?    : Hace que aparezca en negrita y la casilla desplazada a la derecha
	// Elemento 1 ---> isDisabled? : Hace que la casilla esté deshabilitada

	static { 
		
		// PARTICIPACIONES
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.P1501, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.P1502, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.P1503, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.P1504, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.P1506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.P1809, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.P1810, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.P1507, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.P1508, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// BALANCE: ACTIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA101, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA102, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA111, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA115, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA118, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA126, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA134, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA135, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA136, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA137, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA138, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA141, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA144, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA149, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA150, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA160, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA168, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA176, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA177, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BA180, new Boolean[]{TRUE,TRUE});
		
	}

	static { 
		
		// BALANCE: PATRIMONIO NETO Y PASIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP185, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP186, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP187, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP190, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP191, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP194, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP195, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP198, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP199, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP200, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP201, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP202, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP208, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP209, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP210, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP211, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP216, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP223, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP224, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP225, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP226, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP227, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP228, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP229, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP230, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP231, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP238, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP239, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP240, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP250, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP251, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BP252, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// BALANCE DE PERDIDAS Y GANANCIAS 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG255, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG705, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG258, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG259, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG260, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG261, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG262, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG265, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG266, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG270, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG279, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG280, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG284, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG285, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG286, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG287, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG288, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG291, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG294, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG295, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG296, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG297, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG298, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG301, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG305, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG309, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG312, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG313, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG314, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG319, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG329, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG324, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG325, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG326, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG327, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG328, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.PG500, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0500, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0336, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0339, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0340, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0341, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0342, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0343, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0344, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0345, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0346, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0349, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0350, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0351, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0352, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0353, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0354, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.T0355, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO TOTAL DE CAMBIOS EN EL PATRIMONIO NETO
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC422, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC423, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC424, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC425, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC426, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC428, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC429, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC427, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC430, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC431, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC432, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC433, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC434, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC435, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC464, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC465, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC466, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC467, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC468, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC469, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC470, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC471, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC472, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC475, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC476, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC477, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC507, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC508, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC509, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC510, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC511, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC512, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC513, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC514, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC515, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC516, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC517, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC518, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC519, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC619, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC620, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC621, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC622, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC623, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC624, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC625, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC626, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC627, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC628, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC629, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC630, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC631, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC632, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC633, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC634, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC635, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC636, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC637, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC638, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC639, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC640, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC641, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC643, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC644, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC645, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC393, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC407, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC421, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC449, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC463, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC491, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC505, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC533, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC547, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC561, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC575, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC589, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC603, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC617, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC728, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TC742, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// LIQUIDACION
		
		// Resultado de la cuenta de pérdidas y ganancias
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ500, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ301, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ302, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ501, new Boolean[]{TRUE,TRUE});
		
		// Correcciones al resultado de la cuenta de perdidas y ganancias
		for (Mod2002023CorrectionKey key : Mod2002023CorrectionKey.values()) {
			if (key.getIncrease() != null)
				BEHAVIOUR_KEYS_MAP.put(key.getIncrease(), new Boolean[]{FALSE,TRUE});
			if (key.getDecrease() != null)
				BEHAVIOUR_KEYS_MAP.put(key.getDecrease(), new Boolean[]{FALSE,TRUE});
		}
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.I0417, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.D0418, new Boolean[]{TRUE,TRUE});
		
		// Entidades navieras en régimen de tributación en función del tonelaje
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ578, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ579, new Boolean[]{FALSE,TRUE});
		
        // Base Imponible	
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ550, new Boolean[]{TRUE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1032,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ547,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1887,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1890,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ552, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1033,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1034,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1330,new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ553, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ554, new Boolean[]{FALSE,TRUE});
		
		// Tipo de Gravamen
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ558,new Boolean[]{TRUE,FALSE});
		
		// Sociedades cooperativas
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ561,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1331,new Boolean[]{FALSE,TRUE});
		
		// Cuota integra
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ562, new Boolean[]{TRUE,TRUE});
		
		// Bonificaciones y deducciones. Cuota intega ajustada positiva
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN570,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1344,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1280,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN572,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN571,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN573,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN582,new Boolean[]{TRUE,TRUE});
		
		// Otras deducciones. Cuota líquida
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN585,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN584,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN588,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1039,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN2314,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN2315,new Boolean[]{FALSE,TRUE});  
 		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN565,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN590,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN082,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1040,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1041,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN619, new Boolean[]{TRUE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN592, new Boolean[]{TRUE,TRUE});
		
		// Total retenciones e ingresos a cuenta
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1766, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1784, new Boolean[]{FALSE,TRUE});
		
		// Cuota del ejercicio a ingresar o a devolver
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN599, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN600, new Boolean[]{FALSE,TRUE});
		
		// Pagos fraccionados. Cuota diferencial
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN602, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN604, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN606, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN611, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN612, new Boolean[]{FALSE,TRUE});
		
		// Resultado de la autoliquidación
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN616, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1234B,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1332,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1892,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1042,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1333,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1319,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1893,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1881,new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1586, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1587, new Boolean[]{FALSE,TRUE});
		
		// Liquido a ingresar o a devolver
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1583, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ1585, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN621, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN622, new Boolean[]{FALSE,TRUE});		

		// Opción de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS)
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2480, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2482, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2484, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2485, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2486, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2488, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2489, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ3242, new Boolean[]{FALSE,TRUE});
		
		// Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (art. 130 LIS)		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM150, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1043,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.BN1044,new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ3243, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ3245, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ3317, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ3319, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ3320, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2491, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2492, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LQ2494, new Boolean[]{FALSE,TRUE});

	}
	
	static {

		// Desglose Casilla 547
		addBreakdown(Mod2002023LQ547Key.values(), Mod2002023Key.LQ547);
		
		// Desglose Casilla 243
		addBreakdown(Mod2002023LQ243Key.values(), Mod2002023Key.LQ1886, Mod2002023Key.LQ1889, Mod2002023Key.LQ243);  // Desglose con dos filas de subtotales y una de totales
		
		// Desglose Casilla 570
		addBreakdown(Mod2002023BN570Key.values(), Mod2002023Key.BN570, new byte[] {2,4});  // Dos columnas calculadas 
		
		// Desglose Casilla 1344
		addBreakdown(Mod2002023BN1344Key.values(), Mod2002023Key.BN1344, new byte[] {2,4});  // Dos columnas calculadas

		// Desglose Casilla 1280
		addBreakdown(Mod2002023BN1280Key.values(), Mod2002023Key.BN1280);

		// Desglose Casilla 572
		addBreakdown(Mod2002023BN572Key.values(), Mod2002023Key.BN572, new byte[] {2,4});  // Dos columnas calculadas
		
		// Desglose Casilla 571
		addBreakdown(Mod2002023BN571Key.values(), Mod2002023Key.BN571, new byte[] {2,4});  // Dos columnas calculadas
		
		// Desglose Casilla 573
		addBreakdown(Mod2002023BN573Key.values(), Mod2002023Key.BN573);		
		
		// Desglose Casilla 585
		addBreakdown(Mod2002023BN585Key.values(), Mod2002023Key.BN585);
		
		// Desglose Casilla 584
		addBreakdown(Mod2002023BN584Key.values(), Mod2002023Key.BN584);

		// Desglose Casilla 590
		addBreakdown(Mod2002023BN590Key.values(), Mod2002023Key.BN590);		
		
		// Desglose Casilla 588
		addBreakdown(Mod2002023BN588Key.values(), Mod2002023Key.BN588, Mod2002023Key.BN634);
		
		// Desglose Casilla 2315
		addBreakdown(Mod2002023BN2315Key.values(), Mod2002023Key.BN2315);  
		
		// Desglose Casilla 1039
		addBreakdown(Mod2002023BN1039Key.values(), Mod2002023Key.BN1039);  
		
		// Desglose Casilla 2314
		addBreakdown(Mod2002023BN2314Key.values(), Mod2002023Key.BN2314);  
		
		// Desglose Casilla 565
		addBreakdown(Mod2002023BN565Key.values(), Mod2002023Key.BN565);
		addBreakdown(Mod2002023BN565_1Key.values(), Mod2002023Key.BN1689, Mod2002023Key.BN1692, Mod2002023Key.BN1695, Mod2002023Key.BN1698);
		addBreakdown(Mod2002023BN565_2Key.values(), Mod2002023Key.BN1701, Mod2002023Key.BN1704, Mod2002023Key.BN1729, Mod2002023Key.BN1079);
				
		// Desglose Casilla 1040
		addBreakdown(Mod2002023BN1040Key.values(), Mod2002023Key.BN1040, new byte[] {1,3});

		// Desglose Casilla 1041
		addBreakdown(Mod2002023BN1041Key.values(), Mod2002023Key.BN1041, new byte[] {1,3});

		// Desglose Casilla 082
		addBreakdown(Mod2002023BN082Key.values(), Mod2002023Key.BN082, new byte[] {1,3});  // Dos columnas calculadas, ademas 1 de ellas no es la última
	
	}
	
	static {
		
		//  Limitación en la deducibilidad de gastos financieros.
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1240,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1246,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1248,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1249,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1250,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1251,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1252,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1253,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1260,new Boolean[]{FALSE,TRUE});
		
		// Limitación en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1191,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1196,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1201,new Boolean[]{FALSE,TRUE});
		addBreakdown(Mod2002023LM1212Key.values(), Mod2002023Key.LM1212, new byte[]{});  // No lleva columnas de totales automatica (salvo las 3 primeras filas)
		
		// Pendiente de adición por límite beneficio operativo no aplicado
		addBreakdown(Mod2002023LM538Key.values(), Mod2002023Key.LM538);
		
		// Totales de los detalles de correcciones al resultado contable
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2305, new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2306, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2301, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2302, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2303, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2304, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2307, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2308, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.I0417B, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.D0418B, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2309, new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.DC2310, new Boolean[]{FALSE,TRUE});
		
	}
	
	static {
		
		// Desglose Casilla 1032 (Reserva de capitalización)
		addBreakdown(Mod2002023LQ1032Key.values(), Mod2002023Key.LQ1032);

		// Desglose Casillas 1033 y 1034 (Reserva de nivelación)
		addBreakdown(Mod2002023LQ1033_1Key.values(), Mod2002023Key.LQ1033, new byte[] {1,3});  // Dos columnas calculadas
		addBreakdown(Mod2002023LQ1033_2Key.values(), Mod2002023Key.LQ1158, new byte[]{});      // No lleva columnas de totales
		
	}
	
	static { 
		
		// CONVERSION DE ACTIVOS POR IMPUESTO DIFERIDO EN CREDITO EXIGIBLE FRENTE A LA ADMON. TRIBUTARIA (art. 130, DA 13ª Y DT 33ª LIS)
		
		// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM1527,new Boolean[]{FALSE,TRUE});
		addBreakdown(Mod2002023LM1535Key.values(), Mod2002023Key.LM1535);
	
		// Activos por impuesto diferido (AID). Art. 130 LIS
		addBreakdown(Mod2002023LM1561Key.values(), Mod2002023Key.LM1561, new byte[]{});  // No lleva columnas de totales
		
		// Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM393,new Boolean[]{FALSE,TRUE});
		
		// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
		addBreakdown(Mod2002023LM1579Key.values(), Mod2002023Key.LM1579);  
		
	}
	
	static {
		
		// APLICACIÓN DE RESULTADOS
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.ID650, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.ID653, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.ID654, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.ID666, new Boolean[]{TRUE,TRUE});

		// Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
	    // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.LM2806, new Boolean[]{FALSE,TRUE}); 
		addBreakdown(Mod2002023LM1494Key.values(), Mod2002023Key.LM1494, new byte[]{});  // No lleva columnas de totales
		
	}
	
	static { 
		
		// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994)		
		addBreakdown(Mod2002023RIC_1Key.values()); // No lleva fila de totales
		
		// REGIMEN DE COOPERATIVAS - Determinacion de la base imponible
		addBreakdown(Mod2002023LQ554Key.values(), new Mod2002023Key[] {Mod2002023Key.CP2837, Mod2002023Key.CP553}, new byte[] {}); // Lleva 2 filas de totales y no lleva columnas de totales
		
		// REGIMEN DE COOPERATIVAS - Desglose Casilla 561 - Detalle de compensación de cuotas
		addBreakdown(Mod2002023LQ561Key.values(), Mod2002023Key.LQ561);
		
		// Régimen especial de la reserva para inversiones en las Illes Balears (DA 70 Ley 31/2022)		
		addBreakdown(Mod2002023RIIB_1Key.values()); // No lleva fila de totales
		
	}
	
	static {
		
		// Agrupaciones de interés económico y UTES
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT500, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT552, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1330,new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// Partícipes de agrupaciones de interés económico y UTES (cumplimentación voluntaria)
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1279, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1455, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1456, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1458, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1459, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1460, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1461, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1467, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1468, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1523, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1601, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1638, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1639, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1640, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1743, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1909, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1910, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1911, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1912, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.UT1934, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// Tributación conjunta al Estado y a las Administraciones Forales del País Vasco y Navarra
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR050, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR626, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR627, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR628, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR629, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR625, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR420, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR421, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR426, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR427, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR600, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR602, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR604, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR606, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR474, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR475, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR476, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR477, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR612, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR616, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1332,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1333,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1881,new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1624,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1625,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1629,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1630,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1587,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1583,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1585,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR494, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR495, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR496, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR497, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR622, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR2480,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR2482,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR2484,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1646,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1647,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1648,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1649,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR2486,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR2488,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1654,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1655,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1656,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1657,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR3242,new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1043,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR1044,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR3245,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR3319,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR2491,new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002023Key.TR2494,new Boolean[]{FALSE,TRUE});
		
	}
	
	// Añade los totales de los desgloses a BEHAVIOUR_KEYS_MAP, como casillas que no se pueden modificar, pues son calculadas
	// Normalmente los totales van en la última columna y en la última fila, aunque algunos desgloses no cumplen esa regla
	// por eso se indica alguna de las casillas de la fila(s) de los totales para indicar cual es la fila de totales y
	// la columna de total se asume que es la última salvo que venga indicado el parametro totalColumnPosition
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002023Key[] totalRowKey, byte[] totalColumnPosition) {
		
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
					BEHAVIOUR_KEYS_MAP.put((Mod2002023Key)keys[keys.length-1], new Boolean[]{FALSE,TRUE});
			}
			else {
				for (byte position : totalColumnPosition) {
					if (keys[position] != null)
						BEHAVIOUR_KEYS_MAP.put((Mod2002023Key)keys[position], new Boolean[]{FALSE,TRUE});
				}
			}
				
			// Fila(s) de totales
			for (Mod2002023Key totalKey : totalRowKey) {
				if (Arrays.asList(keys).contains(totalKey)) 
					for (IMod200Key key : keys)
						if (key != null)
							BEHAVIOUR_KEYS_MAP.put((Mod2002023Key) key, new Boolean[]{FALSE,TRUE});
			}
		}
				
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002023Key... totalRowKey) {
		addBreakdown(keysProvider, totalRowKey, null);
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002023Key totalRowKey, byte[] totalColumnPosition) {
		addBreakdown(keysProvider, new Mod2002023Key[] {totalRowKey}, totalColumnPosition);
	}	

}



