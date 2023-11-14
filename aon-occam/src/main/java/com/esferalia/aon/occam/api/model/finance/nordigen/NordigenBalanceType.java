package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.util.Arrays;

public enum NordigenBalanceType {
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
	
	private NordigenBalanceType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public static NordigenBalanceType getByValue(String value) {
		return Arrays.stream(NordigenBalanceType.values())
				.filter(bt -> bt.getValue().equals(value))
				.findFirst()
				.orElse(null);
	}
}
