package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.util.Arrays;

public enum NORDIGEN_BALANCE_TYPE {
	CLOSING_BOOKED("closingBooked"),
	EXPECTED("expected"),
	OPENING_BOOKED("openingBooked"),
	INTERIM_AVAILABLE("interimAvailable"),
	FORWARD_AVAILABLE("forwardAvailable"),
	INTERIM_BOOKED("interimBooked"),
	OPENING_AVAILABLE("openingAvailable"),
	PREVIOUSLY_CLOSED_BOOKED("previouslyClosedBooked"),
	CLOSING_AVAILABLE("closingAvailable"),
	INFORMATION("information")
	;
	
	private String value;
	
	private NORDIGEN_BALANCE_TYPE(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public static NORDIGEN_BALANCE_TYPE getByValue(String value) {
		return Arrays.stream(NORDIGEN_BALANCE_TYPE.values())
				.filter(bt -> bt.getValue().equals(value))
				.findFirst()
				.orElse(null);
	}
}
