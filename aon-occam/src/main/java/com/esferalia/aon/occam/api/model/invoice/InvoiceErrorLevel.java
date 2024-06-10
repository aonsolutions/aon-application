package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

public enum InvoiceErrorLevel implements Serializable{
	INF ("INFO."), 
	WRN ("AVISO"), 
	ERR ("ERROR")
	;
	
	private  String label;
	private InvoiceErrorLevel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
}