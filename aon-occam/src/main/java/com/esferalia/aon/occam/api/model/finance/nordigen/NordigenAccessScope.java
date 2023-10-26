package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.util.Arrays;

public enum NordigenAccessScope {
	BALANCES("balances"),
	DETAILS("details"),
	TRANSACTIONS("transactions");
	private String value;
	private NordigenAccessScope(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	public static NordigenAccessScope getByValue(String value) {
		return Arrays.stream(NordigenAccessScope.values())
				.filter(enConst -> enConst.getValue().equals(value))
				.findFirst()
				.orElse(null);
	}
}
