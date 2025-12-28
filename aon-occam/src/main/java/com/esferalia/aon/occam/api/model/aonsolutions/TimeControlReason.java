package com.esferalia.aon.occam.api.model.aonsolutions;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TimeControlReason {
	IN_PERSON("Presencial", true, false),
	TELEWORKING("Teletrabajo", true, false),
	DISPLACED("Desplazado", true, false),
	
	WORK_BREAK("Descanso Laboral", false, true),
	MEDICAL_TESTS("Consulta / Pruebas Médicas", false, true),
	FAMILY_HOSPITALIZATION("Hospitalización Familia", false, true),
	INEXCUSABLE_LEGAL_FUNCTIONS("Func. Legales Inexcusables", false, true),
	BREASTFEEDING_BABY_CARE("Lactancia / Cuidado Bebé", false, true),
	FAMILY_REASON_EMERGENCIES("Motivos familiares / Urgencias", false, true),
	EXAMS_ACADEMIC_TESTS("Exámenes / Pruebas Académicas", false, true),
	OWN_ILLNESS_ACCIDENT("Enfermedad / Accidente Propio", false, true),
	OTHER_REASON("Otro", false, true),
	
	;
	
	private String description;
	private boolean isInReason;
	private boolean isPauseReason;
	
	private TimeControlReason(String description,  boolean isInReason, boolean isPauseReason) {
		this.description = description;
		this.isInReason = isInReason;
		this.isPauseReason = isPauseReason;
	}

	public Byte value() {
		return (byte) ordinal();
	}
	
	public String getDescription() {
		return description;
	}

	public boolean isInReason() {
		return isInReason;
	}

	public boolean isPauseReason() {
		return isPauseReason;
	}

	public static TimeControlReason safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TimeControlReason safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TimeControlReason.values().length) return null;
		return TimeControlReason.values()[i];
	}
	
	public static TimeControlReason safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (TimeControlReason rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
