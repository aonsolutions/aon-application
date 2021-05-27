package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum VatDeductionType implements Serializable {

	 WITH_RIGHT("Con Drcho. Deduc.")
	,WITHOUT_RIGHT("Sin Drcho. Deduc.")
	,NON_TAXABLE("No sujeto")
	; 
	
	private String name;

	private VatDeductionType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public byte value() {
		return (byte) ordinal();
	}

	public static VatDeductionType safeValueOf(Byte i) {
		if (i == null)
			return null;
		return safeValueOf(i.intValue());
	}

	public static VatDeductionType safeValueOf(Integer i) {
		if (i == null)
			return null;
		if (i < 0 || i >= VatDeductionType.values().length)
			return null;
		return VatDeductionType.values()[i];
	}
	
}