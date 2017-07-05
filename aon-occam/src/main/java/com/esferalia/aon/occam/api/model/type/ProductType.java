package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum ProductType implements Serializable {
	
	LABOUR("Mano de Obra"),
    SERVICE("Servicio"),
	COMMERCIAL_PRODUCT("Product Comercial"),
	EXTERNAL_WORK("Trabajo Externo"),
	EXPENSE("Gasto"),
	PREPAYMENT("Suplidos"),
	INCREASE("Recargo"),
	AUXILIARY("Auxiliar");

	String name;
	private ProductType(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public byte value(){
		return (byte) this.ordinal();
	}
}
