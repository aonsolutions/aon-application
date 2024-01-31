package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OCRLine implements Serializable {

    private static final long serialVersionUID = 317117670663946830L;
	
    private String text;
    private OCRWord[] words; 
    private BigDecimal[] boundingBox ;
    
    //private OCRAppearance appearance;
    
    
    public OCRLine setWords(OCRWord[] words) {
	this.words = words;
	return this;
    }
    
    public Optional<OCRWord[]> getWords() {
	return Optional.ofNullable(words);
    }
    
    public Optional<BigDecimal[]> getBoundingBox() {
	return Optional.ofNullable(boundingBox);
    }
    
    public OCRLine setBoundingBox(BigDecimal[] boundingBox) {
	this.boundingBox = boundingBox;
	return this;
    }
	
    public Optional<String> getText() {
	return Optional.ofNullable(text);
    }
    
    public OCRLine setText(String text) {
	this.text = text;
	return this;
    }
	
}
