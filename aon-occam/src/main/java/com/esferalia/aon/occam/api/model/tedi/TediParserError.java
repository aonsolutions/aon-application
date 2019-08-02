package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;

public class TediParserError implements Serializable {
	
	private static final long serialVersionUID = 1588476271729340025L;
	
	private String code;
	private TediLevel level;
	private TediContext context;
	private String message;

	public TediParserError() {
		
	}
	public TediParserError(TediContext context,TediLevel level,String code,String message) {
		this.context = context; 
		this.code = code;
		this.level = level;
		this.message = message;
	}
	public TediContext getContext() {
		return context;
	}
	public TediParserError setContext(TediContext context) {
		this.context = context;
		return this;
	}
	
	public String getCode() {
		return code;
	}
	public TediParserError setCode(String code) {
		this.code = code;
		return this;
	}
	
	public TediLevel getLevel() {
		return level;
	}
	public TediParserError setLevel(TediLevel level) {
		this.level = level;
		return this;
	}

	public String getMessage() {
		return message;
	}
	public TediParserError setMessage(String message) {
		this.message = message;
		return this;
	}

}