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
	,MOD123("mod123")
	,PAYSHEET("paysheet");
	
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

	public static AttachType getAttachType(String name){
		if(name.equals(REGISTRY.getName()))
			return REGISTRY;
		else if(name.equals(CONTRACT.getName()))
			return CONTRACT;
		else if(name.equals(ITEM.getName()))
			return ITEM;
		else if(name.equals(INVOICE.getName()))
			return INVOICE;
		else if(name.equals(OFFER.getName()))
			return OFFER;
		else if(name.equals(PAYROLL.getName()))
			return PAYROLL;
		else if(name.equals(PROJECT.getName()))
			return PROJECT;
		else if(name.equals(SEPE.getName()))
			return SEPE;
		else if(name.equals(MOD111.getName()))
			return MOD111;
		else if(name.equals(MOD115.getName()))
			return MOD115;
		else if(name.equals(MOD123.getName()))
			return MOD123;
		return null;
	}
	
	public Boolean isModel(){
		return this.equals(MOD111) || this.equals(MOD115) || this.equals(MOD123);
	}
}
