package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.LinkedHashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;

public class D2Compute {

	public static Map<String,String> COMPUTE_MAP = new LinkedHashMap<String,String>();
	
	
	static {	// BALANCE: ACTIVO
		COMPUTE_MAP.put(D2DepositHeaderKey.BA111000.getCode(),"Q11100+Q11200+Q11300+Q11400+Q11500+Q11600+Q11700");
		COMPUTE_MAP.put(D2DepositHeaderKey.BA112000.getCode(),"Q12100+Q12200+Q12300+Q12400+Q12500+Q12600+Q12700");

	}

}
