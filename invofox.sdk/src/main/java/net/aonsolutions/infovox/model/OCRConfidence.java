package net.aonsolutions.infovox.model;

import java.util.Arrays;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum OCRConfidence {
	high
	,medium
	,low
	;

	public static Optional<OCRConfidence> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
}
