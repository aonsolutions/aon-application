package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OCRWord implements Serializable {

    private static final long serialVersionUID = 8404461332572383483L;
	
    private String text ;
    private BigDecimal confidence;
    private BigDecimal[] boundingBox ;
    
    
    public Optional<BigDecimal[]> getBoundingBox() {
	return Optional.ofNullable(boundingBox);
    }
    
    public OCRWord setBoundingBox(BigDecimal[] boundingBox) {
	this.boundingBox = boundingBox;
	return this;
    }
    
    public Optional<String> getText() {
	return Optional.ofNullable(text);
    }
    
    public OCRWord setText(String text) {
	this.text = text;
	return this;
    }

    public Optional<BigDecimal> getConfidence() {
	return Optional.ofNullable(confidence);
    }
    
    public OCRWord setConfidence(BigDecimal confidence) {
	this.confidence = confidence;
	return this;
    }
    
    
}
