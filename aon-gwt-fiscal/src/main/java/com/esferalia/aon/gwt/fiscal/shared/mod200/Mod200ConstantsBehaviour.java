package com.esferalia.aon.gwt.fiscal.shared.mod200;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;

import java.util.HashMap;
import java.util.Map;

public class Mod200ConstantsBehaviour {
	public static Map<String,Boolean[]> BEHAVIOUR_KEYS_MAP = new HashMap<String, Boolean[]>();
	// Elemento 0 ---> isTitle?
	// Elemento 1 ---> isDisabled?

	static { // BALANCE: ACTIVO
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA101.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA102.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA111.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA115.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA118.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA126.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA134.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA135.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA136.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA137.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA138.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA141.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA149.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA160.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA168.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA176.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA177.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BA180.toString(),new Boolean[]{TRUE,TRUE});
	}

	static { // BALANCE: PATRIMONIO NETO Y PASIVO
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP185.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP186.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP187.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP190.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP191.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP194.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP195.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP198.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP199.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP201.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP202.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP208.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP209.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP210.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP211.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP216.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP223.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP224.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP225.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP226.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP227.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP228.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP229.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP230.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP231.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP238.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP239.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP250.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP251.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BP252.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { // BALANCE DE PERDIDAS Y GANANCIAS 
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG255.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG705.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG258.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG259.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG260.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG265.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG266.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG270.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG279.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG284.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG285.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG286.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG287.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG288.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG291.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG294.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG295.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG296.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG297.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG298.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG301.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG305.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG309.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG312.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG313.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG314.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG319.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG329.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG324.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG325.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG326.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG327.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG328.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.PG500.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0500.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0336.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0339.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0340.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0341.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0342.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0343.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0344.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0345.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0346.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0349.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0350.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0351.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0352.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0353.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0354.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.T0355.toString(),new Boolean[]{TRUE,TRUE});
	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC422.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC423.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC424.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC425.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC426.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC428.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC429.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC427.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC430.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC431.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC432.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC433.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC434.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC435.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC464.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC465.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC466.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC467.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC468.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC469.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC470.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC471.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC472.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC475.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC476.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC477.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC506.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC507.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC508.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC509.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC510.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC511.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC512.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC513.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC514.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC515.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC516.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC517.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC518.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC519.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC618.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC619.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC620.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC621.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC622.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC623.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC624.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC625.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC626.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC627.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC628.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC629.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC630.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC631.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC632.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC633.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC634.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC635.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC636.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC637.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC638.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC639.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC640.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC641.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC642.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC643.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC644.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC645.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC393.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC407.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC421.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC449.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC463.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC491.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC505.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC533.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC547.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC561.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC575.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC589.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC603.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC617.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC728.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.TC742.toString(),new Boolean[]{FALSE,TRUE});
		
	}
	

	static { 	
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ500.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ501.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.I0417.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.D0418.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ670.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ547.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ671.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ548.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ645.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ648.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ651.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ654.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ657.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ660.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ663.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ666.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ669.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ748.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ277.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ610.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ706.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ015.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ727.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ536.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ671.toString(),new Boolean[]{TRUE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ552.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.LQ562.toString(),new Boolean[]{TRUE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN570.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN116.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN117.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN118.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN848.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN284.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN707.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN300.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN027.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN716.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN738.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN571.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN131.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN132.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN133.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN122.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN126.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN130.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN572.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN573.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN585.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN584.toString(),new Boolean[]{FALSE,TRUE}); 
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN588.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN082.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN590.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN592.toString(),new Boolean[]{FALSE,TRUE});
		
		
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN582.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN592.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN599.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN611.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(Mod200Key.BN621.toString(),new Boolean[]{TRUE,FALSE});

	}
}

