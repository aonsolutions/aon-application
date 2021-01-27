package com.esferalia.aon.occam.api.model.aonsolutions;

public enum TimeControlGroup {
	DAY,
	WEEK,
	MONTH,
	YEAR;

	private TimeControlGroup() {
	
	}
	
	public static TimeControlGroup safeValueOf(String value) {
		for (TimeControlGroup tcg : TimeControlGroup.values()) {
			if(tcg.name().equalsIgnoreCase(value)) {
				return tcg;
			}
		}
		return TimeControlGroup.DAY;
	}
}
