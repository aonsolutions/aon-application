package com.esferalia.aon.occam.api.model.aonsolutions;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TimeControlCause {
	DEFAULT,
	REST, //DESCANSO PERSONAL
	PERSONAL_AFFAIRS, // ASUNTOS PROPIOS
	EXCUSED_ABSENCE, //AUSENCIA JUSTIFICADA
	COMPANY_PERMIT, // PERMISO DE EMPRESA
	MEDICAL_LEAVE, // BAJA MEDICA
	VACATION, //VACACIONES
	;
	
	private TimeControlCause() {}

	public Byte value() {
		return (byte) ordinal();
	}
	
	public static TimeControlCause safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TimeControlCause safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TimeControlCause.values().length) return null;
		return TimeControlCause.values()[i];
	}
	
	public static TimeControlCause safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return DEFAULT;
		for (TimeControlCause rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return TimeControlCause.DEFAULT;
	}
}
