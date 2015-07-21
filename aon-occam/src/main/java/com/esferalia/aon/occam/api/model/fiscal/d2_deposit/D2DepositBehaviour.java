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
	
	static{
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111000.toString() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111300.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111500.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA111700.toString() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112000.toString() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112300.toString() ,new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112500.toString() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA112700.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA110000.toString() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2120000.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121000.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2121300.toString(),new Boolean[]{TRUE,FALSE});
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
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131000.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131200.toString(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131300.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131500.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2131700.toString(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232000.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232300.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232500.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2232700.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA2230000.toString(),new Boolean[]{TRUE,FALSE});

	}
	
	static{
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
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41400.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41430.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41600.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41700.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41800.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA41900.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA42100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA49100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA49200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA49300.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA49500.toString(),new Boolean[]{TRUE,FALSE});
	}
	
	static{
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA159100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150010.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150020.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150030.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150040.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150050.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150060.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150070.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA159200.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150080.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150090.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150100.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150110.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150120.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA150130.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA159300.toString(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA159400.toString(),new Boolean[]{TRUE,FALSE});
	}
	
	static{
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251101.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251201.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251301.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251401.toString(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251501.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251601.toString(),new Boolean[]{TRUE,FALSE});



		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252401.toString(),new Boolean[]{TRUE,FALSE});



		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511019.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2512019.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2513019.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514019.toString(),new Boolean[]{TRUE,FALSE});
	
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2515019.toString(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516019.toString(),new Boolean[]{TRUE,FALSE});

	


		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524019.toString(),new Boolean[]{TRUE,FALSE});
		

		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252501.toString(),new Boolean[]{TRUE,FALSE});
	}
}

