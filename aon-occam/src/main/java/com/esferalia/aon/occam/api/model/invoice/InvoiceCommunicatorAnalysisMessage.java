package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

public class InvoiceCommunicatorAnalysisMessage implements Serializable {
	
	private static final long serialVersionUID = -2945834320797041023L;
	
	private InvoiceErrorLevel level;
	private String message;
	
	public InvoiceErrorLevel getLevel() {
		return level;
	}
	public InvoiceCommunicatorAnalysisMessage setLevel(InvoiceErrorLevel level) {
		this.level = level;
		return this;
	}
	
	public String getMessage() {
		return message;
	}
	public InvoiceCommunicatorAnalysisMessage setMessage(String message) {
		this.message = message;
		return this;
	}
	
	
	

}
