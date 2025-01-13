package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Optional;

import net.aonsolutions.occam.api.model.type.InvoiceErrorKey;
import net.aonsolutions.occam.api.model.type.InvoiceErrorLevel;

public class InvoiceError implements Serializable {
	
	private static final long serialVersionUID = 1588476271729340025L;
	
	private InvoiceErrorKey key;
	private InvoiceErrorLevel level;
	private Integer line;
	private String message;

	public InvoiceError() {
		
	}
	public InvoiceError(InvoiceErrorKey key,InvoiceErrorLevel level,String message) {
		this(key, level, message, null); 
	}
	public InvoiceError(InvoiceErrorKey key,InvoiceErrorLevel level,String message, Integer line) {
		this.key = key; 
		this.level = level;
		this.message = message;
		this.line = line;
	}
	public InvoiceErrorKey getKey() {
		return key;
	}
	public InvoiceError setContext(InvoiceErrorKey key) {
		this.key = key;
		return this;
	}
	
	public InvoiceErrorLevel getLevel() {
		return level;
	}
	public InvoiceError setLevel(InvoiceErrorLevel level) {
		this.level = level;
		return this;
	}

	public String getMessage() {
		return message;
	}
	public InvoiceError setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public Optional<Integer> getLine() {
		return Optional.ofNullable(line);
	}
	public InvoiceError setLine(Integer line) {
		this.line = line;
		return this;
	}
	
	public boolean canBeFixed() {
		return this.level != null && (this.level == InvoiceErrorLevel.ERR || this.level == InvoiceErrorLevel.WRN);
	}
	
}