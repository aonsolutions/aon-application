package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

public class InvoiceError implements Serializable {
	
	private static final long serialVersionUID = 1588476271729340025L;
	
	private String code;
	private InvoiceErrorLevel level;
	private InvoiceErrorContext context;
	private String message;

	public InvoiceError() {
		
	}
	public InvoiceError(InvoiceErrorContext context,InvoiceErrorLevel level,String code,String message) {
		this.context = context; 
		this.code = code;
		this.level = level;
		this.message = message;
	}
	public InvoiceErrorContext getContext() {
		return context;
	}
	public InvoiceError setContext(InvoiceErrorContext context) {
		this.context = context;
		return this;
	}
	
	public String getCode() {
		return code;
	}
	public InvoiceError setCode(String code) {
		this.code = code;
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
	
	public boolean canBeFixed() {
		return this.level != null && (this.level == InvoiceErrorLevel.ERR || this.level == InvoiceErrorLevel.WRN);
	}
	
}