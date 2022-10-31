package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.util.Arrays;

public enum NORDIGEN_ACCESS_SCOPES {
	BALANCES("balances"),
	DETAILS("details"),
	TRANSACTIONS("transactions");
	private String value;
	private NORDIGEN_ACCESS_SCOPES(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	public static NORDIGEN_ACCESS_SCOPES getByValue(String value) {
		return Arrays.stream(NORDIGEN_ACCESS_SCOPES.values())
				.filter(enConst -> enConst.getValue().equals(value))
				.findFirst()
				.orElse(null);
	}
}
