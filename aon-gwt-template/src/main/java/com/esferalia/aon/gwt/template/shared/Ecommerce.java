package com.esferalia.aon.gwt.template.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum Ecommerce implements IsSerializable{
	
	AMAZON("Amazon"),
	EBAY("Ebay"),
	GENERIC("Generico");

	private String name;
	
	private Ecommerce(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String getOrdinalStr(){
		Integer ordinal = ordinal();
		return ordinal.toString();
	}
}
