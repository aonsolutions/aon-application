package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum OCRUnit implements Serializable {
	inch
	,pixel;

	public static Optional<OCRUnit> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
}
