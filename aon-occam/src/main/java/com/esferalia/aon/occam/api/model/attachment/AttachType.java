package com.esferalia.aon.occam.api.model.attachment;

import java.io.Serializable;

public enum AttachType  implements Serializable {
	
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
	,PAYSHEET("paysheet")
	,DATA("data")
	,RAWDOC("rawdoc");
	
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
		if(name.equalsIgnoreCase(REGISTRY.getName()))
			return REGISTRY;
		else if(name.equalsIgnoreCase(CONTRACT.getName()))
			return CONTRACT;
		else if(name.equalsIgnoreCase(ITEM.getName()))
			return ITEM;
		else if(name.equalsIgnoreCase(INVOICE.getName()))
			return INVOICE;
		else if(name.equalsIgnoreCase(OFFER.getName()))
			return OFFER;
		else if(name.equalsIgnoreCase(PAYROLL.getName()))
			return PAYROLL;
		else if(name.equalsIgnoreCase(PROJECT.getName()))
			return PROJECT;
		else if(name.equalsIgnoreCase(SEPE.getName()))
			return SEPE;
		else if(name.equalsIgnoreCase(MOD111.getName()))
			return MOD111;
		else if(name.equalsIgnoreCase(MOD115.getName()))
			return MOD115;
		else if(name.equalsIgnoreCase(MOD123.getName()))
			return MOD123;
		else if(DATA.getName().equalsIgnoreCase(name))
			return DATA;
		else if(RAWDOC.getName().equalsIgnoreCase(name))
			return RAWDOC;
		return null;
	}
	
	public Boolean isModel(){
		return this.equals(MOD111) || this.equals(MOD115) || this.equals(MOD123);
	}
}
