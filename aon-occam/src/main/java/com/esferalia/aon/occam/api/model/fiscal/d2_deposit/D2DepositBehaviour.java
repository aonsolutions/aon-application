package com.esferalia.aon.occam.api.model.fiscal.d2_deposit;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.HashMap;
import java.util.Map;


public class D2DepositBehaviour {
	public static Map<String,Boolean[]> BEHAVIOUR_KEYS_MAP = new HashMap<String, Boolean[]>();
	// Elemento 0 ---> isTitle?
	// Elemento 1 ---> isDisabled?


	static { // APARTADO 3
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA391000.getCode(),new Boolean[]{FALSE,FALSE});
	}

	static { // APARTADO 5
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592001.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592031.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592041.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592081.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592091.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592131.getCode(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920019.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920319.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920419.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920819.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920919.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5921319.getCode(),new Boolean[]{TRUE,FALSE});
	}
	
	static{ // APARTADO 6
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193201.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932019.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193202.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932029.getCode(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193211.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932119.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193213.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932139.getCode(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193221.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932219.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193223.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932239.getCode(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193231.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932319.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193232.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932329.getCode(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193243.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932439.getCode(),new Boolean[]{FALSE,TRUE});
		
	}
	
	static{ // APARTADO 7
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA794301.getCode(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA794302.getCode(),new Boolean[]{FALSE,TRUE});
	}
	
	static{ // APARTADO 10
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095000.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095006.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095012.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095016.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095019.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095020.getCode(),new Boolean[]{TRUE,FALSE});
	}
	
	static{ // APARTADO 11

	}
	
	static{ // APARTADO 12
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B97301.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B97331.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B97441.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B97501.getCode(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B973019.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B973319.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B974419.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B975019.getCode(),new Boolean[]{TRUE,FALSE});

	}
	
	static{ // APARTADO 13

	}
	
	static{ // APARTADO 14

	
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14199004.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14199014.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14199015.getCode(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294600.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294603.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294604.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294607.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294608.getCode(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294613.getCode(),new Boolean[]{TRUE,FALSE});
	}
	
	static{ // APARTADO 15

	}
	
	static{		// BALANCE: ACTIVO
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111000.toString() ,new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA1110009.toString() ,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111300.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111500.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111700.toString() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112000.toString() ,new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA1120009.toString() ,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112300.toString() ,new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA1123009.toString() ,new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112380.toString(), new Boolean[]{FALSE, TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA1123809.toString(), new Boolean[]{FALSE, TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112500.toString() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112700.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA110000.toString() ,new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA1100009.toString() ,new Boolean[]{FALSE,TRUE});
		
	}
		
	static{		// BALANCE: PATRIMONIO NETO Y PASIVO
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2120000.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA21200009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121000.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA21210009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121100.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA21211009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121300.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA21213009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121500.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121700.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121800.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121900.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA21219009.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212190098.toString(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121900.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2122000.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2123000.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131000.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA21310009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131200.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA21312009.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131300.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131500.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131700.toString(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232000.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA22320009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232300.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA22323009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232500.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA22325009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232700.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2230000.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA22300009.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232580.toString(), new Boolean[]{FALSE, TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA22325809.toString(), new Boolean[]{FALSE, TRUE});
	}
	
	static{ 	// CUENTA DE PÉRDIDAS Y GANANCIAS
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA40100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA40200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA40300.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA40400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA40500.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA40600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA40700.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA40800.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA40900.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41000.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41300.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41400.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA414009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41430.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41700.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41800.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41900.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA42100.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA421009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA49100.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA491009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA49200.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA492009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA49300.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA493009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA49500.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA49500.toString(),new Boolean[]{FALSE,TRUE});
	}
	
	static{ 	// ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA159100.toString(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150010.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150020.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150030.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150040.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150050.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150060.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150070.toString(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA159200.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA1592009.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150080.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150090.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150110.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150120.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150130.toString(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA159300.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA1593009.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA159400.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA1594009.toString(),new Boolean[]{FALSE,TRUE});
	

	}
	
	static{ 	// ESTADO TOTAL DE CAMBIOS EN EL PATRIMONIO NETO
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511019.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511029.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511039.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511049.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511059.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511069.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511079.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511089.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511099.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511109.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511119.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511129.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511139.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2512019.toString(),new Boolean[]{TRUE,FALSE});		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2512139.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2513019.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2513139.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514019.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514029.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514039.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514049.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514059.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514069.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514079.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514089.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514099.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514109.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514119.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514129.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514139.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2515019.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2515139.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516019.toString(), new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516029.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516039.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516049.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516059.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516069.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516079.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516089.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516099.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516109.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516119.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516129.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516139.toString(), new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1528019.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1528139.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530019.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530029.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530039.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530049.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530059.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530069.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530079.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530089.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530099.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530119.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530129.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1530139.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524019.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524029.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524039.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524049.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524059.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524069.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524079.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524089.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524099.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524109.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524119.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524129.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524139.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2517139.toString(), new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2518139.toString(), new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2526139.toString(), new Boolean[]{FALSE, TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2531139.toString(), new Boolean[]{FALSE, TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2532139.toString(), new Boolean[]{FALSE, TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251101.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251102.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251103.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251104.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251105.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251106.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251107.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251108.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251109.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251110.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251111.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251112.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251113.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251201.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251213.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251301.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251313.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251401.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251402.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251403.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251404.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251405.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251406.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251407.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251408.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251409.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251410.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251411.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251412.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251413.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251501.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251513.toString(),new Boolean[]{FALSE,TRUE});

		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP152801.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP152813.toString(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153001.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153002.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153003.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153004.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153005.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153006.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153007.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153008.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153009.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153011.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153012.toString(),new Boolean[]{FALSE,TRUE});		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP153013.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251601.toString(), new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251602.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251603.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251604.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251605.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251606.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251607.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251608.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251609.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251610.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251611.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251612.toString(), new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251613.toString(), new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252401.toString(),new Boolean[]{TRUE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252402.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252403.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252404.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252405.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252406.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252407.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252408.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252409.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252410.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252411.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252412.toString(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252413.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252501.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252513.toString(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251713.toString(), new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251813.toString(), new Boolean[]{FALSE, TRUE});
	
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252613.toString(), new Boolean[]{FALSE, TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA253113.toString(), new Boolean[]{FALSE, TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA253213.toString(), new Boolean[]{FALSE, TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP152713.toString(), new Boolean[]{FALSE, TRUE});
	
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1527139.toString(), new Boolean[]{FALSE, TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP152913.toString(), new Boolean[]{FALSE, TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNP1529139.toString(), new Boolean[]{FALSE, TRUE});
	
	}
}

