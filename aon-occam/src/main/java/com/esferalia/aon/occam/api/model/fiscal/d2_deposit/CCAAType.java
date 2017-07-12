package com.esferalia.aon.occam.api.model.fiscal.d2_deposit;

public enum CCAAType {
	
	ABREVIATE("Abreviado"),
	PYMES("Pymes"),
	NORMAL("Normal"),
	MIXED("Normal"),		
	CONSOLIDATED("Consolidado");
	
	private String name;
	
	private CCAAType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

}
