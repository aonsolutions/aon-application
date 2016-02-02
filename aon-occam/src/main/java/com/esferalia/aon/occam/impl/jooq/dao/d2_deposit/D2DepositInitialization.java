package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.LinkedHashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;

public class D2DepositInitialization {
	public static Map<String,String> INITIALIZE_EXPRESSION_MAP_D2 = new LinkedHashMap<String,String>();
	
	static { // APLICACION DE RESULTADOS
		// BASE DE REPARTO
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391000.toString(),"sab({129})");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391001.toString(),"sab({120})");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391002.toString(),"sab({113})");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391003.toString(),"0.0");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391004.toString(),"sab({129,120,113})");

	
		//APLICACIÓN A
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391005.toString(),"sab({112})");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391006.toString(),"0.0");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391007.toString(),"sab({114})");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391008.toString(),"sab({113})");
		
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391009.toString(),"0.0");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391010.toString(),"sab({120})");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391011.toString(),"sab({121})");

		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391012.toString(),"sab({112,114,113,121,120})");

	}
	
}

