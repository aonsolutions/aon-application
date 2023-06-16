package net.aonsolutions.infovox.model;

import java.math.BigDecimal;
import java.util.Optional;

public class OCRBoolean {
	
	private Boolean value;
	private BigDecimal confidence;
	
	public Optional<Boolean> getValue() {
		return Optional.ofNullable(value);
	}
	public OCRBoolean setValue(Boolean value) {
		this.value = value;
		return this;
	}
	
	public Optional<BigDecimal> getConfidence() {
		return Optional.ofNullable(confidence);
	}
	public OCRBoolean setConfidence(BigDecimal confidence) {
		this.confidence = confidence;
		return this;
	}
	
}
