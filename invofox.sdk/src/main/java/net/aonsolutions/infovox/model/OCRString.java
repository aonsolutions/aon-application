package net.aonsolutions.infovox.model;

import java.math.BigDecimal;
import java.util.Optional;

public class OCRString {
	
	private String value;
	private BigDecimal confidence;
	
	public Optional<String> getValue() {
		return Optional.ofNullable(value);
	}
	public OCRString setValue(String value) {
		this.value = value;
		return this;
	}
	
	public Optional<BigDecimal> getConfidence() {
		return Optional.ofNullable(confidence);
	}
	public OCRString setConfidence(BigDecimal confidence) {
		this.confidence = confidence;
		return this;
	}
	
	
}
