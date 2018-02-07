package com.esferalia.aon.occam.api.model.fiscal.modules;

import java.io.Serializable;

public enum ModuleInfoType implements Serializable {
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