package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.HashMap;
import java.util.Map;

public class Mod2002014Behaviour {
	public static Map<String,Boolean[]> BEHAVIOUR_KEYS_MAP = new HashMap<String, Boolean[]>();
	// Elemento 0 ---> isTitle?
	// Elemento 1 ---> isDisabled?

	static { // BALANCE: ACTIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA101.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA102.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA111.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA115.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA118.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA126.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA134.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA135.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA136.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA137.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA138.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA141.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA149.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA150.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA160.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA168.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA176.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA177.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BA180.toString(),new Boolean[]{TRUE,TRUE});
	}

	static { // BALANCE: PATRIMONIO NETO Y PASIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP185.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP186.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP187.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP190.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP191.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP194.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP195.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP198.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP199.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP201.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP202.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP208.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP209.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP210.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP211.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP216.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP223.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP224.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP225.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP226.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP227.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP228.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP229.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP230.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP231.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP238.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP239.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP240.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP250.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP251.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BP252.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { // BALANCE DE PERDIDAS Y GANANCIAS 
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG255.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG705.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG258.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG259.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG260.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG265.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG266.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG270.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG279.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG284.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG285.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG286.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG287.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG288.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG291.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG294.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG295.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG296.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG297.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG298.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG301.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG305.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG309.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG312.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG313.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG314.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG319.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG329.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG324.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG325.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG326.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG327.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG328.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.PG500.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0500.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0336.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0339.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0340.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0341.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0342.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0343.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0344.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0345.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0346.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0349.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0350.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0351.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0352.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0353.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0354.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.T0355.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC422.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC423.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC424.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC425.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC426.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC428.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC429.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC427.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC430.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC431.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC432.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC433.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC434.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC435.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC464.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC465.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC466.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC467.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC468.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC469.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC470.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC471.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC472.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC475.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC476.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC477.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC506.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC507.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC508.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC509.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC510.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC511.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC512.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC513.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC514.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC515.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC516.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC517.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC518.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC519.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC618.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC619.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC620.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC621.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC622.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC623.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC624.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC625.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC626.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC627.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC628.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC629.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC630.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC631.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC632.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC633.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC634.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC635.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC636.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC637.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC638.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC639.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC640.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC641.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC642.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC643.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC644.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC645.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC393.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC407.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC421.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC449.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC463.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC491.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC505.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC533.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC547.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC561.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC575.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC589.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC603.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC617.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC728.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.TC742.toString(),new Boolean[]{FALSE,TRUE});
		
	}
	

	static { 	
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ500.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ301.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ302.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ501.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.I0417.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.D0418.toString(),new Boolean[]{TRUE,TRUE});

		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC003.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC004.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC005.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC006.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC027.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC028.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC029.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC030.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC051.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC052.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC053.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.DC054.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ578.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ579.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ547.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ670.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ671.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ548.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ645.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ648.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ651.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ654.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ657.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ660.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ663.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ666.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ669.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ748.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ277.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ610.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ706.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ015.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ727.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ536.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ699.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ671.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ550.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ552.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ553.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ554.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.CP0C6.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.CP0E6.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.CPC12.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.CPE12.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ561.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ678.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ681.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ684.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ687.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ690.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ693.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ672.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ281.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ900.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ100.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ019.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ777.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ909.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ912.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ937.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ694.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ695.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LQ562.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN570.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN116.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN117.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN118.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN696.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN846.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN282.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN702.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN071.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN025.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN714.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN736.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN848.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN284.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN707.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN300.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN027.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN716.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN738.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN571.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN131.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN132.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN133.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN122.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN126.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN130.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN572.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN711.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN637.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN849.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN285.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN825.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN001.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN028.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN717.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN722.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN740.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN135.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN639.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN197.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN287.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN827.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN003.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN030.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN719.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN724.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN742.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN137.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN160.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN161.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN162.toString(),new Boolean[]{FALSE,TRUE});

		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN573.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN166.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN170.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN171.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN172.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN174.toString(),new Boolean[]{FALSE,TRUE});
		
		
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN585.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN837.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN840.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN934.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN299.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN092.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN006.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN033.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN024.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN042.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN140.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN143.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN190.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN805.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN709.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN841.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN585.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN843.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN754.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN757.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN760.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN763.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN746.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN784.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN764.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN765.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN584.toString(),new Boolean[]{FALSE,TRUE});
		

		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN770.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN776.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN782.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN788.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN833.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN897.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN290.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN468.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN586.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN478.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN182.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN533.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN947.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN962.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN186.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN968.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN459.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN462.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN815.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN794.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN797.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN800.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN713.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN507.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN594.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN889.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN809.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN818.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN965.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN751.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN975.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN542.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN903.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN065.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN069.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN073.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN077.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN080.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN087.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN058.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN209.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN215.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN218.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN224.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN245.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN206.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN221.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN230.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN236.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN239.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN016.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN293.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN394.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN407.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN423.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN428.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN431.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN434.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN437.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN440.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN453.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN456.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN830.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN636.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN831.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN832.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN588.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN082.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN083.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN565.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN944.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN296.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN084.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN010.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN036.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN203.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN906.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN992.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN999.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN248.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN995.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN598.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN895.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN590.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN859.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN862.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN865.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN885.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN790.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN801.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN196.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN834.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN873.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN876.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN879.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN882.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN870.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN941.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN193.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN701.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN011.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN039.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN046.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN530.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN146.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN149.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN242.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN806.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN856.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN886.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN887.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN592.toString(),new Boolean[]{FALSE,TRUE});
		
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN582.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN592.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN599.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN600.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN611.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN612.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN621.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.BN622.toString(),new Boolean[]{FALSE,TRUE});
	}
	
	static { // APLICACIÓN DE RESULTADOS
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.ID650.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.ID653.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.ID666.toString(),new Boolean[]{FALSE,TRUE});
	}

	static { // LIMITACIÓN EN LA DEDUCIBILIDAD DE GASTOS FINANCIEROS.
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM175.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM176.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM177.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM178.toString(),new Boolean[]{FALSE,TRUE});

		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM253.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM259.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM043.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM271.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM971.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM263.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM266.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM267.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM268.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM269.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM892.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM523.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM537.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM957.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM538.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM539.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM546.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002014Key.LM344.toString(),new Boolean[]{FALSE,TRUE});
	}


	
}

