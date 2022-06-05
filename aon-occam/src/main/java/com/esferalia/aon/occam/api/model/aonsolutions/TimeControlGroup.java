package com.esferalia.aon.occam.api.model.aonsolutions;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TimeControlGroup {
	DAY,
	WEEK,
	MONTH,
	YEAR;

	private TimeControlGroup() {
	
	}
	
	public static TimeControlGroup safeValueOf(String value) {
		if(AonStringUtils.isBlank(value)) return DAY;
		for (TimeControlGroup tcg : TimeControlGroup.values()) {
			if(tcg.name().equalsIgnoreCase(value)) {
				return tcg;
			}
		}
		return TimeControlGroup.DAY;
	}
}
