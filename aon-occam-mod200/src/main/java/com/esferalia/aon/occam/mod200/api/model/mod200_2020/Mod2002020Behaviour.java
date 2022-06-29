
package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Arrays;
import java.util.EnumMap;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

public class Mod2002020Behaviour {
	
	public static EnumMap<Mod2002020Key,Boolean[]> BEHAVIOUR_KEYS_MAP = new EnumMap<Mod2002020Key,Boolean[]>(Mod2002020Key.class);
	// Elemento 0 ---> isTitle?    : Hace que aparezca en negrita y la casilla desplazada a la derecha
	// Elemento 1 ---> isDisabled? : Hace que la casilla esté deshabilitada

	static { 
		
		// PARTICIPACIONES
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1501, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1502, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1503, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1504, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1505, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1809, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1810, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1507, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.P1508, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// BALANCE: ACTIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA101, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA102, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA111, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA115, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA118, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA126, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA134, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA135, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA136, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA137, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA138, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA141, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA144, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA149, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA150, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA160, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA168, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA176, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA177, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BA180, new Boolean[]{TRUE,TRUE});
		
	}

	static { 
		
		// BALANCE: PATRIMONIO NETO Y PASIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP185, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP186, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP187, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP190, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP191, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP194, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP195, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP198, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP199, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP200, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP201, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP202, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP208, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP209, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP210, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP211, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP216, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP223, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP224, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP225, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP226, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP227, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP228, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP229, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP230, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP231, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP238, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP239, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP240, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP250, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP251, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BP252, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// BALANCE DE PERDIDAS Y GANANCIAS 
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG255, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG705, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG258, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG259, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG260, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG261, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG262, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG265, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG266, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG270, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG279, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG280, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG284, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG285, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG286, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG287, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG288, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG291, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG294, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG295, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG296, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG297, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG298, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG301, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG305, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG309, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG312, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG313, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG314, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG319, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG329, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG324, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG325, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG326, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG327, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG328, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.PG500, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0500, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0336, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0339, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0340, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0341, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0342, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0343, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0344, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0345, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0346, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0349, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0350, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0351, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0352, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0353, new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0354, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.T0355, new Boolean[]{TRUE,TRUE});
		
	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO TOTAL DE CAMBIOS EN EL PARTRIMONIO NETO
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC422, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC423, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC424, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC425, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC426, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC428, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC429, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC427, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC430, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC431, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC432, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC433, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC434, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC435, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC464, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC465, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC466, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC467, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC468, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC469, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC470, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC471, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC472, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC475, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC476, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC477, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC507, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC508, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC509, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC510, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC511, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC512, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC513, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC514, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC515, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC516, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC517, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC518, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC519, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC619, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC620, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC621, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC622, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC623, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC624, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC625, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC626, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC627, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC628, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC629, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC630, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC631, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC632, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC633, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC634, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC635, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC636, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC637, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC638, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC639, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC640, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC641, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC643, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC644, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC645, new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC393, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC407, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC421, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC449, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC463, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC491, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC505, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC533, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC547, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC561, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC575, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC589, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC603, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC617, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC728, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TC742, new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// LIQUIDACION
		
