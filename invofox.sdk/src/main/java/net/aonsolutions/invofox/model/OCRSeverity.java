package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum OCRSeverity implements Serializable {
	 processing
	,pendingCorrection
	,discarded
	,pendingDecission
	,rejected
	,approved
	,exported
	,error
	;
	
	public static Optional<OCRSeverity> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
}
