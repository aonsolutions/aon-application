package com.esferalia.aon.occam.api.model.attachment;

public enum AttachType {
	REGISTRY("registry")
	,CONTRACT("contract")
	,ITEM("item")
	,INVOICE("invoice")
	,OFFER("offer")
	,PAYROLL("payroll")
	,PROJECT("project")
	,SEPE("sepe")
	,MOD111("mod111")
	,MOD115("mod115")
	,MOD123("mod123");
	
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
	
	public Boolean isModel(){
		return this.equals(MOD111) || this.equals(MOD115) || this.equals(MOD123);
	}
}
