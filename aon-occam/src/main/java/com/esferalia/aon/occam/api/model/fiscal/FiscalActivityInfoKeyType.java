package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public enum FiscalActivityInfoKeyType implements Serializable {
	INFO,
	VAT_MODULE,
	IRPF_MODULE,
	VAT_INFO,
	IRPF_INFO,
	MODULE_DETAIL,
	MODULE;

	public Byte getValue() {
		return (byte) ordinal();
	}
		
}