		// Resultado de la cuenta de pérdidas y ganancias
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ500, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ301, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ302, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ501, new Boolean[]{TRUE,TRUE});
		
		// Correcciones al resultado de la cuenta de perdidas y ganancias
		for (Mod2002020CorrectionKey key : Mod2002020CorrectionKey.values()) {
			if (key.getIncrease() != null)
				BEHAVIOUR_KEYS_MAP.put(key.getIncrease(), new Boolean[]{FALSE,TRUE});
			if (key.getDecrease() != null)
				BEHAVIOUR_KEYS_MAP.put(key.getDecrease(), new Boolean[]{FALSE,TRUE});
		}
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.I0417, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.D0418, new Boolean[]{TRUE,TRUE});
		
		// Entidades navieras en régimen de tributación en función del tonelaje
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ578, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ579, new Boolean[]{FALSE,TRUE});
		
        // Base Imponible	
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ550, new Boolean[]{TRUE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ1032,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ547,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ552, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ1033,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ1034,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ1330,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ553, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ554, new Boolean[]{TRUE,TRUE});
		
		// Tipo de Gravamen - Sociedades cooperativas
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ561,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ1331,new Boolean[]{FALSE,TRUE});
		
		// Cuota integra
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LQ562, new Boolean[]{TRUE,TRUE});
		
		// Bonificaciones y deducciones. Cuota intega ajustada positiva
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN570,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1344,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1280,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN572,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN571,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN573,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN582,new Boolean[]{TRUE,TRUE});
		
		// Otras deducciones. Cuota líquida positiva
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN585,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN584,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN588,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN565,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN590,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN082,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1040,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1041,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN592,new Boolean[]{TRUE,TRUE});
		
		// Cuota del ejercicio a ingresar o a devolver
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN599,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN600, new Boolean[]{FALSE,TRUE});
		
		// Pagos fraccionados. Cuota diferencial
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN602, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN604, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN606, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN611,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN612,new Boolean[]{FALSE,TRUE});
		
		// Liquido a ingresar o a devolver
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN616, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN620, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1234B,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1332,new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1200,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1042,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1333,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN621, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN622, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM150, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1043,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM506, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.BN1044,new Boolean[]{FALSE,TRUE});

	}
	
	static {

		// Desglose Casilla 547
		addBreakdown(Mod2002020LQ547Key.values(), Mod2002020Key.LQ547);
		
		// Desglose Casilla 570
		addBreakdown(Mod2002020BN570Key.values(), Mod2002020Key.BN570, new byte[] {2,4});  // Dos columnas calculadas 
		
		// Desglose Casilla 1344
		addBreakdown(Mod2002020BN1344Key.values(), Mod2002020Key.BN1344, new byte[] {2,4});  // Dos columnas calculadas

		// Desglose Casilla 1280
		addBreakdown(Mod2002020BN1280Key.values(), Mod2002020Key.BN1280);

		// Desglose Casilla 572
		addBreakdown(Mod2002020BN572Key.values(), Mod2002020Key.BN572, new byte[] {2,4});  // Dos columnas calculadas
		
		// Desglose Casilla 571
		addBreakdown(Mod2002020BN571Key.values(), Mod2002020Key.BN571, new byte[] {2,4});  // Dos columnas calculadas
		
		// Desglose Casilla 573
		addBreakdown(Mod2002020BN573Key.values(), Mod2002020Key.BN573);		
		
		// Desglose Casilla 585
		addBreakdown(Mod2002020BN585Key.values(), Mod2002020Key.BN585);
		
		// Desglose Casilla 584
		addBreakdown(Mod2002020BN584Key.values(), Mod2002020Key.BN584);

		// Desglose Casilla 590
		addBreakdown(Mod2002020BN590Key.values(), Mod2002020Key.BN590);		
		
		// Desglose Casilla 588
		addBreakdown(Mod2002020BN588Key.values(), Mod2002020Key.BN588, Mod2002020Key.BN634);
		
		// Desglose Casilla 565
		addBreakdown(Mod2002020BN565Key.values(), Mod2002020Key.BN565);
				
		// Desglose Casilla 1040
		addBreakdown(Mod2002020BN1040Key.values(), Mod2002020Key.BN1040);

		// Desglose Casilla 1041
		addBreakdown(Mod2002020BN1041Key.values(), Mod2002020Key.BN1041);

		// Desglose Casilla 082
		addBreakdown(Mod2002020BN082Key.values(), Mod2002020Key.BN082, new byte[] {1,3});  // Dos columnas calculadas, ademas 1 de ellas no es la última

		// Totales de los detalles de correcciones al resultado contable
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2301, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2302, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2303, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2304, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2305, new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2306, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2307, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2308, new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2309, new Boolean[]{FALSE,TRUE});  
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2310, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.I0417B, new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.D0418B, new Boolean[]{TRUE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2295, new Boolean[]{TRUE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.DC2296, new Boolean[]{TRUE,TRUE});
		
	}
	
	static {
		
		//  Limitación en la deducibilidad de gastos financieros.
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1240,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1246,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1248,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1249,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1250,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1251,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1252,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1253,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1260,new Boolean[]{FALSE,TRUE});
		
		// Limitación en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir
