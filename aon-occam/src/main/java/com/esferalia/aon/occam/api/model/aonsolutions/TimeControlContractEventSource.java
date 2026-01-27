package com.esferalia.aon.occam.api.model.aonsolutions;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TimeControlContractEventSource {
	FESTIVE,
	HOLIDAYS,
	NON_WORKING,
	EFFECITVE_DAYS,
	REAL_JOURNEY,
	THEORIC_JOURNEY,
	WORKING,
	ERE,
	STRIKE,
	ERE_FZA,
	ERE_FZA_EXO,
	ABSENCE,
	INACTIVITY,
	PAID_LEAVE,
	PARTIALITY,
	IT
	;

	private TimeControlContractEventSource() {
	
	}
	
	public static TimeControlContractEventSource safeValueOf(String value) {
		if(AonStringUtils.isBlank(value)) return WORKING;
		for (TimeControlContractEventSource tcg : TimeControlContractEventSource.values()) {
			if(tcg.name().equalsIgnoreCase(value)) {
				return tcg;
			}
		}
		return TimeControlContractEventSource.WORKING;
	}
	
	public static TimeControlContractEventSource parseContractDataName(String name) {
		switch (name) {
			case "DIAS_VACACIONES" :
				return HOLIDAYS;
			case "NO_LABORABLE" :
				return NON_WORKING;
			case "DIAS_EFECTIVOS" :
				return EFFECITVE_DAYS;
			case "JORNADAS_REALES" :
				return REAL_JOURNEY;
			case "JORNADAS_TEORICAS" :
				return THEORIC_JOURNEY;
			case "LABORABLE" :
				return WORKING;
			case "COEFICIENTE_ERE" :
				return ERE;
			case "COEFICIENTE_HUELGA" :
				return STRIKE;
			case "COEFICIENTE_ERE_FZA" :
				return ERE_FZA;
			case "COEFICIENTE_ERE_FZA_EXONERADO" :
				return ERE_FZA_EXO;
			case "COEFICIENTE_AUSENCIA" :
				return ABSENCE;
			case "CAUSA_INACTIVIDAD" :
				return INACTIVITY;
			case "PERMISO_RETRIBUIDO" :
				return PAID_LEAVE;
			case "COEFICIENTE_PARCIALIDAD":
				return PARTIALITY;
			default :
				return WORKING;
		}
	}
}
