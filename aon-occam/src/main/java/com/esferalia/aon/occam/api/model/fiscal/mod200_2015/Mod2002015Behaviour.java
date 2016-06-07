package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.HashMap;
import java.util.Map;

public class Mod2002015Behaviour {
	public static Map<String,Boolean[]> BEHAVIOUR_KEYS_MAP = new HashMap<String, Boolean[]>();
	// Elemento 0 ---> isTitle?    : Hace que aparezca en negrita y la casilla desplazada a la derecha
	// Elemento 1 ---> isDisabled? : Hace que la casilla esté deshabilitada

	static { // BALANCE: ACTIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA101.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA102.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA111.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA115.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA118.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA126.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA134.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA135.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA136.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA137.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA138.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA141.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA149.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA150.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA160.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA168.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA176.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA177.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BA180.toString(),new Boolean[]{TRUE,TRUE});
	}

	static { // BALANCE: PATRIMONIO NETO Y PASIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP185.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP186.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP187.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP190.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP191.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP194.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP195.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP198.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP199.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP201.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP202.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP208.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP209.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP210.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP211.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP216.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP223.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP224.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP225.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP226.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP227.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP228.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP229.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP230.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP231.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP238.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP239.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP240.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP250.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP251.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BP252.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { // BALANCE DE PERDIDAS Y GANANCIAS 
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG255.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG705.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG258.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG259.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG260.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG261.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG262.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG265.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG266.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG270.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG279.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG284.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG285.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG286.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG287.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG288.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG291.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG294.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG295.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG296.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG297.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG298.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG301.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG305.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG309.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG312.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG313.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG314.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG319.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG329.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG324.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG325.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG326.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG327.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG328.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.PG500.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0500.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0336.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0339.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0340.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0341.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0342.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0343.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0344.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0345.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0346.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0349.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0350.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0351.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0352.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0353.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0354.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.T0355.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC422.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC423.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC424.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC425.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC426.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC428.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC429.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC427.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC430.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC431.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC432.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC433.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC434.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC435.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC464.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC465.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC466.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC467.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC468.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC469.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC470.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC471.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC472.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC475.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC476.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC477.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC506.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC507.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC508.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC509.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC510.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC511.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC512.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC513.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC514.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC515.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC516.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC517.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC518.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC519.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC618.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC619.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC620.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC621.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC622.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC623.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC624.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC625.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC626.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC627.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC628.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC629.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC630.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC631.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC632.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC633.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC634.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC635.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC636.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC637.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC638.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC639.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC640.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC641.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC642.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC643.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC644.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC645.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC393.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC407.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC421.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC449.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC463.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC491.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC505.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC533.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC547.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC561.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC575.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC589.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC603.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC617.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC728.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.TC742.toString(),new Boolean[]{FALSE,TRUE});
		
	}
	

	static { 	
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ500.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ301.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ302.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ501.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.I0417.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.D0418.toString(),new Boolean[]{TRUE,TRUE});

		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC003.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC004.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC005.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC006.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC027.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC028.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC029.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC030.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC051.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC052.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC053.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.DC054.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ578.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ579.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ547.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ670.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ671.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ548.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ645.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ648.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ651.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ654.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ657.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ660.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ663.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ666.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ669.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ748.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ277.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ610.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ706.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ015.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ727.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ536.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ699.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ1047.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ671.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ550.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ552.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ553.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ554.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.CP0C6.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.CP0E6.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.CPC12.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.CPE12.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ561.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ678.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ681.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ684.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ687.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ690.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ693.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ672.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ281.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ900.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ100.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ019.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ777.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ909.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ912.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ937.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ694.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ695.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LQ562.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN570.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN116.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN117.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN118.toString(),new Boolean[]{FALSE,TRUE});
		
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN696.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN846.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN282.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN702.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN071.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN025.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN714.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN736.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN848.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN284.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN707.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN300.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN027.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN716.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN738.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN571.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN131.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN132.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN133.toString(),new Boolean[]{FALSE,TRUE});
		
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN122.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN126.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN130.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN572.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN711.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN637.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN849.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN285.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN825.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN001.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN028.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN717.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN722.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN740.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN135.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN639.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN197.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN287.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN827.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN003.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN030.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN719.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN724.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN742.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN137.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN160.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN161.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN162.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1051.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1053.toString(),new Boolean[]{FALSE,TRUE});

		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN573.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN166.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN170.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN171.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN172.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN174.toString(),new Boolean[]{FALSE,TRUE});
		
		
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN585.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN837.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN840.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN934.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN299.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN092.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN006.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN033.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN024.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN042.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN140.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN143.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN190.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN805.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1057.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN709.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN841.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN585.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN843.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN754.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN757.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN760.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN763.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN746.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN784.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN764.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN765.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN584.toString(),new Boolean[]{FALSE,TRUE});
		

		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN770.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN776.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN782.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN788.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN833.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN897.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN290.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN468.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN586.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN478.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN182.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN533.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN947.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN962.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN186.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN968.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN459.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN462.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN815.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN797.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN800.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN713.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1074.toString(),new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN507.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN594.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN809.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN965.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN751.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN073.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN080.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN087.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN209.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN218.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN206.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN221.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN230.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN239.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN016.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN293.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN423.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN428.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN431.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN434.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN437.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN440.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN830.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN636.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN831.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1065.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1068.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1071.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1077.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1080.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1083.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1086.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1089.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1092.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1095.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1098.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1101.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1104.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1107.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1110.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1113.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1116.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1119.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1122.toString(),new Boolean[]{FALSE,TRUE});
		
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN832.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN588.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN082.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN919.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN976.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN823.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN233.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1124.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1128.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN517.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN081.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN082.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN580.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN978.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN231.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN851.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1126.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1130.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN083.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN565.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN944.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN296.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN084.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN010.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN036.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN203.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN906.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN992.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN999.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN248.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN995.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN598.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN895.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN590.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN859.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN862.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN865.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN885.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN790.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN801.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN196.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN834.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN873.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN876.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN879.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN882.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN870.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN941.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN193.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN701.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN011.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN039.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN046.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN530.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN146.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN149.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN242.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN1060.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN806.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN856.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN886.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN887.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN592.toString(),new Boolean[]{FALSE,TRUE});
		
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN582.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN592.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN599.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN600.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN611.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN612.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN621.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.BN622.toString(),new Boolean[]{FALSE,TRUE});
	}
	
	static { // APLICACIÓN DE RESULTADOS
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.ID650.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.ID653.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.ID666.toString(),new Boolean[]{FALSE,TRUE});
	}

	static { // LIMITACIÓN EN LA DEDUCIBILIDAD DE GASTOS FINANCIEROS.
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM175.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM176.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM177.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM178.toString(),new Boolean[]{FALSE,TRUE});
//
//		
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM253.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM259.toString(),new Boolean[]{FALSE,TRUE});
//		
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM043.toString(),new Boolean[]{FALSE,TRUE});
//		
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM271.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM971.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM263.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM266.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM267.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM268.toString(),new Boolean[]{FALSE,TRUE});
//		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM269.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM892.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM523.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM537.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM957.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM538.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM539.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM546.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002015Key.LM344.toString(),new Boolean[]{FALSE,TRUE});
	}


	
}

