package com.esferalia.aon.occam.api.model.finance;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceDataName {

	INVOFOX_ID,
	MD5,
	TBAI_ID,
	TBAI_URL,
	THIRD_PART,
	VERIFACTU_HUELLA,
	VERIFACTU_QR,
	 ;
	
	public String getValue() {
		return toString();
	}
	
	public String getValue(int i) {
		return getValue() + "_" + i;
	}

	public static Optional<InvoiceDataName> safeValueOf(String value) {
		if (AonStringUtils.isEmpty(value)) return Optional.empty(); 
		return AonCollectionUtils.stream(values())
			.filter(idn -> AonStringUtils.equals(idn.getValue(), value))
			.findFirst();
	}

}
