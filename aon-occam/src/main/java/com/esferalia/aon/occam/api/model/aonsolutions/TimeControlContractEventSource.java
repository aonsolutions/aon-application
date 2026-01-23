package com.esferalia.aon.occam.api.model.aonsolutions;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TimeControlContractEventSource {
	FESTIVE,
	HOLIDAY,
	CONTRACT
	;

	private TimeControlContractEventSource() {
	
	}
	
	public static TimeControlContractEventSource safeValueOf(String value) {
		if(AonStringUtils.isBlank(value)) return CONTRACT;
		for (TimeControlContractEventSource tcg : TimeControlContractEventSource.values()) {
			if(tcg.name().equalsIgnoreCase(value)) {
				return tcg;
			}
		}
		return TimeControlContractEventSource.CONTRACT;
	}
}
