package com.esferalia.aon.occam.api.model.type;

public enum CategoryType {
		
	REGISTRY_ATTACHMENT,
	ARTICLE;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName(){
		return this.toString();
	}
}
