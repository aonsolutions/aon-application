package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum VatDeductionType implements Serializable {

	 WITH_RIGHT		("Con Drcho. Deduc.","CON Dcho.")
	,WITHOUT_RIGHT	("Sin Drcho. Deduc.","SIN Dcho.")
	,NON_TAXABLE	("No sujeto"		,"No Suj.")
	; 
	
	private String name;
	private String abbr;

	private VatDeductionType(String name, String abbr) {
		this.name = name;
		this.abbr = abbr;
	}

	public String getName() {
		return name;
	}
	public String getAbbr() {
		return abbr;
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