//		addBreakdown(Mod2002020LM1212Key.values(), Mod2002020Key.LM1212);
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1191,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1196,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1201,new Boolean[]{FALSE,TRUE});		

		
		// Pendiente de adición por límite beneficio operativo no aplicado
		addBreakdown(Mod2002020LM538Key.values(), Mod2002020Key.LM538);
		
	}
	
	static {
		
		// Desglose Casilla 1032 (Reserva de capitalización)
		addBreakdown(Mod2002020LQ1032Key.values(), Mod2002020Key.LQ1032);

		// Desglose Casillas 1033 y 1034 (Reserva de nivelación - Reducción en base imponible)
		addBreakdown(Mod2002020LQ1033_1Key.values(), Mod2002020Key.LQ1033);
		
		// Desglose Casillas 1033 y 1034 (Reserva de nivelación - Dotación de la reserva)
		addBreakdown(Mod2002020LQ1033_2Key.values(), Mod2002020Key.LQ1158, new byte[]{});  // No lleva columnas de totales
		
		// Reversión de las pérdidas por deterioro de valores representativos de la participación en el capital o en los fondos
		// propios de entidades pendientes de reversión (DT 16ª LIS).	
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1517,new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// CONVERSION DE ACTIVOS POR IMPUESTO DIFERIDO EN CREDITO EXIGIBLE FRENTE A LA ADMON. TRIBUTARIA (art. 130, DA 13ª Y DT 33ª LIS)
		
		// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM1527,new Boolean[]{FALSE,TRUE});
		addBreakdown(Mod2002020LM1535Key.values(), Mod2002020Key.LM1535);
	
		// Activos por impuesto diferido (AID). Art. 130 LIS
		addBreakdown(Mod2002020LM1561Key.values(), Mod2002020Key.LM1561, new byte[]{});  // No lleva columnas de totales
		
		// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)
		addBreakdown(Mod2002020LM1579Key.values(), Mod2002020Key.LM1579);  
		
	}
	
	static {
		
		// APLICACIÓN DE RESULTADOS
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.ID650, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.ID653, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.ID654, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.ID666, new Boolean[]{FALSE,TRUE});

		// Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
	    // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.LM2436, new Boolean[]{FALSE,TRUE});
		addBreakdown(Mod2002020LM1494Key.values(), Mod2002020Key.LM1494, new byte[]{});  // No lleva columnas de totales
		
	}
	
	static { 
		
		// Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994)
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.RC2437, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.RC093, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.RC048, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.RC527, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.RC925, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.RC996, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.RC2441, new Boolean[]{FALSE,TRUE});
		
		// REGIMEN DE COOPERATIVAS - Determinacion de la base imponible
		addBreakdown(Mod2002020LQ554Key.values(), Mod2002020Key.CP0C6, Mod2002020Key.CPC12, new byte[] {}); // Lleva 2 filas de totales y no lleva columnas de totales
		
		// REGIMEN DE COOPERATIVAS - Desglose Casilla 561 - Detalle de compensación de cuotas
		addBreakdown(Mod2002020LQ561Key.values(), Mod2002020Key.LQ561);
		
	}
	
	static {
		
		// Agrupaciones de interés económico y UTES
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.UT500, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.UT552, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.UT1330,new Boolean[]{FALSE,TRUE});
		
	}
	
	static { 
		
		// Tributación conjunta al Estado y a las Administraciones Forales del País Vasco y Navarra
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR050, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR626, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR627, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR628, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR629, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR625, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR420, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR421, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR426, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR427, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR600, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR602, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR604, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR606, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR474, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR475, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR476, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR477, new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR612, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR616, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR642, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR618, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR620, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR1332,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR1333,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR494, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR495, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR496, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR497, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR622, new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR1043,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002020Key.TR1044,new Boolean[]{FALSE,TRUE});
		
	}
	
	// Añade los totales de los desgloses a BEHAVIOUR_KEYS_MAP, como casillas que no se pueden modificar, pues son calculadas
	// Normalmente los totales van en la última columna y en la última fila, aunque algunos desgloses no cumplen esa regla
	// por eso se indica alguna de las casillas de la fila(s) de los totales para indicar cual es la fila de totales y
	// la columna de total se asume que es la última salvo que venga indicado el parametro positionsColumnsTotal
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002020Key totalRowKey, Mod2002020Key totalRowKey2, byte... positionsColumnsTotal ) {
		
		for (IMod200KeysProvider kp : keysProvider) {			
		
			IMod200Key[] keys = kp.getKeys();
			
			// Columna de totales
			// Normalmente la última columna es el total, salvo que venga cumlimentado positionsColumnsTotal, 
			// en tal caso se le pasará un array con las posiciones (base 0), que ocupan las columnas de los totales
			// ya que algunos desgloses llevan mas de una columna de totales, o la ultima columna no es el tocal, 
			// o no llevan columnas de totales
			if (positionsColumnsTotal == null) {
				if (keys[keys.length-1] != null)
					BEHAVIOUR_KEYS_MAP.put((Mod2002020Key)keys[keys.length-1], new Boolean[]{FALSE,TRUE});
			}
			else {
				for (byte position : positionsColumnsTotal) {
					if (keys[position] != null)
						BEHAVIOUR_KEYS_MAP.put((Mod2002020Key)keys[position], new Boolean[]{FALSE,TRUE});
				}
			}
				
			// Fila(s) de totales 
			if (Arrays.asList(keys).contains(totalRowKey) || (totalRowKey2 != null && Arrays.asList(keys).contains(totalRowKey2)))
				for (IMod200Key key : keys)
					if (key != null)
						BEHAVIOUR_KEYS_MAP.put((Mod2002020Key) key, new Boolean[]{FALSE,TRUE});
		}
				
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002020Key totalRowKey, byte... positionsColumnsTotal) {
		addBreakdown(keysProvider, totalRowKey, null, positionsColumnsTotal);
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002020Key totalRowKey, Mod2002020Key totalRowKey2) {
		addBreakdown(keysProvider, totalRowKey, totalRowKey2, null);
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002020Key totalRowKey) {
		addBreakdown(keysProvider, totalRowKey, null, null);
	}

}



