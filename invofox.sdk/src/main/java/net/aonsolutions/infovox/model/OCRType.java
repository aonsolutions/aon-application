package net.aonsolutions.infovox.model;

import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum OCRType {
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
