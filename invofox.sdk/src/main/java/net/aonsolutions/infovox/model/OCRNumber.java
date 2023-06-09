package net.aonsolutions.infovox.model;

import java.math.BigDecimal;
import java.util.Optional;

public class OCRNumber {
	
	private BigDecimal value;
	private BigDecimal confidence;
	
	public Optional<BigDecimal> getValue() {
		return Optional.ofNullable(value);
	}
	public OCRNumber setValue(BigDecimal value) {
		this.value = value;
		return this;
	}
	
	public Optional<BigDecimal> getConfidence() {
		return Optional.ofNullable(confidence);
	}
	public OCRNumber setConfidence(BigDecimal confidence) {
		this.confidence = confidence;
		return this;
	}
	
}
