package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum TaxType implements Serializable {

	  UNKNOWN (" ---- ")
	, VAT("I.V.A.")
	, RETENTION("I.R.P.F.")
	;

	private String name;

	private TaxType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public byte value() {
		return (byte) ordinal();
	}

	public static TaxType safeValueOf(Byte i) {
		if (i == null)
			return null;
		return safeValueOf(i.intValue());
	}

	public static TaxType safeValueOf(Integer i) {
		if (i == null)
			return null;
		if (i < 0 || i >= TaxType.values().length)
			return null;
		return TaxType.values()[i];
	}

}