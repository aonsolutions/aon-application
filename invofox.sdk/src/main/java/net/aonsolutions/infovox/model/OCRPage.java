package net.aonsolutions.infovox.model;

import java.math.BigDecimal;
import java.util.Optional;

public class OCRPage {
	
	private BigDecimal width;
	private BigDecimal height;
	private OCRUnit unit;
	private BigDecimal angle;
	
	public Optional<BigDecimal> getWidth() {
		return Optional.ofNullable(width);
	}
	public OCRPage setWidth(BigDecimal width) {
		this.width = width;
		return this;
	}
	
	public Optional<BigDecimal> getHeight() {
		return Optional.ofNullable(height);
	}
	public OCRPage setHeight(BigDecimal height) {
		this.height = height;
		return this;
	}
	
	public Optional<OCRUnit> getUnit() {
		return Optional.ofNullable(unit);
	}
	public OCRPage setUnit(OCRUnit unit) {
		this.unit = unit;
		return this;
	}
	
	public Optional<BigDecimal> getAngle() {
		return Optional.ofNullable(angle);
	}
	public OCRPage setAngle(BigDecimal angle) {
		this.angle = angle;
		return this;
	}
	
	
}
