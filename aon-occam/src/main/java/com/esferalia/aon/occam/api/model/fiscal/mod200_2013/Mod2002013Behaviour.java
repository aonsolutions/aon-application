package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.HashMap;
import java.util.Map;

public class Mod2002013Behaviour {
	public static Map<String,Boolean[]> BEHAVIOUR_KEYS_MAP = new HashMap<String, Boolean[]>();
	// Elemento 0 ---> isTitle?
	// Elemento 1 ---> isDisabled?

	static { // BALANCE: ACTIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA101.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA102.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA111.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA115.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA118.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA126.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA134.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA135.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA136.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA137.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA138.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA141.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA149.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA150.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA160.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA168.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA176.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA177.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BA180.toString(),new Boolean[]{TRUE,TRUE});
	}

	static { // BALANCE: PATRIMONIO NETO Y PASIVO
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP185.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP186.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP187.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP190.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP191.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP194.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP195.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP198.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP199.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP201.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP202.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP208.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP209.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP210.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP211.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP216.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP223.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP224.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP225.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP226.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP227.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP228.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP229.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP230.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP231.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP238.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP239.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP240.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP250.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP251.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BP252.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { // BALANCE DE PERDIDAS Y GANANCIAS 
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG255.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG705.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG258.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG259.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG260.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG265.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG266.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG270.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG279.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG284.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG285.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG286.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG287.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG288.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG291.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG294.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG295.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG296.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG297.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG298.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG301.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG305.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG309.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG312.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG313.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG314.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG319.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG329.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG324.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG325.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG326.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG327.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG328.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.PG500.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0500.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0336.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0339.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0340.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0341.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0342.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0343.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0344.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0345.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0346.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0349.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0350.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0351.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0352.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0353.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0354.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.T0355.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC422.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC423.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC424.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC425.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC426.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC428.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC429.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC427.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC430.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC431.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC432.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC433.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC434.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC435.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC464.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC465.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC466.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC467.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC468.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC469.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC470.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC471.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC472.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC475.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC476.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC477.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC506.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC507.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC508.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC509.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC510.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC511.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC512.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC513.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC514.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC515.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC516.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC517.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC518.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC519.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC618.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC619.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC620.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC621.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC622.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC623.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC624.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC625.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC626.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC627.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC628.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC629.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC630.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC631.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC632.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC633.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC634.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC635.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC636.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC637.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC638.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC639.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC640.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC641.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC642.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC643.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC644.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC645.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC393.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC407.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC421.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC449.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC463.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC491.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC505.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC533.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC547.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC561.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC575.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC589.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC603.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC617.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC728.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.TC742.toString(),new Boolean[]{FALSE,TRUE});
		
	}
	

	static { 	
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ500.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ501.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.I0417.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.D0418.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ578.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ579.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ547.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ670.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ671.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ548.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ645.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ648.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ651.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ654.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ657.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ660.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ663.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ666.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ669.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ748.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ277.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ610.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ706.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ015.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ727.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ536.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ671.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ550.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ552.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ553.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ554.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.CP0C6.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.CP0E6.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.CPC12.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.CPE12.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ561.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ678.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ681.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ684.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ687.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ690.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ693.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ672.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ281.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ900.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ100.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ019.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ777.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ909.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ912.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ937.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ694.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ695.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LQ562.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN570.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN116.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN117.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN118.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN848.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN284.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN707.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN300.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN027.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN716.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN738.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN571.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN131.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN132.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN133.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN122.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN126.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN130.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN572.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN573.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN585.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN584.toString(),new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN588.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN082.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN590.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN592.toString(),new Boolean[]{FALSE,TRUE});
		
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN582.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN592.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN599.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN600.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN611.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN612.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN621.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.BN622.toString(),new Boolean[]{FALSE,TRUE});
	}
	
	static { // APLICACIÓN DE RESULTADOS
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.ID650.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.ID653.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.ID666.toString(),new Boolean[]{FALSE,TRUE});
	}

	static { // LIMITACIÓN EN LA DEDUCIBILIDAD DE GASTOS FINANCIEROS.
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM175.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM176.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM177.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM178.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM253.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM259.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM043.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM971.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM263.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM266.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM267.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM268.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM269.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM523.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM272.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM537.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM538.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM539.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM546.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod2002013Key.LM344.toString(),new Boolean[]{FALSE,TRUE});
	}


	
}

