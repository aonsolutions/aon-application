package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TaxType implements Serializable {

	  UNKNOWN (" ---- ", "")
	, VAT("I.V.A.", "IVA")
	, RETENTION("I.R.P.F.", "IRPF")
	;

	private String name;
	private String name2;

	private TaxType(String name, String name2) {
		this.name = name;
		this.name2 = name2;
	}

	public String getName() {
		return name;
	}
	
	public String getName2() {
		return name2;
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
	
	public static TaxType safeValueOf( String str ) {
		if(AonStringUtils.isBlank(str)) return UNKNOWN;
		for (TaxType rs : values()) {
			if(rs.name().equalsIgnoreCase(str) || rs.getName().equalsIgnoreCase(str) || rs.getName2().equalsIgnoreCase(str))
				return rs;
		}
		return UNKNOWN;
	}

	public static String safeValueOf(TaxType t) {
		return t == null ? UNKNOWN.name() : t.name();
	}

}