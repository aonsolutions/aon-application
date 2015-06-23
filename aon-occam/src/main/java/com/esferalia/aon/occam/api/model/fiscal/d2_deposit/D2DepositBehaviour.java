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
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095000.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095006.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095012.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095016.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095019.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA1095020.getName(),new Boolean[]{TRUE,FALSE});
	}
	
	static{ // APARTADO 11

	}
	
	static{ // APARTADO 12
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B97301.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B97331.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B97441.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B97501.getName(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B973019.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B973319.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B974419.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA12B975019.getName(),new Boolean[]{TRUE,FALSE});

	}
	
	static{ // APARTADO 13

	}
	
	static{ // APARTADO 14

	
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14199004.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14199014.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14199015.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294600.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294603.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294604.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294607.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294608.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositKey.MA14294613.getName(),new Boolean[]{TRUE,FALSE});
	}
	
	static{ // APARTADO 15

	}
	
	static{
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11100098.getName() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11110098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11120098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11130098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11140098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11150098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11160098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11170098.getName() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11200098.getName() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11210098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11220098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11230098.getName() ,new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11240098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11250098.getName() ,new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11260098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11270098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA11000098.getName() ,new Boolean[]{TRUE,FALSE});
		
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212000098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212100098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212110098.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212120098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212130098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212140098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212150098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212160098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212170098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212180098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212190098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212200098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA212300098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA213100098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA213110098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA213120098.getName(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA213130098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA213140098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA213150098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA213160098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA213170098.getName(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA223200098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA223210098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA223220098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA223230098.getName(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA223240098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA223250098.getName(),new Boolean[]{TRUE,FALSE});
	
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA223260098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA223270098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.BA223000098.getName(),new Boolean[]{TRUE,FALSE});

	}
	
	static{
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4010098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4020098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4030098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4040098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4050098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4060098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4070098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4080098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4090098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4100098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4110098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4120098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4130098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4140098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4143098.getName(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4160098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4170098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4180098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4190098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4210098.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4910098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4920098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4930098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PA4950098.getName(),new Boolean[]{TRUE,FALSE});
		
	}
	
	static{
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15910098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15001098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15002098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15003098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15004098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15005098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15006098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15007098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15920098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15008098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15009098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15010098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15011098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15012098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15013098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15930098.getName(),new Boolean[]{TRUE,FALSE});
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA15940098.getName(),new Boolean[]{TRUE,FALSE});
	
	
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251101.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251201.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251301.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251401.getName(),new Boolean[]{TRUE,FALSE});
		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251501.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA251601.getName(),new Boolean[]{TRUE,FALSE});



		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252401.getName(),new Boolean[]{TRUE,FALSE});



		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2511019.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2512019.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2513019.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2514019.getName(),new Boolean[]{TRUE,FALSE});
	
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2515019.getName(),new Boolean[]{TRUE,FALSE});

		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2516019.getName(),new Boolean[]{TRUE,FALSE});

	


		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA2524019.getName(),new Boolean[]{TRUE,FALSE});
		

		
		BEHAVIOUR_KEYS_MAP.put(D2DepositHeaderKey.PNA252501.getName(),new Boolean[]{TRUE,FALSE});
	}
}

