package com.esferalia.aon.occam.api.model.finance.nordigen;

public enum NordigenCashAccountType {
	CACC,
	CASH,
	CISH,
	COMM,
	CPAC,
	LLSV,
	LOAN,
	MGLD,
	MOMA,
	NREX,
	ODFT,
	ONDP,
	OTHR,
	SACC,
	SLRY,
	SVGS,
	TAXE,
	TRAN,
	TRAS;
	
	public static NordigenCashAccountType safeValueOf(String value) {
		try {
			return NordigenCashAccountType.valueOf(value);
		} catch (NullPointerException | IllegalArgumentException e) {
			return null;
		}
	}
}
