package com.esferalia.aon.occam.api.model.warehouse;

public enum CarrierPackingStatus {

	PENDING("Pendiente"),
	FINISHED("Finalizada"),
	ON_ROUTE("En ruta");
	
	private String name;
	
	private CarrierPackingStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
}
