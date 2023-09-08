package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum OCRType implements Serializable {
	 invoice
	,deliveryNote
	,promissoryNote
	,ticket
	,supplyInvoice
	;

	public static Optional<OCRType> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
}
