package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public enum VATExemptionCause implements Serializable {
	
	E1("Exenta por el articulo 20"),
    E2("Exenta por el articulo 21"),
    E3("Exenta por el articulo 22"),
    E4("Exenta por el articulo 23 y 24"),
    E5("Exenta por el articulo 25"),
    E6("Exenta por otros");

    private final String description;

	private VATExemptionCause(String description){
		this.description = description;
	}

	public Byte value(){
		return (byte) ordinal();
	}
	
	public String getDescription() {
		return description;
	}
}