package com.esferalia.aon.occam.api.model.finance.nordigen;

public enum NORDIGEN_CASH_ACCOUNT_TYPE {
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
	
	public static NORDIGEN_CASH_ACCOUNT_TYPE safeValueOf(String value) {
		try {
			return NORDIGEN_CASH_ACCOUNT_TYPE.valueOf(value);
		} catch (NullPointerException | IllegalArgumentException e) {
			return null;
		}
	}
}
