package com.esferalia.aon.occam.api.model.warehouse;

public enum CarrierPackingStatus {

	PENDING("Pendiente", "orangered"),
	ON_ROUTE("En ruta", "orange"),
	ON_BASCULA("En bascula", "green"),
	FINISHED("Finalizada", "darkslategray");
	
	private String name;
	private String color;
	
	private CarrierPackingStatus(String name, String color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public String getColor() {
		return color;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
}
