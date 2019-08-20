package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;

public enum TediLevel implements Serializable{
	INF ("INFO."), 
	WRN ("AVISO"), 
	ERR ("ERROR")
	;
	
	private  String label;
	private TediLevel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
}