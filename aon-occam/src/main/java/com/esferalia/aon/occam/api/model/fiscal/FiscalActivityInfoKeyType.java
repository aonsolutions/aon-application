package com.esferalia.aon.occam.api.model.fiscal;

public enum FiscalActivityInfoKeyType {
	INFO,
	VAT_MODULE,
	IRPF_MODULE,
	VAT_INFO,
	IRPF_INFO,
	MODULE_DETAIL,
	M311_DETAIL,
	M311_FARMER_DETAIL,
	MODULE;

	public Byte getValue() {
		return (byte) ordinal();
	}
}