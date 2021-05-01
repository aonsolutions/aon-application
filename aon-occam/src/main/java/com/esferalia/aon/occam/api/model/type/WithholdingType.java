package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum WithholdingType implements Serializable {

	 PROFESSIONAL("Profesional")
	,RENTING("Arrendamiento")
	,MOVABLE_CAPITAL("Cap. Mobiliario")
	,FARMER("Agricultura")
	,TRANSPORT_OPERATOR("Transpor. y Asim.")	
	;
	private String description;
	
	private WithholdingType(String description){
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public byte value() {
		return (byte) ordinal();
	}	

	public static WithholdingType safeValueOf(Byte i) {
		if (i == null)
			return null;
		return safeValueOf(i.intValue());
	}

	public static WithholdingType safeValueOf(Integer i) {
		if (i == null)
			return null;
		if (i < 0 || i >= WithholdingType.values().length)
			return null;
		return WithholdingType.values()[i];
	}
	
	public static WithholdingType safeValueOf(String str) {
		if("IRPF_PROF".equalsIgnoreCase(str)) {
			return PROFESSIONAL;
		}
		if("IRPF_ALQ".equalsIgnoreCase(str)) {
			return MOVABLE_CAPITAL;
		}
		if("IRPF_AGRI".equalsIgnoreCase(str)) {
			return FARMER;
		}
		for (WithholdingType rs : values()) {
			if(rs.name().equalsIgnoreCase(str) || rs.getDescription().equalsIgnoreCase(str))
				return rs;
		}
		return PROFESSIONAL;
	}
}

