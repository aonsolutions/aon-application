package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

public class OCRNumber implements OCRValue<BigDecimal>, Serializable {
	
	private static final long serialVersionUID = 4356862400426081496L;
	
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
