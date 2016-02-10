package com.esferalia.aon.occam.api.model.attachment;

public enum AttachType {
	REGISTRY("registry")
	,CONTRACT("contract")
	,ITEM("item")
	,INVOICE("invoice")
	,OFFER("offer")
	,PAYROLL("payroll")
	,PROJECT("project")
	,SEPE("sepe");
	
	private String name;
	
	private AttachType(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
}
