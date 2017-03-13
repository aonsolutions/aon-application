package com.esferalia.aon.occam.api.model.registry;

public enum RegistryItemStatus {
	ACTIVE,
	INTERESTED,
	REFUSED;
	
	private RegistryItemStatus() {
		
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
}
