package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OCRPage implements Serializable {
	
	private static final long serialVersionUID = -4171680729048412819L;
	
	private BigDecimal width;
	private BigDecimal height;
	private OCRUnit unit;
	private BigDecimal angle;
	
	private Integer page;
	private OCRLine[] lines;
	
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
	
	public Optional<OCRLine[]> getLines() {
	    return Optional.of(lines);
	}
	
	public OCRPage setLines(OCRLine[] lines) {
	    this.lines = lines;
	    return this;
	}
	
	public Optional<Integer> getPage() {
		return Optional.ofNullable(page);
	}
	public OCRPage setPage(Integer page) {
		this.page = page;
		return this;
	}
	
	
	
}
