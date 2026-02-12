package com.esferalia.aon.occam.api.model;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum EnterpriseDataNames {

	// INVOICE COMMUNICATION CONFIGURATION
	
	ICC_ADMINISTRATION,
	ICC_LROE,
	ICC_SIF,
	ICC_NO_SIF,
	ICC_SII,
	ICC_TBAI,
	ICC_SERES,
	ICC_EMAIL,
	ICC_VERIFACTU,
	ICC_NO_VERIFACTU,
	ICC_FACTURAE, 
	
	// INVOFOX COUNTERS
	INVOFOX, 
	
	// PAYROLL 
	PAY_authorization_key_PAY
	;

	public static Optional<EnterpriseDataNames> safeValueOf(String name) {
		if (name == null) return Optional.empty();
		return AonCollectionUtils.stream(EnterpriseDataNames.values())
			.filter(e -> AonStringUtils.equalsIgnoreCase( e.name() , name))
			.findFirst();
	}
}
