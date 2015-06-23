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
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA391000.getName(),new Boolean[]{FALSE,FALSE});
	}

	static { // APARTADO 5
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592001.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592031.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592041.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592081.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592091.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA592131.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920019.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920319.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920419.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920819.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5920919.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA5921319.getName(),new Boolean[]{TRUE,FALSE});
	}
	
	static{ // APARTADO 6
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193201.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932019.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193202.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932029.getName(),new Boolean[]{FALSE,TRUE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193211.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932119.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193213.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932139.getName(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193221.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932219.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193223.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932239.getName(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193231.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932319.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193232.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932329.getName(),new Boolean[]{FALSE,TRUE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA6193243.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA61932439.getName(),new Boolean[]{FALSE,TRUE});
		
	}
	
	static{ // APARTADO 7
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA794301.getName(),new Boolean[]{FALSE,TRUE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA794302.getName(),new Boolean[]{FALSE,TRUE});
	}
	
	static{ // APARTADO 10

	}
	
	static{ // APARTADO 11

	}
	
	static{ // APARTADO 12

	}
	
	static{ // APARTADO 13

	}
	
	static{ // APARTADO 14

	}
	
	static{ // APARTADO 15

	}
}

